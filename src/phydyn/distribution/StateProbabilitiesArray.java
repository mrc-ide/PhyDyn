package phydyn.distribution;

import java.util.ArrayList;
import java.util.List;

import org.jblas.DoubleMatrix;

import beast.evolution.tree.Node;

/**
 * @author Igor Siveroni
 */

public class StateProbabilitiesArray implements StateProbabilities {
	
	 /* Lineage and state probabilities management  */
	 // Extant Lineages
	protected int numNodes; // total number of Tree Nodes
	protected int[] extantLineages, extantIndex;
	protected DoubleMatrix[] extantProbs;  // assuming row-vectors
	protected int numExtant, numStates;
	protected DoubleMatrix[] ancestralProbs;

	public StateProbabilitiesArray() {
		numNodes = 0;
		numStates = 0;
	}
	
	public StateProbabilities copy() {
		return null;
	}
	


	@Override
	public void init(int numNodes, int numStates) {
		// default first
		this.numNodes = numNodes;
		this.numStates= numStates;
		extantLineages = new int[numNodes];
		extantIndex = new int[numNodes];
		extantProbs = new DoubleMatrix[numNodes];
		ancestralProbs=null;
        initExtantLineages();
	}
	
	@Override
	public void initExtantLineages() {
		numExtant = 0;
		for(int i = 0; i < numNodes; i++)
			extantIndex[i] = -1;
	}

	public int getNumExtant() {
		return numExtant;
	}
	
	public int[] getExtantLineages() { 
		int[] result;
		if (numExtant>0) result = new int[numExtant];
		else result = null;
		for(int i=0; i < numExtant; i++)
			result[i] = extantLineages[i];
		return result;
	}
	
	public int getLineageIndex(int lineage) {
		return extantIndex[lineage];
	}
	
	public int getLineageFromIndex(int idx) {
		return extantLineages[idx];
	}
	

	public List<Integer> getCoalescentIndices(STreeIntervals intervals, int interval) {
		List<Node> coalLines = intervals.getLineagesRemoved(interval);
		if (coalLines.size() > 2) return null;
  	
    	if (coalLines.size() > 2) {
			throw new RuntimeException("Unsupported coalescent at non-binary node");
		}
		int childIdx1 = extantIndex[coalLines.get(0).getNr()];
		int childIdx2 = extantIndex[coalLines.get(1).getNr()];
		
		if (childIdx1 == -1 || childIdx2 == -1){
	    	intervals.swap();
	    	coalLines = intervals.getLineagesRemoved(interval);
	    	childIdx1 = extantIndex[coalLines.get(0).getNr()];
	    	childIdx2 = extantIndex[coalLines.get(1).getNr()];
		}
		List<Integer> result = new ArrayList<Integer>();
		result.add(childIdx1);  result.add(childIdx2);
		return result;
	}
	
	public List<DoubleMatrix> getCoalescentVectors(STreeIntervals intervals, int interval) {
		List<Node> coalLines = intervals.getLineagesRemoved(interval);
		//if (coalLines.size() > 2) return null;
  	
    	if (coalLines.size() > 2) {
			throw new RuntimeException("Unsupported coalescent at non-binary node");
		}
    	//System.out.println("coalLines size="+coalLines.size());
		int childIdx1 = extantIndex[coalLines.get(0).getNr()];
		int childIdx2 = extantIndex[coalLines.get(1).getNr()];
		
		// if swap was already performed, this check is not needed
		if (childIdx1 == -1 || childIdx2 == -1){
	    	intervals.swap();
	    	coalLines = intervals.getLineagesRemoved(interval);
	    	childIdx1 = extantIndex[coalLines.get(0).getNr()];
	    	childIdx2 = extantIndex[coalLines.get(1).getNr()];
		}
		List<DoubleMatrix> result = new ArrayList<DoubleMatrix>();
		DoubleMatrix pvec1, pvec2;		
		pvec1 = this.getExtantProbsFromIndex(childIdx1);
		pvec2 = this.getExtantProbsFromIndex(childIdx2);
				
		result.add(pvec1);  result.add(pvec2);
		
		return result;		
	}
	
	

	@Override
	public int addLineage(int nodeNr, DoubleMatrix pvec) {
		// activeLineages.add(parentNode.getNr());
		extantLineages[numExtant] = nodeNr;
		extantIndex[nodeNr] = numExtant;
		extantProbs[numExtant++] = pvec;
		return numExtant-1;
	}

