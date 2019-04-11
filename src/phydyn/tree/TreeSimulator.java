package phydyn.tree;

/*
 * Structured tree simulation using the structured coalescent process
 * Birth and migration rates are pre-computed by the population model given as input. 
 * 
 * Under development
 */

import java.util.ArrayList;
import java.util.List;

import beast.core.BEASTObject;
import beast.core.Input;
import beast.core.Input.Validate;
import beast.evolution.tree.Tree;
import phydyn.model.PopModel;
import phydyn.model.TimeSeriesFGY;

public class TreeSimulator  {
	
	final static  double A_EPS = 0.00;
	final double Y_EPS = 1.E-12;

	private PopModel popModel;
	private double t0, t1;
	private String[] demes; 
	private double[] sampleHeights;
	private int[] sampleDemeIndex;
	
	private int numDemes, numTips, numInternal;
	private double maxSampleTime;
	private boolean finiteSizeCorrection;

	public TreeSimulator() {
		
	}
	
	// first version with pre-processed arrays
	// assume t1 = maxSampleTime
	public TreeSimulator(PopModel popModel, int[] sampleDemes, double[] sampleTimes, double[] sampleSizes) {
		this.popModel = popModel;
		numDemes = popModel.getNumStates();
		numTips = sampleDemes.length;
		if (numTips!= sampleTimes.length)
			throw new IllegalArgumentException("Mismatch in sample sizes (deme name and sample time)");
		if (numDemes != sampleSizes.length)
			throw new IllegalArgumentException("Problem with population sizes array");
		
		TimeSeriesFGY ts = popModel.getTimeSeries();
		t0 = popModel.getStartTime();
		t1 = popModel.getEndTime();
		
		// code repeated. 
		maxSampleTime = sampleTimes[0];
		for(int i= 1; i < numTips; i++) {
			if (sampleTimes[i] > maxSampleTime)
				maxSampleTime = sampleTimes[i];
		}
		
		if (Math.abs(maxSampleTime-t1) > 0.000001)
			throw new IllegalArgumentException("max sample time must match t1");
		
		finiteSizeCorrection = true;
		// n = numSamples nNoe = numInternal
		numInternal = numTips-1;
		// sort times
		
		
		
	}
	
	public List<Tree> simulate() {
		List<Tree> trees = new ArrayList<Tree>();
		
		
		
		
		return trees;
	}


}
