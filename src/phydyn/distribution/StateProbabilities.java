package phydyn.distribution;

import java.util.List;

import org.jblas.DoubleMatrix;

import phydyn.util.DMatrix;
import phydyn.util.DVector;

/**
 * @author Igor Siveroni
 */

public interface StateProbabilities {
	public StateProbabilities copy();
	public void init(int numNodes, int numStates); // usually tree.getNodeCount
	
		
	public int getNumExtant();
	public int[] getExtantLineages();

	public int getExtantProbs(DVector[] probs);
	public DVector[] getExtantProbs();
	
	public void getExtantProbabilities(int[] lineages, int numLineages, DVector[] coalVector);
		
	public DVector addLineage(int nodeNr);
	public DVector addLineage(int nodeNr, DVector pvec);
	public DVector addSample(int nodeNr, int sampleState);	
	public DVector removeLineage(int nodeNr);
	
	public DVector getRootProbs();
	
	public DVector getExtantProbs(int lineage);
	public void printExtantProbabilities();
	
	public DVector getAncestralProbs(int nodeNr);
	public void storeAncestralProbs(int nodeNr);
	public void storeAncestralProbs(int nodeNr, DVector p, boolean makeCopy);
	public DVector[] clearAncestralProbs();
	public void printAncestralProbabilities();

	//public void mulExtantProbabilities(DoubleMatrix Q, boolean normalise);
	public void mulExtantProbabilities(DMatrix Q, boolean normalise);
	
	// public void mulExtantProbability(int idx, DoubleMatrix Q, boolean normalise);
	public void setMinP(double minP);
	
	public DVector getLineageStateSum();
	public DVector getLineageSumSquares();
	public void copyProbabilitiesToArray(double[] p);
	public void copyProbabilitiesFromArray(double[] p);
	
}
