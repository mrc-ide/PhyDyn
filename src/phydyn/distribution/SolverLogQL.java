package phydyn.distribution;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;

// import org.jblas.DoubleMatrix;

import phydyn.model.TimeSeriesFGY;
import phydyn.model.TimeSeriesFGY.FGY;
import phydyn.model.TimeSeriesFGYStd;
import phydyn.util.DMatrix;
import phydyn.util.DVector;

public class SolverLogQL extends SolverIntervalODE implements FirstOrderDifferentialEquations {
	
	 private double[] ql0, ql1, qdata, logqdata;
	 
	 private double tsTimes0;
	 private int tsPointLast, numStatesSQ;
	 private TimeSeriesFGY ts;
	 private double sumA0;
	 private DVector A0;

	 static double MIN_Y = 1e-12 ;
	 static double MIN_P = 1e-12 ;
	 static double MAX_P = 1 - 1e-12 ;
	 
	 private static double clamp(double x, double lb, double ub) {
		 if (x < lb) return lb;
		 if (x > ub) return ub;
		 return x;
	 }
	 
	 private static double p2logit(double p) {
		 double pp = clamp( p, MIN_P, MAX_P); 
		 return Math.log( pp / (1.-pp));
	 }
	 
	 private static double logit2p(double u) {
		 return clamp( 1. / (1. + Math.exp(-u) ), MIN_P, MAX_P);
	 }
	 
	public SolverLogQL(STreeLikelihoodODE stlh) {  // assuming the number of states never change
		super(stlh);
    	numStatesSQ = numStates*numStates;
    	ql0 = new double[numStatesSQ+1];
    	ql1 = new double[numStatesSQ+1];
    	qdata = new double[numStatesSQ];
    	logqdata = new double[numStatesSQ];
    	// we could set tsTimes0 and setMinP HERE
	}
	
	
	public void solve(double h0, double h1, int lastPoint, STreeLikelihoodODE stlh) {
		tsPointLast = lastPoint;
		tsTimes0 = stlh.tsTimes0;
		ts = stlh.ts;

		StateProbabilities sp = stlh.stateProbabilities;
		A0 = sp.getLineageStateSum(); // numStates column vector
		sumA0 = A0.sum();		
		
		//System.out.println("A0="+A0);
		
		// prepare ql arrays
		int k=0;
		double logit0 = p2logit(0);
		double logit1 = p2logit(1);
		for(int i = 0; i < numStates; i++) {
			for(int j=0; j < numStates; j++) {
				if (i==j) 
					ql0[k] = logit1;
				else 
					ql0[k] = logit0;
				ql1[k] = 0.0;
				k++;
			}
		}
		ql0[k] = ql1[k] = 0.0;
		
		foi.integrate(this, h0, ql0, h1, ql1);
		
		DMatrix Q = new DMatrix(numStates,numStates);
		
		double[] Qdata = Q.data;
		for(k=0; k < numStatesSQ; k++) Qdata[k] = logit2p(ql1[k]); 
		 
		Q.diviRowVector(Q.columnSums());  // normalise columns
		 
		logLh = -ql1[numStatesSQ]; // likelihood - negative 

		if (Double.isNaN(logLh)) logLh = Double.NEGATIVE_INFINITY;

		// Update state probabilities in Likelihood object 
		// Update lineage probabilities

		// sp stores state probs as row-vectors - p * Qt
		// changed to Q * p
		// DoubleMatrix Qtrans = Q.transpose();
		sp.mulExtantProbabilities(Q, true);
		if (stlh.setMinP) {
			sp.setMinP(stlh.minP);
		}
		
		ts = null;
		return;
	}

	@Override
	public void computeDerivatives(double h, double[] ql, double[] dql)
			throws MaxCountExceededException, DimensionMismatchException {
		
		int tsPointCurrent = ts.getTimePoint(tsTimes0-h, tsPointLast);
		
		tsPointLast = tsPointCurrent;
		FGY fgy = ts.getFGY(tsPointCurrent);
		DVector Y = fgy.Y; // ts.getYs()[tsPointCurrent];
		DMatrix F = fgy.F; // ts.getFs()[tsPointCurrent];
		DMatrix G = fgy.G; // ts.getGs()[tsPointCurrent];
		
		if (forgiveY) Y.maxi(1.0); else Y.maxi(MIN_Y);
		int i;
		
		
		for(i=0; i < numStatesSQ; i++) {
			qdata[i] = logit2p(ql[i]);
			logqdata[i] = ql[i];
		}		
		
		
		// compute A and a
		DMatrix Q =  new DMatrix(numStates,numStates,qdata);				
		DMatrix Qnorm = new DMatrix(Q); // igor: Q.dup();
		Qnorm.diviRowVector(Q.columnSums());
		
		DVector A =  A0.rmul(Qnorm);  // Qnorm*A -- Qnorm.mmul(A0);
		
		A.divi(A.sum());  // normalised
		A.muli(sumA0);    // sum of A = sum(A0)
		//A = A.mul(sumA0).div(A.sum());
		
		DVector a = A.div(Y);  // column vector 

		// DoubleMatrix dQ = new DoubleMatrix(numStates,numStates,dql);
		double dL = 0;
		DMatrix FG = F.add(G);
		double pdot, Qkz;
		int k,l,z;
		i=0;
	    for (z = 0; z < numStates; z++){
	    	for (k = 0; k < numStates; k++){
	        	pdot = 0;
	        	Qkz = Q.get(k,z);
	        	for(l=0; l < numStates; l++) {
	        		if (k != l) {
	        			pdot += FG.get(k,l) *  Q.get(l,z)/ Math.max(Q.get(l,z), Y.get(l))- 
	        					FG.get(l,k) *  Qkz / Math.max(Qkz, Y.get(k));
	        			
	        		}
	        		// coalescent
	        		if (Qkz > 0) {
	        			pdot -= F.get(k,l) * a.get(l) * Qkz/ Math.max(Qkz, Y.get(k));
	        		}
	        	}
	        	//dQ.put(k,z,accum);
	        	dql[i++] = pdot / (Qkz*(1-Qkz) );
	        }
	    }
	    
	    double deltaDL = 0;
	    for (k= 0; k < numStates; k++){
	    	for (l =0 ; l < numStates; l++){			
	    		if (k == l && A.get(k) >= 1. ){
	    			deltaDL = (A.get(k) / Y.get(k)) * ((A.get(k)-1.) / Y.get(k)) * F.get(k,l) ; 
	      		} else {
	      			deltaDL = a.get(k) * a.get(l) * F.get(k,l);
	      		}
	    		//System.out.println("---deltaDL="+deltaDL);
	    		dL += deltaDL;
	    	}
	    }
	    dL = Math.max(dL, 0.);
	    //System.out.println("dL="+dL);
	    dql[numStatesSQ] = dL;					

	}

	@Override
	public int getDimension() {
		return numStatesSQ+1;
	}

}
