package phydyn.distribution;

import java.util.List;

import beast.evolution.tree.Node;
import phydyn.util.DMatrix;
import phydyn.util.DVector;

/**
 * @author Igor Siveroni
 */

public class StateProbabilitiesVectors implements StateProbabilities {
	
	 /* Lineage and state probabilities management  */
	 // Extant Lineages
	protected int numNodes; // total number of Tree Nodes
	protected int[] extantLineages;
	protected int[] extantIndex; // maps lineage to position in extant arrays
	protected int numExtant, numStates;
	protected DVector[] extantProbs, ancestralProbs; // now column vectors
	
	// auxiliary
	int[] pair = new int[2];

	public StateProbabilitiesVectors() {
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
		extantProbs = new DVector[numNodes];
		ancestralProbs=null;
        initExtantLineages();
	}
	
	// local 
	private void initExtantLineages() {
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
	
	public DVector[] getExtantProbs() {
		DVector[] result;
		if (numExtant>0) result = new DVector[numExtant];
		else result = null;
		for(int i=0; i < numExtant; i++)
			result[i] = extantProbs[extantIndex[extantLineages[i]]];
		return result;
	}
	
	public int getExtantProbs(DVector[] probs) {
		// Careful: read-only
		return numExtant;
	}
	
	@Override
	public void getExtantProbabilities(int[] lineages, int numLineages, DVector[] pVectors) {
		for(int l=0; l < numLineages; l++) {
			pVectors[l] = extantProbs[extantIndex[lineages[l]]];
		}
		
	}

	@Override
	public DVector addLineage(int nodeNr) {
		DVector p = new DVector();
		addLineage(nodeNr, p);
		return p;
	}
	
	@Override
	public DVector addLineage(int nodeNr, DVector pvec) {
		// activeLineages.add(parentNode.getNr());
		extantLineages[numExtant] = nodeNr;
		extantIndex[nodeNr] = numExtant;
		extantProbs[numExtant++] = pvec;
		return pvec;
	}
	
	public DVector addSample(int nodeNr, int sampleState) {
		DVector p = new DVector(numStates); // row-vector
		p.put(sampleState, 1.0);
		this.addLineage(nodeNr,p);
		return p;
	}

	@Override
	public DVector removeLineage(int nodeNr) {
		int nodeIdx = extantIndex[nodeNr];
		if (nodeIdx==-1) {
			throw new IllegalArgumentException("Error removing lineage: Lineage not extant");
		}
		DVector result = extantProbs[nodeIdx];
		
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
	
	public DVector getRootProbs() {
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
	public DVector getExtantProbs(int nodeNr) {
		if (extantIndex[nodeNr] == -1)
			return null;
		else 
			return extantProbs[extantIndex[nodeNr]];
	}
	
	public DVector getAncestralProbs(int nodeNr) {
		if (ancestralProbs == null)
			return null;
		return ancestralProbs[nodeNr];

	}
	
	// store current state prob at nodeNr. 
	public void storeAncestralProbs(int nodeNr) {
		DVector p = getExtantProbs(nodeNr);
		if (p==null)
			throw new IllegalArgumentException("Error storing Ancestral probs: lineage not extant");
		storeAncestralProbs(nodeNr,p,true); // make copy
	}
	
	public void storeAncestralProbs(int nodeNr, DVector p, boolean makeCopy) {
		if (ancestralProbs == null)
			ancestralProbs = new DVector[numNodes];
		if (makeCopy)
			ancestralProbs[nodeNr] = p.add(0);
		else
			ancestralProbs[nodeNr] = p;
	}
	
	public DVector[] clearAncestralProbs() {
		DVector[] processed = ancestralProbs;
		ancestralProbs = new DVector[numNodes];
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
	public void mulExtantProbabilities(DMatrix Q, boolean normalise) {
		// Update extant probabilities: p*Q
		for (int l = 0; l < numExtant; l++) {		
			// DoubleMatrix probs = (extantProbs[l]).mmul(Q); 
			DVector probs = (extantProbs[l]).rmul(Q);
			if (normalise) {
				//extantProbs[l] = probs.div(probs.sum());
				probs.divi(probs.sum()); /* normalise */
				probs.maxi(0.0); /* clamp(0,1) */
				probs.mini(1.0);				
			} 
			extantProbs[l] = probs;
		}
	}
	
	
	//public void mulExtantProbability(int idx, DoubleMatrix Q, boolean normalise) {
	//	DoubleMatrix probs = (extantProbs[idx]).mmul(Q); 
	//	if (normalise) {
	//		//extantProbs[l] = probs.div(probs.sum());
	//		probs.divi(probs.sum()); /* normalise */
	//		probs.maxi(0.0); /* clamp(0,1) */
	//		probs.mini(1.0);				
	//	} 
	//	extantProbs[idx] = probs;		
	//}
	
	
	public void setMinP(double minP) {
		for (int l = 0; l < numExtant; l++) {		
			DVector probs = extantProbs[l];
			probs.maxi(minP);
			probs.divi(probs.sum()); /* normalise */
			// extantProbs[l] = probs;
		}
	}
	
	@Override
	public DVector getLineageStateSum() {
		DVector sumAk = new DVector(numStates);
   		for (int lin = 0; lin < numExtant; lin++) {
   			sumAk.addi(extantProbs[lin]);
   		}
   		return sumAk;  
	}
	
	@Override
	public DVector getLineageSumSquares() {
		DVector sumsq = new DVector(numStates);
   		for (int lin = 0; lin < numExtant; lin++) {
   			sumsq.addi(extantProbs[lin].mul(extantProbs[lin]));
   		}
   		return sumsq;  
	}
	
	// note that size of p can be greater that numExtant
	public void copyProbabilitiesToArray(double[] p) {
		DVector probs;
		int idx=0;
		for(int i = 0; i < numExtant; i++) {
			probs = extantProbs[i];
			probs.copyToArray(p, idx);  // igor: new
			idx += numStates;
			//for(int j=0; j < numStates; j++) {
			//	p[idx++] = probs.get(j);
			//}
		}
	}
	
	public void copyProbabilitiesFromArray(double[] p) {
		DVector probs;
		int idx=0;
		for(int i = 0; i < numExtant; i++) {
			probs = extantProbs[i];
			//probs.copyFromArray(p, idx); 
			System.arraycopy(p, idx, probs.data, probs.start, probs.length);
			probs.maxi(0.0);
			probs.divi(probs.sum());
			idx += numStates;
		}
	}
	
    // Debug
    public void printExtantProbabilities() {
    		System.out.println("Extant Lineages Probabilities");
    		int lineage;
    		DVector pvec;
    		/*
    		 * Base implementation
    		int[] extantLineages = this.getExtantLineages();
    		DVector[] extantProbs = this.getExtantProbs();
    		for (int l = 0; l < numExtant; l++) {
    			System.out.println("lin: "+extantLineages[l]+" probs "+ extantProbs[l]);
    		}
    		*/
    		for (int idx = 0; idx < numExtant; idx++) {
    			// lineage = this.getLineageFromIndex(l);
    			lineage = extantLineages[idx];
    			// pvec = this.getExtantProbsFromIndex(l);
    			pvec = extantProbs[idx];	
    			System.out.println("lin: "+lineage+" probs "+ pvec);
		}
    }
    
    
    

}