	@Override
	public DoubleMatrix removeLineageNr(int nodeNr) {
		int nodeIdx = extantIndex[nodeNr];
		if (nodeIdx==-1) {
			throw new IllegalArgumentException("Error removing lineage: Lineage not extant");
		}
		DoubleMatrix result = extantProbs[nodeIdx];
		
		if (nodeIdx == (numExtant-1)) { // added last (no swap needed)
			extantProbs[nodeIdx] = null;
			extantIndex[nodeNr] = -1;
			numExtant--;			
		} else {  // swap positions and probs
			int lastNr = extantLineages[numExtant-1];
			extantLineages[nodeIdx] = lastNr;
			extantProbs[nodeIdx] = extantProbs[numExtant-1];
			extantIndex[nodeNr] = -1;
			extantIndex[lastNr] = nodeIdx;
			numExtant--;
			extantProbs[numExtant] = null;
		}
		return result;
	}
	
	public DoubleMatrix getRootProbs() {
		// rootNr = numNodes
		if (this.ancestralProbs!=null) {
			return ancestralProbs[numNodes-1];
		}
		if (numExtant>0) {
			if (this.extantIndex[numNodes]!=-1)
				return extantProbs[extantIndex[numNodes]];
		}
		return null;
	}

	@Override
	public DoubleMatrix getExtantProbs(int nodeNr) {
		if (extantIndex[nodeNr] == -1)
			return null;
		else 
			return extantProbs[extantIndex[nodeNr]];
	}
	
	public DoubleMatrix getAncestralProbs(int nodeNr) {
		if (ancestralProbs == null)
			return null;
		return ancestralProbs[nodeNr];

	}
	
	public void storeAncestralProbs(int nodeNr, DoubleMatrix p, boolean makeCopy) {
		if (ancestralProbs == null)
			ancestralProbs = new DoubleMatrix[numNodes];
		if (makeCopy)
			ancestralProbs[nodeNr] = p.add(0);
		else
			ancestralProbs[nodeNr] = p;
	}
	
	public DoubleMatrix[] clearAncestralProbs() {
		DoubleMatrix[] processed = ancestralProbs;
		ancestralProbs = new DoubleMatrix[numNodes];
		return processed;
	}
	
	public void printAncestralProbabilities() {
		if (this.ancestralProbs==null)
			System.out.println("--NULL--");
		else {
			for(int i=0; i < numNodes; i++)
				if (this.ancestralProbs[i]!=null)
					System.out.println("node: "+i+" p="+ this.ancestralProbs[i]);
		}
	}
	
	@Override
	public DoubleMatrix getExtantProbsFromIndex(int idx) {
		return extantProbs[idx];	
	}
	
	@Override
	public void mulExtantProbabilities(DoubleMatrix Q, boolean normalise) {
		// Update extant probabilities: p*Q
		for (int l = 0; l < numExtant; l++) {		
			DoubleMatrix probs = (extantProbs[l]).mmul(Q); 
			if (normalise) {
				//extantProbs[l] = probs.div(probs.sum());
				probs.divi(probs.sum()); /* normalise */
				probs.maxi(0.0); /* clamp(0,1) */
				probs.mini(1.0);				
			} 
			extantProbs[l] = probs;
		}
	}
	
	public void mulExtantProbability(int idx, DoubleMatrix Q, boolean normalise) {
		DoubleMatrix probs = (extantProbs[idx]).mmul(Q); 
		if (normalise) {
			//extantProbs[l] = probs.div(probs.sum());
			probs.divi(probs.sum()); /* normalise */
			probs.maxi(0.0); /* clamp(0,1) */
			probs.mini(1.0);				
		} 
		extantProbs[idx] = probs;		
	}
	
	public void setMinP(double minP) {
		for (int l = 0; l < numExtant; l++) {		
			DoubleMatrix probs = extantProbs[l];
			probs.maxi(minP);
			probs.divi(probs.sum()); /* normalise */
			// extantProbs[l] = probs;
		}
	}
	
	@Override
	public DoubleMatrix getLineageStateSum() {
		DoubleMatrix sumAk = DoubleMatrix.zeros(numStates);
   		for (int lin = 0; lin < numExtant; lin++) {
   			sumAk = sumAk.add(extantProbs[lin]);
   		}
    	return sumAk;  
	}
	
	// note that size of p can be greater that numExtant
	public void copyProbabilitesToArray(double[] p) {
		DoubleMatrix probs;
		int idx=0;
		for(int i = 0; i < numExtant; i++) {
			probs = extantProbs[i];
			for(int j=0; j < numStates; j++) {
				p[idx++] = probs.get(j);
			}
		}
	}
	
    // Debug
    public void printExtantProbabilities() {
    	System.out.println("Extant Lineages Probabilities");
    	int lineage;
    	DoubleMatrix pvec;
    	for (int l = 0; l < numExtant; l++) {
    		lineage = this.getLineageFromIndex(l);
    		pvec = this.getExtantProbsFromIndex(l);
			System.out.println("lin: "+lineage+" probs "+ pvec);
		}
    }

}
