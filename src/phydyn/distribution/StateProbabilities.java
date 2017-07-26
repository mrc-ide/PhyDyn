package phydyn.distribution;

import java.util.List;

import org.jblas.DoubleMatrix;

public interface StateProbabilities {
	
	public void init(int numNodes, int numStates); // ususally tree.getNodeCount
	
	public void initExtantLineages();
		
	public int getNumExtant();
	
	public int getLineageIndex(int lineage);
	public int getLineageFromIndex(int idx);
	
	
	public List<Integer> getCoalescentIndices(STreeIntervals intervals, int interval);
	public List<DoubleMatrix> getCoalescentVectors(STreeIntervals intervals, int interval);
		
	public int addLineage(int nodeNr, DoubleMatrix pvec);
	
	public int removeLineageNr(int nodeNr);
	
	public DoubleMatrix getStateProbs(int lineage);
	public DoubleMatrix getStateProbsFromIndex(int idx);
	

	public void mulExtantProbabilities(DoubleMatrix Q, boolean normalise);	
	public void mulExtantProbability(int idx, DoubleMatrix Q, boolean normalise);
	public void setMinP(double minP);
	
	public DoubleMatrix getLineageStateSum();
	
	public void copyProbabilitesToArray(double[] p);
	
}
