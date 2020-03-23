package phydyn.distribution;

import phydyn.util.DMatrix;
import phydyn.util.DVector;

/*
 * Under development.
 */

public class StateProbabilitiesMatrix extends StateProbabilities {
	protected int numTips;
	
	protected DMatrix probsMatrix;
	protected DVector[] probsArray;
	protected int numExtant;
	protected int[] extantLineages; 
	protected int[] extantIndex; // position in extantLineages array
	protected DVector[] extantProbs;  // keep for efficient external access
	
	@Override
	public void check() {}
	
	@Override
	public void clear() {
		for(int col=0, idx=probsMatrix.start; col<numNodes; col++) {
			probsArray[idx] = new DVector(numStates,probsMatrix.data,idx);
			idx += numStates;
		}
		initExtantLineages();
	}
	
	@Override
	public StateProbabilities copy() {
		// TODO Auto-generated method stub
		// important for state
		return null;
	}

	public StateProbabilitiesMatrix(int numNodes, int numStates) {
		super(numNodes,numStates);
					
		this.numTips = (numNodes+1)/2; 
		probsMatrix = new DMatrix(numStates,numNodes);
		probsArray = new DVector[numNodes];
		extantLineages = new int[numTips];
		extantIndex = new int[numNodes];
		for(int col=0, idx=probsMatrix.start; col<numNodes; col++) {
			probsArray[idx] = new DVector(numStates,probsMatrix.data,idx);
			idx += numStates;
		}
		initExtantLineages();
	}
	
	public int getNumStates() { return numStates; }
	

	// do we need to set them to -1??
	private void initExtantLineages() {
		numExtant = 0;
		for(int i=0; i < extantIndex.length; i++)
			extantIndex[i] = -1; // indicates not extant
	}

	@Override
	public int getNumExtant() {
		return numExtant;	
	}
	
	@Override
	protected int getLineageNr(int idx) {
		return extantLineages[idx];
	}
	
	@Override
	protected int getLineageIdx(int linNr) {
		return extantIndex[linNr];
	}

	@Override
	// careful: returns internal array, read-only
	public int[] getExtantLineages() {
		return extantLineages;
	}
	
	// probs.length >= this.numExtant
	@Override
	public int getExtantProbs(DVector[] probs) {
		if (probs.length<numExtant)
			throw new IllegalArgumentException("Array size insufficient");
		for(int i=0; i < numExtant; i++)
			probs[i] = probsArray[extantLineages[i]];
		return numExtant;
	}
	
	@Override
	// careful: read-only
	public DVector[] getExtantProbs() {
		return extantProbs;
	}



	@Override
	public void getExtantProbabilities(int[] lineages, int numLineages, DVector[] pVectors) {
		for(int l=0; l < numLineages; l++) {
			pVectors[l] = probsArray[lineages[l]];
		}
		
	}

	@Override
	public DVector addLineage(int nodeNr) {
		// pvec already created
		extantLineages[numExtant] = nodeNr;
		extantProbs[numExtant] = probsArray[nodeNr];
		extantIndex[nodeNr] = numExtant;
		numExtant++;
		return probsArray[nodeNr];
	}
	
	@Override
	// Copies contents of pvec to corresponding probability vector
	public DVector addLineage(int nodeNr, DVector pvec) {
		probsArray[nodeNr].copy(pvec);
		return addLineage(nodeNr);
	}

	@Override
	public DVector addSample(int nodeNr, int sampleState) {
		// assumption: zeroed pvec
		probsArray[nodeNr].put(sampleState, 1);
		return addLineage(nodeNr);
	}
	
	@Override
	public DVector addSample(int nodeNr, int sampleState, double  minP) {
		// assumption: zeroed pvec
		// probsArray[nodeNr].put(sampleState, 1);
		DVector probs = extantProbs[nodeNr];
		probs.put(minP);
		probs.put(sampleState, 1.0 - (numStates-1)*minP);		
		probs.divi(probs.sum());  // normalise
		return addLineage(nodeNr);
	}

	@Override
	// igor: verify this. optimise
	public DVector removeLineage(int nodeNr) {
		if (extantIndex[nodeNr]==-1)
			throw new IllegalArgumentException("removeLineage: Node not extant");
		if (nodeNr==extantLineages[numExtant-1]) {
			extantIndex[nodeNr] = -1;
			extantProbs[numExtant-1] = null;
		} else {
			int lastNr = extantLineages[numExtant-1];
			int nodeIdx = extantIndex[nodeNr];
			extantLineages[nodeIdx] = lastNr;
			extantProbs[nodeIdx] = extantProbs[numExtant-1];
			extantIndex[lastNr] = nodeIdx;
			extantIndex[nodeNr] = -1;
			extantProbs[numExtant-1] = null;
		}
		numExtant--;
		return null;
	}

	@Override
	// igor: check ancestralProbs first
	public DVector getRootProbs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DVector getExtantProbs(int lineage) {
		if (extantIndex[lineage]==-1)
			throw new IllegalArgumentException("getExtantProbs: lineage not extant");
		return probsArray[lineage];
	}

	@Override
	public void printExtantProbabilities() {
		// print lineage and p vector
		for(int idx=0; idx<numExtant;idx++) {
			System.out.println("lin: "+extantLineages+" probs "+ extantProbs[idx]);
		}
	}

	@Override
	public DVector getAncestralProbs(int nodeNr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void storeAncestralProbs(int nodeNr) {
		// TODO Auto-generated method stub

	}

	@Override
	public void storeAncestralProbs(int nodeNr, DVector p, boolean makeCopy) {
		// TODO Auto-generated method stub

	}

	@Override
	public DVector[] clearAncestralProbs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void printAncestralProbabilities() {
		// TODO Auto-generated method stub

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
			extantProbs[l].copy(probs);
		}
	}

	@Override
	public void setMinP(double minP) {
		for (int l = 0; l < numExtant; l++) {		
			DVector probs = extantProbs[l];
			probs.maxi(minP);
			probs.divi(probs.sum()); /* normalise */
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

	@Override
	public void copyProbabilitiesToArray(double[] p) {
		DVector probs;
		int idx=0;
		for(int i = 0; i < numExtant; i++) {
			probs = extantProbs[i];
			probs.copyToArray(p, idx);
			idx += numStates;
		}
	}

	@Override
	public void copyProbabilitiesFromArray(double[] p) {
		DVector probs;
		int idx=0;
		for(int i = 0; i < numExtant; i++) {
			probs = extantProbs[i];
			System.arraycopy(p, idx, probs.data, probs.start, probs.length);
			probs.maxi(0.0);
			probs.divi(probs.sum());
			idx += numStates;
		}		
	}

}
