package phydyn.run;

import java.util.List;
import java.io.FileWriter;

import beast.core.Runnable;
import beast.core.Input;
import beast.core.Input.Validate;
import beast.evolution.tree.Tree;
import beast.evolution.tree.coalescent.TreeIntervals;
import phydyn.distribution.STreeDistribution;
import phydyn.distribution.STreeIntervals;
import phydyn.model.PopModelODE;
import phydyn.model.TimeSeriesFGY;
import phydyn.model.TimeSeriesFGYStd;


public class PhyDynTest extends Runnable {
	
	public Input<Tree> treeInput = new Input<Tree>("tree","Tree: main Input",Validate.REQUIRED);
	public Input<STreeIntervals> treeIntervalsInput = new Input<STreeIntervals>("treeIntervals","Tree Intervals of model",Validate.REQUIRED);
	//public Input<SI3Model> si3ModelInput = new Input<SI3Model>("epimodel","Sample SI3 model",Validate.REQUIRED);
	public Input<PopModelODE> modelODEInput = new Input<>(
			"model","Population Model");
	public Input<STreeDistribution> densityInput = new Input<>("density", "Tree density", Validate.REQUIRED);
	
	protected Tree tree;
	protected TreeIntervals treeIntervals; /* until otherwise (STreeIntervals) needed */
	//protected SI3Model si3Model;
	protected PopModelODE model=null;
	
	
	@Override
	public void initAndValidate() { 
		System.out.println("Initializing SI3R model...");
				
		tree = treeInput.get();
		treeIntervals = treeIntervalsInput.get();
		//si3Model = si3ModelInput.get();
		if (modelODEInput.get()!=null) { model = modelODEInput.get(); }
		
		/* Inspect Tree using TreeInterface */
		int ntips, ninternal, nnodes;
		ntips = tree.getLeafNodeCount();
		ninternal = tree.getInternalNodeCount();
		nnodes = tree.getNodeCount();
		System.out.println("ID : "+tree.getID());
		System.out.println("tips: "+ntips+" internal: "+ninternal+ " nnodes: "+nnodes);
		
		double[] intervals = treeIntervals.getIntervals(null);
		System.out.println("Num intervals ="+intervals.length);
		
	}
	
	@Override
	public void run() throws Exception {
		System.out.println("running...");
		double[] y0 = {1,0.01,0.01,3000};
		int steps;
		double t0,t1;
		double[] y1 = {0,0,0,0};
		t0=0.0;
		t1 = 50;
		steps = 11; // from 1 to 1000 - 999 dxs
		
		//si3Model.integrate(y0, t0, t1, steps, y1);
		
		//model.integrate();
		
		densityInput.get().calculateLogP();
		System.out.println("logP="+densityInput.get().getCurrentLogP());
		
		TimeSeriesFGY ts = model.getTimeSeries();
		//double[] points = ts.getTimePoints();
		double previous, min,max, stepSize;
		int numPoints = ts.getNumTimePoints();
		
		previous = ts.getTime(0);
		max=0.0; min=previous;
		for(int i = 1; i < numPoints; i++) {
			stepSize = (previous-ts.getTime(i));
			if (stepSize>max) max=stepSize;
			if (stepSize<min) min=stepSize;
			//System.out.println(points[i]+" - stepSize = "+ stepSize);
			previous = ts.getTime(i);
		}
		System.out.println("Number of points: "+numPoints);
		System.out.println("Stepsizes (min,max)=(" + min + ","+max+")");
				
		// timeTrajectory();
		//testLikelihood("beta0",0.0008,0.002,100,"hiv2beta0.log"); 
		
		System.out.println("Done");
	}
	
	public void timeTrajectory() {
		System.out.println("----------LOOP------");
		double lh, max_lh = Double.NEGATIVE_INFINITY, max_coef=0.0;
		
		long sum = 0;
		for(int i=0; i<10; i++) {	
			long startTime = System.nanoTime();
			for(double coef=0.3; coef < 0.7; coef += 0.001) {
				model.updateRate("gamma2", coef);
				model.update();
			}
			//System.out.println("--- MAX ----\ncoef: "+max_coef+" maxLH="+max_lh);
			long endTime = System.nanoTime();
			long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
			System.out.println("duration:"+duration/1000000.0);
			sum += duration/1000000.0;
		}
		System.out.println("Avg:"+sum/10.0);
				
		/* Timing results (milliseconds) 
		 * Computation of 400 trajectories/ 1000 integration steps, (t0,t1)=(0,50)
		 * iMAC: compiled: 1436 interpreted: 6284 (22% /4.37)
		 * UbuntuWork: 1300 interpreted: 5950 (21.8% / 4.57)
		 * ie more than four times faster in both cases
		 * However, trajectory computation only accounts for 5% of the likelihood computation
		 * Need to optimise likelihood computation to obtain better relative performace gains.
		 */
	}
	
	public void testLikelihood(String coefName, double lbound, double ubound, int numPoints, String fileName) throws Exception {
		double lh, max_lh = Double.NEGATIVE_INFINITY, max_coef=0.0;
		double stepSize;
		//long sum = 0;
		System.out.println(coefName+",logLH");
		
		FileWriter writer = new FileWriter(fileName);
		writer.append(coefName+",lh\n");
		
		stepSize = (ubound-lbound)/(numPoints-1);
		
		for(double coef=lbound; coef < ubound; coef += stepSize) {
			
			model.updateRate(coefName, coef);
			model.update();
			//long startTime = System.nanoTime();
			densityInput.get().calculateLogP();// throws exception
			//long endTime = System.nanoTime();
			//long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
			//sum += duration/1000000.0;
			//System.out.println("duration:"+duration/1000000.0);
			
			lh  = densityInput.get().getCurrentLogP();
			if (lh > max_lh) { max_lh = lh; max_coef = coef; }
			System.out.println(coef+ "," + lh);
			if ((new Double(lh)).compareTo(Double.NEGATIVE_INFINITY)==0) {
				writer.append(coef+","+"-Inf\n");
			} else {
				writer.append(coef+","+lh+"\n");
			}
		}
		System.out.println("\n--- MAX ----\ncoef: "+max_coef+" maxLH="+max_lh);
		writer.flush();
	    writer.close();
		//System.out.println("Total:"+sum);
		
	}

}
