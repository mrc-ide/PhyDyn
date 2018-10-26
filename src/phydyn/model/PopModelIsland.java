package phydyn.model;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import beast.core.Input;
import beast.core.Input.Validate;
import beast.core.Loggable;
import beast.core.parameter.RealParameter;
import phydyn.analysis.PopModelAnalysis;
import phydyn.analysis.XMLFileWriter;
import phydyn.util.DMatrix;
import phydyn.util.DVector;

public class PopModelIsland extends PopModel implements Loggable {

	public Input<List<String>> demeNamesInput = new Input<>(
            "demes",
            "Deme names",
            new ArrayList<>());
	
	public Input<RealParameter> NeInput = new Input<>(
			"Ne","Ne values",Validate.REQUIRED);
	
	public Input<RealParameter> migRatesInput = new Input<>(
			"migRates","Migration rates matrix",Validate.REQUIRED);
	
	public Input<RealParameter> xFInput = new Input<>("xF","xF");
	
	public Input<RealParameter> t0Input = new Input<>("t0","t0",Validate.REQUIRED);
	
	public Input<Double> t1Input = new Input<>("t1","t1");
	
	//public int numDemes, numNonDemes; // m = numDemes
	//public String[] demeNames, nonDemeNames;
	//boolean diagF;
	
	DMatrix F,G;
	DVector D;
	double[] yAll;
	TimeSeriesFGYConstant ts;
		
	TimeSeriesFGYConstant tsConstant;
	double t0,t1;
	boolean symmetric, endTimeDefined;

	@Override
	public void initAndValidate() {
		numDemes = demeNamesInput.get().size();
		if (numDemes<1) {
			System.out.println("Deme Names Missing");
			throw new IllegalArgumentException("Deme names missing");
		}
		demeNames = new String[numDemes];
		demeNamesInput.get().toArray(demeNames);
		nonDemeNames = new String[0];
		numNonDemes = 0;
		if (NeInput.get().getDimension()!=numDemes) {
			throw new IllegalArgumentException("Ne dimension must match number of demes");
		}
		symmetric = false;
		if (migRatesInput.get().getDimension() != numDemes*(numDemes-1)) {
			if ( 2*migRatesInput.get().getDimension() == numDemes*(numDemes-1)) {
				symmetric = true;
			} else {   
			throw new IllegalArgumentException("Incorrect number of migration rates. Must be:"+
					numDemes*(numDemes-1) + " or " + numDemes*(numDemes-1)/2 );
			}
		} 
		t0 = t0Input.get().getValue();
		if (t1Input.get()!=null) {
			t1 = t1Input.get();
			endTimeDefined = true;
		} else {
			endTimeDefined = false;
		}
		
		diagF = true;
		F = new DMatrix(numDemes, numDemes);
		G = new DMatrix(numDemes, numDemes);
		D = new DVector(numDemes);
		yAll = new double[numDemes+numNonDemes]; // numNonDemes is 0
	}
	
	@Override
	public String getName() {
		return this.getID();
	}
	
	public void printModel() {
		System.out.println("Island Model");
	}

	public String writeXML(XMLFileWriter writer, PopModelAnalysis analysis) throws IOException {
		writer.tabAppend("-- not implemented -- ");
		return this.getName();
	}
	
	protected void updateMatrices() {
		double xF;
		if (xFInput.get()==null) {
			xF = 10000;
		} else {
			xF = xFInput.get().getValue();
		}
		double[] Ne = NeInput.get().getDoubleValues();
		
		// Populate G matrix, column by column	
		for(int i=0; i < numDemes; i++) {
			F.put(i,i,(Ne[i]*xF)*(xF/2));
			yAll[i] = Ne[i]*xF;
			
		}
		// Migration matrix
		int mig_idx=0;
		double[] mig = migRatesInput.get().getDoubleValues();
		if (symmetric) {
			for(int i=0; i < numDemes; i++) {
				for(int j=i+1; j < numDemes; j++) {
					G.put(j, i, mig[mig_idx] * yAll[i]);
					G.put(i, j, mig[mig_idx] * yAll[j]);
					mig_idx++;
				}
			}
		} else {
			for(int i=0; i < numDemes; i++) {
				for(int j=0; j < numDemes; j++) {
					if (j!=i) {
						G.put(j, i, mig[mig_idx] * yAll[i]);
						mig_idx++;		
					}
				}
			}
		}
		
	}
	
	@Override
	public boolean isConstant() { return true; }
	
	@Override
	public boolean update() {
		if (!endTimeDefined) {
			throw new IllegalArgumentException("Population model: End time undefined");
		}
		tsConstant = new TimeSeriesFGYConstant(this,t0Input.get().getValue(), t1,2);
		updateMatrices();
		tsConstant.addFGY(0, F, G, yAll,D);
		tsConstant.reverse();
		return false;
	}

	@Override
	public TimeSeriesFGY getTimeSeries() {
		return tsConstant;
	}

	@Override
	public boolean hasEndTime() {
		return endTimeDefined;
	}

	@Override
	public double getEndTime() {
		if (!endTimeDefined) {
			throw new IllegalArgumentException("Population model: End time undefined");
		}
		return t1;
	}

	@Override
	public void setEndTime(double newt1) {
		t1 = newt1;
		endTimeDefined = true;
	}
	
	@Override
	public void unsetEndTime() {
		endTimeDefined = false;
	}

	@Override
	public void setStartTime(double newT0) {
		t0Input.get().setValue(newT0);
		t0=newT0;
	}
	
	@Override
	public double getStartTime() {
		return t0;
	}

	@Override
	public int getParamIndex(String paramName) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getYindex(String yName) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void updateRate(String rateName, double v) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean updateParam(String paramName, double paramValue) {
		// TODO Auto-generated method stub
		return false;
	}
	
	// not properly implemented
	@Override
	public boolean isParameter(String paramName) {
		return false;
	}
	
	@Override
	public String getParameterValue(String paramName) {
		return "not-implemented";
	}

	@Override
	public void init(PrintStream out) {
		// print headers (Ne's and migrations rates) - tab separated
		for(int i=0; i < numDemes; i++) {
			out.print(String.format("Ne_%s\t",demeNames[i]));
		}
		if (symmetric) {
			for(int row=0; row < numDemes; row++) {
				for(int col=0; col<numDemes; col++) {				
					out.print(String.format("bmig_%s_to_%s\t",demeNames[row],demeNames[col]));					
				}
			}
		} else {
			for(int row=0; row < numDemes; row++) {
				for(int col=0; col<numDemes; col++) {
					if (row!=col) {
						out.print(String.format("bmig_%s_to_%s\t",demeNames[row],demeNames[col]));
					}
				}
			}
		}
	}

	@Override
	public void log(int sample, PrintStream out) {
		double[] Ne = NeInput.get().getDoubleValues();
		for(int i=0; i < numDemes; i++) {
			out.print(Ne[i]+"\t");			
		}
		double[] mig = migRatesInput.get().getDoubleValues();
		for(int mig_idx=0; mig_idx < migRatesInput.get().getDimension(); mig_idx++) {
			out.print(mig[mig_idx]+"\t");
		}		
	}

	@Override
	public void close(PrintStream out) {
		// TODO Auto-generated method stub
		
	}

}
