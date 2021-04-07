package phydyn.datafit;

import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;

import beast.core.Input;
import beast.core.Input.Validate;
import phydyn.model.TimeSeriesFGY;
import phydyn.util.DVector;
import phydyn.util.General;

public class SeroprevalenceLikelihood extends TrajectoryFit {
	public Input<Double> sigmaInput = new Input<>("sigma", "Standard deviation used for normal distribution",Validate.REQUIRED);
	public Input<Double> popSizeInput = new Input<>("popSize", "Population size used to compute seroprevalence",Validate.REQUIRED);
	public Input<String> yinfectionsInput = new Input<>("yinfections","Name of ODE variable denoting the total infected population",new String("infections"));

	
	public Input<Boolean> quietInput = new Input<>("quiet","Print seropevalences",new Boolean(true));
	public Input<Boolean> ignoreInput = new Input<>("ignore","Ignore SeroP likelihood",new Boolean(false));
	
	double popSize;
	double sigma;

	boolean quiet, ignore;
	double[] colTime;
	double[] colSP;
	
	//org.apache.commons.math.distribution.NormalDistribution dist;	
	NormalDistribution[] dist;
	String[] yNames;
	int[] idxs;
	int idxIl, idxIh, idxInfections;

	@Override
	public void initAndValidate() {
		super.initAndValidate();
		sigma = sigmaInput.get();
		popSize = popSizeInput.get();
		
		quiet = quietInput.get();
		ignore = ignoreInput.get();
		
		colTime = this.getColumnAsDouble(0);
		colSP = this.getColumnAsDouble(1);
		
		
		/*
		 * Distribution parameters
		 * Currently sigma. They could be passed in a parameter vector.
		 */
		//System.out.println("sigma="+sigma+"  popSize="+popSize);
		
		/*
		 * one distribution per datapoint
		 * We can use any distribution implemented by Apache Commons
		 */
		dist = new NormalDistribution[numrows];
		//System.out.println("--- initialising seroprevalence likelihood --");
		for(int i = 0; i < numrows; i++) {
			//System.out.println(colTime[i]+" - "+colSP[i]);
			final double dataSP = colSP[i];
			// one distribution per datapoint 
			dist[i] = new NormalDistributionImpl(dataSP, sigma);
		}
				
		/*
		 * Our likelihood needs deme values at particular time points.
		 * Seroprevalence requires Il and Ih
		 * We extract deme values by calculating first their position in the Ys array
		 * These positions coincide with their positions in the demes (String0 array
		 */
		
		//popModel.getDemesString(sep)
		int numDemes = popModel.numDemes;
		String[] demes = popModel.demeNames;
		System.out.print("deme names = ");
		for(int i = 0; i < numDemes; i++) {
			System.out.print(demes[i]+"  ");
		}		
		System.out.println(" ");
		System.out.print("non-deme names = ");
		for(int i = 0; i < popModel.numNonDemes; i++) {
			System.out.print(popModel.nonDemeNames[i]+"  ");
		}		
		System.out.println(" ");
		
		String ys = yinfectionsInput.get().trim();
		if (ys.length() < 1) {
			throw new IllegalArgumentException("Empty input: yinfections - Must enter variable names from population model");
		}
		yNames = ys.split("\\s+");
		idxs = new int[yNames.length];
		
		// Linking deme names to indices in Ys
		int nextidx;
		for(int i=0; i < yNames.length; i++) {
			nextidx = General.indexOf(yNames[i], popModel.demeNames);
			if (nextidx == -1) {
				nextidx = General.indexOf(yNames[i], popModel.nonDemeNames);
				if (nextidx == -1)
					throw new IllegalArgumentException("Couldn't find '" + yNames[i] + "' variable in popmodel");
				nextidx += popModel.demeNames.length;
			}
			System.out.println(yNames[i]+" index = "+nextidx);
			idxs[i] = nextidx;
		}
		

	}

	@Override
    public double calculateLogP() {
		
		TimeSeriesFGY ts = popModel.getTimeSeries();
		
		// Double-checking t in [t0,t1]
		//double t0 = popModel.getStartTime();
		//double t1 = popModel.getEndTime(); // popModel.getEndTime();
		//System.out.println("t0 = "+t0+" t1 = "+t1);
		
		int nps = ts.getNumTimePoints();  // reverse time
		
		// Get time using datapoint
		//double tst1 = ts.getTime(0);
		//double tst0 = ts.getTime(nps-1);
		//System.out.println("tst0 = "+tst0+" tst1 = "+tst1);
		
		
		int tpStart = nps/2;
		double infections=0 ;
			
		logP = 0;
		double sp=0;
		DVector Ys = null;
		int tp = tpStart;
		for(int i = 0; i < numrows; i++) {
			final double t = colTime[i];
			final double dataSP = colSP[i];  // mean
			tp = ts.getTimePoint(t, tp);			
			//final double tback = ts.getTime(tp);		
			//System.out.println("time="+t+" after search="+tback);
			infections = 0;
			Ys  = ts.getYall(tp);
			for(int j = 0; j < idxs.length; j++)
				infections += Ys.get(idxs[j]);
			//System.out.println("infected="+infected+" sp="+(infected/popSize)*100+"  data SP = "+dataSP);
			sp = (infections/popSize)*100;
			//dist = new NormalDistributionImpl(dataSP, sigma);
			//logP += Math.log(dist[i].density(sp));
			logP += -Math.log(sigma*Math.sqrt(2*Math.PI))- 0.5*(sp - dataSP)*(sp-dataSP)/(sigma*sigma);
		}
		if (!quiet)
			System.out.println("serop="+ sp +"  seroP logP = "+logP);	
		if (ignore)
			logP=0;
        return logP;
    }
	
	/*
	 * Critique
	 * Expressions made of deme/nonDeme terms need explicit coding e.g.
	 * infected = Ys.get(idxIh)+Ys.get(idxIl);
	 * This is hard-coded
	 * One option would be to introduce a definition in our popmodel eg
	 * infected = Il+Ih
	 * and make it public so it can be stored in our timeseries
	 * This requires updates to popModelODE and TimeSeriesFGY
	 * --. Working on this
	 */
	
	
	
}
