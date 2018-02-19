package phydyn.distribution;

import java.util.List;

import org.jblas.DoubleMatrix;

/**
 * @author Igor Siveroni
 */

public interface StateProbabilities {
	public StateProbabilities copy();
	public void init(int numNodes, int numStates); // usually tree.getNodeCount
	
	public void initExtantLineages();
		
	public int getNumExtant();
	public int[] getExtantLineages();

	public int getLineageIndex(int lineage);
	public int getLineageFromIndex(int idx);
	
	
	public List<Integer> getCoalescentIndices(STreeIntervals intervals, int interval);
	public List<DoubleMatrix> getCoalescentVectors(STreeIntervals intervals, int interval);
		
	public int addLineage(int nodeNr, DoubleMatrix pvec);
	
	public DoubleMatrix removeLineageNr(int nodeNr);
	
	public DoubleMatrix getRootProbs();
	
	public DoubleMatrix getExtantProbs(int lineage);
	public DoubleMatrix getExtantProbsFromIndex(int idx);
	public void printExtantProbabilities();
	
	public DoubleMatrix getAncestralProbs(int nodeNr);	
	public void storeAncestralProbs(int nodeNr, DoubleMatrix p, boolean makeCopy);
	public DoubleMatrix[] clearAncestralProbs();
	public void printAncestralProbabilities();

	public void mulExtantProbabilities(DoubleMatrix Q, boolean normalise);	
	public void mulExtantProbability(int idx, DoubleMatrix Q, boolean normalise);
	public void setMinP(double minP);
	
	public DoubleMatrix getLineageStateSum();	
	public void copyProbabilitesToArray(double[] p);
	
}
