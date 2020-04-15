package phydyn.loggers;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.jblas.DoubleMatrix;

import beast.core.CalculationNode;
import beast.core.Input;
import beast.core.Input.Validate;
import beast.core.Loggable;
import phydyn.model.PopModelODE;
import phydyn.model.TimeSeriesFGY;
import phydyn.model.TimeSeriesFGY.FGY;
import phydyn.model.TimeSeriesFGYStd;
import phydyn.util.DMatrix;
import phydyn.util.DVector;

public class TrajectoryLogger extends CalculationNode implements Loggable {
	
	final public Input<PopModelODE> popModelInput =  new Input<>("popModel",
	        "Population model to log.", Validate.REQUIRED);
	
	final public Input<Integer> frequencyInput = new Input<>("pointFrequency",
	        "Frequency between points in time series to be displayed");
	
	
	final public Input<String> ratesInput = new Input<>("logrates",
			"rates to log form birth and migration matrices");
	//final public Input<Integer> numPoints = new Input<>("numPoints",
	//        "Number of points in time series to be logged");
	
	// we could also add a point distance input parameter

	private PopModelODE popModel;
	private int frequency=1;
	// private boolean useFrequency=false, usePoints=false;
	private int numLogRates = 0;
	private char[] matrixName;
	private String[] logRateHeader;
	private int[] demeRow, demeColumn;
	
	public TrajectoryLogger() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initAndValidate() {
		popModel = popModelInput.get();		
		if (frequencyInput.get() != null) {
			frequency = frequencyInput.get();
		} 
		if (frequency < 1) frequency = 1;
		if (ratesInput.get()!=null) {
			processRatesString(ratesInput.get());
		}
		if (this.numLogRates > 0) {
			System.out.print("Logging rates: ");
			for(int i=0; i<numLogRates; i++) {
				System.out.print(this.logRateHeader[i]+ " ");
			}
			System.out.println(" ");
		}		
	}

	@Override
	public void init(PrintStream out) {
		out.print("t");
		for (int i = 0; i < popModel.yLength; i++) {
			out.print("\t"+popModel.yNames[i]);
		}
		if (numLogRates>0) {
			for(int i=0; i < numLogRates; i++) {
				out.print("\t"+logRateHeader[i]);
			}
		}
	}

	@Override
	public void log(int sample, PrintStream out) {
		TimeSeriesFGY timeseries = popModel.getTimeSeries();
		
		FGY fgy; // = ts.getFGY(0);
		DVector y; // = ySeries[0];
		DMatrix F,G;
		//int j;
		int idx = timeseries.getNumTimePoints() - 1;
		// The timeseries runs backwards in time
		// first - line
		fgy = timeseries.getFGY(idx);
		y = fgy.Yall;
		out.print(timeseries.getTime(idx)); // don't print sample number
		for(int j = 0; j < popModel.yLength; j++) {
			out.print("\t"+y.get(j));
		}
		if (numLogRates>0) {
			F = fgy.F; G = fgy.G;
			for(int j = 0; j < numLogRates; j++) {
				if (matrixName[j]== 'F') {
					out.print("\t"+F.get(demeRow[j], demeColumn[j]));
				} else {
					out.print("\t"+G.get(demeRow[j], demeColumn[j]));
				}
			}
		}
		out.println("");

		// other lines
		idx -= frequency;
		while (idx > 0) {
			fgy = timeseries.getFGY(idx);
			y =  fgy.Yall;  //ySeries[idx];
			out.print(sample+"\t"+timeseries.getTime(idx));
			for(int j = 0; j < popModel.yLength; j++) {
				out.print("\t"+y.get(j));
			}
			if (numLogRates>0) {
				F = fgy.F; G = fgy.G;
				for(int j = 0; j < numLogRates; j++) {
					if (matrixName[j]== 'F') {
						out.print("\t"+F.get(demeRow[j], demeColumn[j]));
					} else {
						out.print("\t"+G.get(demeRow[j], demeColumn[j]));
					}
				}
			}
			out.println("");
			idx -= frequency;
		}
		// last line - idx = 0
		fgy = timeseries.getFGY(0);
		y = timeseries.getFGY(0).Yall;
		out.print(sample+"\t"+timeseries.getTime(0));
		for(int j = 0; j < popModel.yLength; j++) {
			out.print("\t"+y.get(j));
		}
		if (numLogRates>0) {
			F = fgy.F; G = fgy.G;
			for(int j = 0; j < numLogRates; j++) {
				if (matrixName[j]== 'F') {
					out.print("\t"+F.get(demeRow[j], demeColumn[j]));
				} else {
					out.print("\t"+G.get(demeRow[j], demeColumn[j]));
				}
			}
		}
		out.println("");
	}

	@Override
	public void close(PrintStream out) {
		
	}
	
	void processRatesString(String ratesStr) {
		//System.out.println("Processing: "+ratesStr);
		// row = model.indexOf( model.demeNames, originName);
		String[] ratesArray = ratesStr.trim().split("\\s+");
		if (ratesArray.length == 0) {
			System.out.println("Warning: logrates parameter (TrajectoryLogger) empty");
			return;
		}
		// initlaize arrays
		int n = ratesArray.length;
		matrixName = new char[n];
		logRateHeader= new String[n];
		demeRow = new int[n];
		demeColumn = new int[n];
		for (String rateStr : ratesArray) {
			processRate(rateStr);
		}
	}
	
	boolean processRate(String rateStr) {
		List<String> tokens = parseRate(rateStr);
		if (tokens.size()!=6 ||
			!tokens.get(1).equals("(") ||
			!tokens.get(3).equals(",") ||
			!tokens.get(5).equals(")")
			) 
		{
			System.out.println("Warning (TrajectoryLogger): Incorrect lograte syntax "+ rateStr);
			return false;
		}
		String m = tokens.get(0);
		if (!m.equals("F")  && !m.equals("G") ) {
			System.out.println("Warning (TrajectoryLogger): Incorrect matrix name "+m);
			return false;
		}
		
		String deme = tokens.get(2).trim();
		int row = popModel.indexOf(popModel.demeNames, deme);
		if (row<0) {
			System.out.println("Warning (Trajectory Logger): Unknown deme name "+ deme);
			return false;
		}
		deme = tokens.get(4).trim();
		int column = popModel.indexOf(popModel.demeNames, deme);
		if (column<0) {
			System.out.println("Warning (Trajectory Logger): Unknown deme name "+ deme);
			return false;
		}
		matrixName[numLogRates] = m.charAt(0);
		logRateHeader[numLogRates] = rateStr;
		demeRow[numLogRates] = row;
		demeColumn[numLogRates] = column;
		numLogRates++;
		//System.out.println("Tokens for : "+rateStr+" "+tokens.size());
		//for(int i=0; i<tokens.size();i++) {
		//	System.out.println(tokens.get(i));
		//}		
		return true;
	}
	
	List<String> parseRate(String rateStr) {
		List<String> tokens = new ArrayList<String>();
		// syntax: ('F'|'G') '(' <deme> ',' <deme> ')'
		int firstIdx=0,idx=0;
		while(idx < rateStr.length()) {
			final char chr = rateStr.charAt(idx);
			if (chr=='(' || chr==')' || chr==',') {
				if (firstIdx < idx) {
					tokens.add(rateStr.substring(firstIdx, idx));					
				}
				tokens.add(rateStr.substring(idx,idx+1));
				idx++;
				firstIdx=idx;
			} else {
				idx++;
			}					
		}
		if (firstIdx < idx) {
			tokens.add(rateStr.substring(firstIdx, idx));
		}	
		return tokens;
	}
	

}
