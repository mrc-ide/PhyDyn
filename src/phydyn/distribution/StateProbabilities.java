package phydyn.distribution;

import java.util.List;

import org.jblas.DoubleMatrix;

import phydyn.model.TimeSeriesFGY.FGY;
import phydyn.util.DMatrix;
import phydyn.util.DVector;

/**
 * @author Igor Siveroni
 */

public abstract class StateProbabilities implements StateProbabilitiesI {
	protected int numNodes; // total number of Tree Nodes
	protected int numStates;  // number of demes
		
	protected abstract int getLineageNr(int idx);
	protected abstract int getLineageIdx(int linNr);
	
	public StateProbabilities(int numNodes, int numStates) { //usually tree.getNodeCount
		this.numNodes = numNodes;
		this.numStates = numStates;
	}
		
	public double updateRateMatrix(FGY fgy, DMatrix rates) {
		double totalCoal = 0.0;
		int numExtant = this.getNumExtant();
		if (numExtant < 2) return totalCoal; 
		
		DMatrix F = fgy.F;
		DVector Y = fgy.Y;
		
		
		rates.reshape(numExtant, numExtant);		
		
		Y.maxi(1e-12);  // Fixes Y lower bound
			
				
		/*
		 * Simplify if F is diagonal.
		 */
		DVector pI;
		DVector[] extantProbs = this.getExtantProbs();
		
		DVector pi_Y, pj_Y, pa, pJ;
		for (int linI = 0; linI < numExtant; linI++) {
			pI = extantProbs[linI];
			pi_Y = pI.div(Y);
			for (int linJ = linI+1; linJ < numExtant; linJ++) {
				pJ = extantProbs[linJ];
				pj_Y = pJ.div(Y);
				pa = pi_Y.mul( pj_Y.rmul(F) );   // F.mmul(pj_Y)
				pa.addi(pj_Y.mul( pi_Y.rmul(F) )); // F..mmul(pi_Y)
				final double rate_ij = pa.sum();
				// use bottom left triangle
				rates.put(linJ,linI,rate_ij);
				totalCoal += rate_ij;	
			}	
		}
		
		return totalCoal;
	}
	
	/*
	 * Picks a pair of extant lineages by sampling from a Categorical / Generalised Bernoulli
	 * distribution defined by the coalescent rates p_i = lambda_i /  lambda_total
	 * The coalescent rates are stored in the rates matrix. 
	 * All the rates (bottom left minus diagonal) must add to toalCoal.
	 * 
	 * 
	 */
	public double sampleCoalescentPair(DMatrix rates, int[] coalPair, double totalCoal, double u) {
		//System.out.println("SAMPLE PAIR!! --- num extant = "+this.getNumExtant());
		final double stopRate = u*totalCoal;
		final int numExtant = this.getNumExtant();
		//System.out.println("num extant = "+numExtant);
		totalCoal += 0.01; 
		double accumRate = 0.0;
		int i,j=0;
		
		boolean found = false;
		for (i = 0; i < numExtant && !found; i++) {
			for (j = i+1; j < numExtant && !found; j++) {
				accumRate += rates.get(j,i); // use bottom left	
				if (stopRate < accumRate) {
					coalPair[0] = this.getLineageNr(j);
					coalPair[1] = this.getLineageNr(i);
					return rates.get(j, i);
				}
			}
		}
		throw new IllegalArgumentException("sampleCoalescentPair: totalCoal mismatch (too small)");
	}
	
	public DVector getCoalProbVectorNr(int[] coalNr, FGY fgy) {
		int linI = this.getLineageIdx(coalNr[0]);
		int linJ = this.getLineageIdx(coalNr[1]);
		return getCoalProbVectorIdx(linI,linJ, fgy);
	}
	
	public DVector getCoalProbVectorIdx(int linI, int linJ, FGY fgy) {
		DVector[] extantProbs = this.getExtantProbs();
		DVector pi_Y = extantProbs[linI].div(fgy.Y);
		DVector pj_Y = extantProbs[linJ].div(fgy.Y);
		DVector pa = pi_Y.mul( pj_Y.rmul(fgy.F) );   // F.mmul(pj_Y)
		pa.addi(pj_Y.mul( pi_Y.rmul(fgy.F) )); // F..mmul(pi_Y)
		return pa;
	}
	
	
	
	public double calcCoalescentRate(DVector A, DMatrix F, DMatrix G, DVector Y,
			boolean approx, boolean isDiag) {
		if (A==null) {	
			A = this.getLineageStateSum(); 
		}
		double totalCoal = 0.0;
		final int numExtant = this.getNumExtant();
		final int numStates = this.getNumStates();

		if (numExtant < 2) return totalCoal; 
				
		
		Y.maxi(1e-12);  // Fixes Y lower bound
			
		if (approx) {  // (A/Y)' * F * (A/Y)
			DVector A_Y = A.div(Y);
			return A_Y.dot( A_Y.rmul(F) );  // igor: F.mmul(A_Y)
		}
				
		/*
		 * Simplify if F is diagonal.
		 */
		DVector pI;
		DVector[] extantProbs = this.getExtantProbs();
		
		double[] phiDiag = new double[this.getNumStates()];
		
    	if(isDiag){
    		// resolve constant issue later
    		//if (!popModel.isConstant()) { // if constant, phiDiag was already calculated
    			for (int k = 0; k < numStates; k++){
    				phiDiag[k] = F.get(k, k) / (Y.get(k)*Y.get(k));
    			}
    		//}
    		DVector phiDiagVector = new DVector(numStates,phiDiag);    		  		
    		DVector A2 = this.getLineageStateSum();
    		A2.squarei();
    		A2.subi( this.getLineageSumSquares()  );
    		totalCoal = A2.dot(phiDiagVector);    					
    	} else {
    		DVector pi_Y, pj_Y, pa, pJ;
			for (int linI = 0; linI < numExtant; linI++) {
				pI = extantProbs[linI];
				pi_Y = pI.div(Y);
				for (int linJ = linI+1; linJ < numExtant; linJ++) {
					pJ = extantProbs[linJ];
					pj_Y = pJ.div(Y);
					pa = pi_Y.mul( pj_Y.rmul(F) );   // F.mmul(pj_Y)
					pa.addi(pj_Y.mul( pi_Y.rmul(F) )); // F..mmul(pi_Y)
					totalCoal += pa.sum();				
				}	
			}
    	} 
    	return totalCoal;    	
		
	}
	
}
