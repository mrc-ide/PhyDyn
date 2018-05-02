package phydyn.distribution;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;

// import org.jblas.DoubleMatrix;

import phydyn.model.TimeSeriesFGY;
import phydyn.model.TimeSeriesFGY.FGY;
import phydyn.util.DMatrix;
import phydyn.util.DVector;

public class SolverQL extends SolverIntervalODE implements FirstOrderDifferentialEquations {
	
	 private double[] ql0, ql1;
	 // private double[] qdata;
	 private double[] qnorm, a, adivy;
	 private double[] fg, fgtrans, ftrans;
	 
	 private double tsTimes0;
	 private int tsPointLast, numStatesSQ;
	 private TimeSeriesFGY ts;
	 private double sumA0;
	 private DVector A0;
	 boolean negQ;
	 int iterations;

	 static double MIN_Y = 1e-12 ;
	 
	public SolverQL(STreeLikelihoodODE stlh) {  // assuming the number of states never change
		super(stlh);
    	numStatesSQ = numStates*numStates;
    	ql0 = new double[numStatesSQ+1];
    	ql1 = new double[numStatesSQ+1];
    	//qdata = new double[numStatesSQ];
    	// we could set tsTimes0 and setMinP HERE
    	qnorm = new double[numStatesSQ];
    	a = new double[numStates];
    	adivy = new double[numStates];
		fg = new double[numStatesSQ];
		fgtrans = new double[numStatesSQ];
		ftrans = new double[numStatesSQ];
	}
	
	
	public void solve(double h0, double h1, int lastPoint, STreeLikelihoodODE stlh) {
		tsPointLast = lastPoint;
		tsTimes0 = stlh.tsTimes0;
		ts = stlh.ts;

		StateProbabilities sp = stlh.stateProbabilities;
		A0 = sp.getLineageStateSum(); // numStates column vector
		sumA0 = A0.sum();		
		
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
		
		DMatrix Q = new DMatrix(numStates,numStates);
		
		double[] Qdata = Q.data;
		for(k=0; k < numStatesSQ; k++) Qdata[k] = ql1[k]; 
		 
		Q.diviRowVector(Q.columnSums());  // normalise columns
		 
		logLh = -ql1[numStatesSQ]; // likelihood - negative 
		// TODO: check if sign has to be modified in main loop
		if (Double.isNaN(logLh)) logLh = Double.NEGATIVE_INFINITY;

		// Update state probabilities in Likelihood object 
		// Update lineage probabilities
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
		DVector Y = fgy.Y;
		DMatrix F = fgy.F;
		DMatrix G = fgy.G;
		
		if (forgiveY) Y.maxi(1.0); else Y.maxi(MIN_Y);
		int i;
		
		double[] y = Y.data;
		double[] q = ql;
		
		/*
		DoubleMatrix Q =  new DoubleMatrix(numStates,numStates,qdata);		
		DoubleMatrix Qnorm = Q.dup();
		Qnorm.diviRowVector(Q.columnSums());
		*/
		
		// qnorm
		double colsum;
		int idx, colidx=0;
		for(int col=0; col<numStates; col++) {
			colsum=0;
			idx=colidx;
			for(int row=0; row<numStates; row++) {
				colsum += q[idx++];
			}
			idx=colidx;
			for(int row=0; row<numStates; row++) {
				qnorm[idx] = q[idx]/colsum;
				idx++;
			}
			colidx+=numStates;			
		}
		
		// DoubleMatrix A = Qnorm.mmul(A0);
				
		double a0;
		int aidx=0, a0idx=0;
		int qidx=0;
		a0 = A0.data[a0idx];
		for(int row=0; row < numStates; row++) {
			a[aidx]=a0*qnorm[qidx];
			qidx++; aidx++;
		}
		for(int col=1; col<numStates; col++) {
			a0idx++;
			aidx=0;
			a0 = A0.data[a0idx];
			for(int row=0;row < numStates; row++) {
				a[aidx]+=a0*qnorm[qidx];
				qidx++; aidx++;
			}
		}
			
		/*
		A.divi(A.sum());  // normalised
		A.muli(sumA0);    // sum of A = sum(A0)
		DoubleMatrix a = A.div(Y);  // column vector 
		*/
		
		double asum=0;
		
		for(i=0; i < numStates; i++) asum+=a[i];
		for(i=0; i < numStates; i++) {
			a[i] = a[i]*sumA0/asum;
			adivy[i] = a[i]/Y.data[i];
		}
		
		// DoubleMatrix dQ = new DoubleMatrix(numStates,numStates,dql);
		//DoubleMatrix FG = F.add(G);
		
		double dL=0;
		
		aidx=0;qidx=0;
		for(int row=0; row<numStates; row++) {
			qidx = row;
			for(int col=0; col < numStates; col++) {
				fgtrans[qidx] = fg[aidx] = F.data[aidx]+G.data[aidx];
				ftrans[qidx] = F.data[aidx];
				aidx++;
				qidx += numStates;
			}
		}
				
		double accum;
		int k,l,z;
		int lz, kz, lk, next_lz=0;
		i=0;
		lz = kz = lk = 0;
	    for (z = 0; z < numStates; z++){
	    	lk = 0;
	    	for (k = 0; k < numStates; k++){
	        	accum=0;
	        	lz = next_lz;
	        	for(l=0; l < numStates; l++) {
	        		if (k != l) {
	        			if (q[lz] > 0) {
	        				// accum += FG.get(k,l) * Q.get(l,z)/Math.max(Q.get(l,z), Y.get(l));
	        				accum += fgtrans[lk] * q[lz]/Math.max(q[lz], y[l]);
	        			}
	        			if (q[kz] > 0) {
	        				// accum -= FG.get(l,k) *  Q.get(k,z)/Math.max(Q.get(k,z), Y.get(k));
	        				accum -= fg[lk] * q[kz]/Math.max(q[kz], y[k]);
	        			}
	        		}
	        		if (q[kz] > 0) {
	        			// accum -= F.get(k,l) * a.get(l) * Q.get(k,z)/Math.max(Q.get(k,z), Y.get(k));
	        			accum -= ftrans[lk] * adivy[l] * q[kz]/Math.max(q[kz], y[k]);
	        		}
	        		lz++;
	        		lk++;
	        	}  // end-for l	        	
	        	dql[i++] = accum;
	        	kz++;
	        } // end-for k
	    	next_lz += numStates;
	    } // end-for z
	    
	    double deltaDL=0;
	    lk=0;
	    for (k= 0; k < numStates; k++){
	    	for (l =0 ; l < numStates; l++){			
	    		if (k == l && a[k] >= 1. ){
	    			// deltaDL = (A.get(k) / Y.get(k)) * ((A.get(k)-1.) / Y.get(k)) * F.get(k,l) ; 
	    			deltaDL = (a[k] / y[k]) * ((a[k]-1.) / y[k]) * ftrans[lk] ; 
	      		} else {
	      			// deltaDL = a.get(k) * a.get(l) * F.get(k,l);
	      			deltaDL = adivy[k] * adivy[l] * ftrans[lk];
	      		}
	    		dL += deltaDL;
	    		lk++;
	    	}
	    }
	    dL = Math.max(dL, 0.);
	    dql[numStatesSQ] = dL;					

	}

	@Override
	public int getDimension() {
		return numStatesSQ+1;
	}

}
