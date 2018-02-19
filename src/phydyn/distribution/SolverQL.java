package phydyn.distribution;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.jblas.DoubleMatrix;

import phydyn.model.TimeSeriesFGY;
import phydyn.model.TimeSeriesFGY.FGY;

public class SolverQL extends SolverIntervalODE implements FirstOrderDifferentialEquations {
	
	 private double[] ql0, ql1, qdata;
	 
	 private double tsTimes0;
	 private int tsPointLast, numStatesSQ;
	 private TimeSeriesFGY ts;
	 private double sumA0;
	 private DoubleMatrix A0;
	 boolean negQ;
	 int iterations;

	 static double MIN_Y = 1e-12 ;
	 
	public SolverQL(STreeLikelihoodODE stlh) {  // assuming the number of states never change
		super(stlh);
    	numStatesSQ = numStates*numStates;
    	ql0 = new double[numStatesSQ+1];
    	ql1 = new double[numStatesSQ+1];
    	qdata = new double[numStatesSQ];
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
		for(int i = 0; i < numStates; i++) {
			for(int j=0; j < numStates; j++) {
				if (i==j) 
					ql0[k] = 1.0;
				else 
					ql0[k] = 0.0;
				ql1[k] = 0.0;
				k++;
			}
		}
		ql0[k] = ql1[k] = 0.0;
		
		negQ=false;	
		iterations=0;
		
		if ((h1-h0) < stlh.stepSize) {	
			FirstOrderIntegrator newfoi;
			newfoi = new ClassicalRungeKuttaIntegrator((h1-h0)/10);
			if (debug) System.out.println("--- length="+(h1-h0));
			newfoi.integrate(this, h0, ql0, h1, ql1);
		}  else {
			foi.integrate(this, h0, ql0, h1, ql1);
		}
		
		DoubleMatrix Q = new DoubleMatrix(numStates,numStates);
		
		double[] Qdata = Q.data;
		for(k=0; k < numStatesSQ; k++) Qdata[k] = ql1[k]; 
		 
		Q.diviRowVector(Q.columnSums());  // normalise columns
		 
		logLh = -ql1[numStatesSQ]; // likelihood - negative 
		// TODO: check if sign has to be modified in main loop
		if (Double.isNaN(logLh)) logLh = Double.NEGATIVE_INFINITY;

		// Update state probabilities in Likelihood object 
		DoubleMatrix Qtrans = Q.transpose();
		// Update lineage probabilities
		sp.mulExtantProbabilities(Qtrans, true);
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
		DoubleMatrix Y = fgy.Y;
		DoubleMatrix F = fgy.F;
		DoubleMatrix G = fgy.G;
		
		if (forgiveY) Y.maxi(1.0); else Y.maxi(MIN_Y);
		int i;
		
		
		for(i=0; i < numStatesSQ; i++) {
			if (ql[i]<0) {
				negQ=true;
				qdata[i] = ql[i];
				//qdata[i] = 0.00001;
			} else {
				qdata[i] = ql[i];
			}
			
		}		
		
		DoubleMatrix Q =  new DoubleMatrix(numStates,numStates,qdata);
		
		
		DoubleMatrix Qnorm = Q.dup();
		Qnorm.diviRowVector(Q.columnSums());
		
		DoubleMatrix A = Qnorm.mmul(A0);
		
		
			
		//System.out.println("A="+A);
		A.divi(A.sum());  // normalised
		A.muli(sumA0);    // sum of A = sum(A0)
		//A = A.mul(sumA0).div(A.sum());
		DoubleMatrix a = A.div(Y);  // column vector 

		// DoubleMatrix dQ = new DoubleMatrix(numStates,numStates,dql);
		double dL = 0;
		DoubleMatrix FG = F.add(G);
		double accum;
		int k,l,z;
		i=0;
	    for (z = 0; z < numStates; z++){
	    	for (k = 0; k < numStates; k++){
	        	accum = 0;
	        	for(l=0; l < numStates; l++) {
	        		if (k != l) {
	        			if (Q.get(l,z) > 0) {
	        				accum += FG.get(k,l) *  Q.get(l,z)/Math.max(Q.get(l,z), Y.get(l));
	        			}
	        			if (Q.get(k,z) > 0) {
	        				accum -= FG.get(l,k) *  Q.get(k,z)/Math.max(Q.get(k,z), Y.get(k));
	        			}
	        		}
	        		if (Q.get(k,z) > 0) {
	        			accum -= F.get(k,l) * a.get(l) * Q.get(k,z)/Math.max(Q.get(k,z), Y.get(k));
	        		}
	        	}
	        	//dQ.put(k,z,accum);
	        	dql[i++] = accum;
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
