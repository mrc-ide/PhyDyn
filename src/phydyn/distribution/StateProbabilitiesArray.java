package phydyn.distribution;

import java.util.ArrayList;
import java.util.List;

import org.jblas.DoubleMatrix;

import beast.evolution.tree.Node;

public class StateProbabilitiesArray implements StateProbabilities {
	
	 /* Lineage and state probabilities management  */
	 // Extant Lineages
	int numNodes; // total number of Tree Nodes
	protected int[] extantLineages, extantIndex;
	protected DoubleMatrix[] extantProbs;
	protected int numExtant, numStates;

	public StateProbabilitiesArray() {
		numNodes = 0;
		numStates = 0;
	}

	@Override
	public void init(int numNodes, int numStates) {
		// default first
		this.numNodes = numNodes;
		this.numStates= numStates;
		extantLineages = new int[numNodes];
		extantIndex = new int[numNodes];
		extantProbs = new DoubleMatrix[numNodes];
		//if (this.numNodes != numNodes) {
		//	this.numNodes = numNodes;
		//	extantLineages = new int[numNodes];
		//	extantIndex = new int[numNodes];
		//	extantProbs = new DoubleMatrix[numNodes];
		//}
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
		pvec1 = this.getStateProbsFromIndex(childIdx1);
		pvec2 = this.getStateProbsFromIndex(childIdx2);
				
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
	public int removeLineageNr(int nodeNr) {
		int nodeIdx = extantIndex[nodeNr];
		if (nodeIdx == (numExtant-1)) {
			extantProbs[nodeIdx] = null;
			extantIndex[nodeNr] = -1;
			numExtant--;
			return 0;
		}
		int lastNr = extantLineages[numExtant-1];
		extantLineages[nodeIdx] = lastNr;
		extantProbs[nodeIdx] = extantProbs[numExtant-1];
		extantIndex[nodeNr] = -1;
		extantIndex[lastNr] = nodeIdx;
		numExtant--;
		extantProbs[numExtant] = null;
		return 0;
	}

	@Override
	public DoubleMatrix getStateProbs(int nodeNr) {
		 int i;
		 for(i=0; i < numExtant; i++)
			 if (extantLineages[i]==nodeNr) 
				 break;
		 if (i>= numExtant)
			 return null;
		 else
			 return extantProbs[i];	
	}
	
	@Override
	public DoubleMatrix getStateProbsFromIndex(int idx) {
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
   			//System.out.println(extantProbs[lin]);
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
    void printExtantProbabilities() {
    	System.out.println("Extant Lineages Probabilities");
    	int lineage;
    	DoubleMatrix pvec;
    	for (int l = 0; l < numExtant; l++) {
    		lineage = this.getLineageFromIndex(l);
    		pvec = this.getStateProbsFromIndex(l);
			System.out.println("lin: "+lineage+" probs "+ pvec);
		}
    }

}
