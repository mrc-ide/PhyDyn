package phydyn.distribution;

import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;

/*
 * Structured Coalescent likelihood matrix exponentiation implementation
 * Based on code by D. Rasmussen & NF. Mueller
 */

public class STreeLikelihoodExp extends STreeLikelihood {

	
	public STreeLikelihoodExp() {
		
	}
	
	
	/* updates t,h,tsPoint and lineage probabilities */
    protected double processInterval(int interval, double intervalDuration, double[] tsTimes ) {
        double segmentDuration;
        
    	double hEvent = h + intervalDuration; 		// event height
    	double tEvent = tsTimes[0] - hEvent;      // event time
    	
    	// traverse timeseries until closest latest point is found
    	// tsTimes[tsPoint+1] <= tEvent < tsTimes[tsPoint] -- note that ts points are in reverse time
    	// t = tsTimes[0] - h;
    	
    	// Process Interval
    	double lhinterval = 0;
    	while (tsTimes[tsPoint+1] > tEvent) {
    		//System.out.println("tsPoint: "+tsPoint+" Y="+ts.getYs()[tsPoint]);
    		segmentDuration = t - tsTimes[tsPoint+1];
    		lhinterval += processIntervalSegment(tsPoint,segmentDuration);
    		if (lhinterval == Double.NEGATIVE_INFINITY) {
    			return Double.NEGATIVE_INFINITY;
    		} 				
    		t = tsTimes[tsPoint+1];
    		h += segmentDuration;
    		tsPoint++;
    		// tsTimes[0] = t + h -- CONSTANT
    	}
    	// process (sub)interval before event
    	segmentDuration = hEvent - h;  // t - tEvent
    	if (segmentDuration > 0) {
    		lhinterval += processIntervalSegment(tsPoint,segmentDuration);
    		if (lhinterval == Double.NEGATIVE_INFINITY) {
    			return Double.NEGATIVE_INFINITY;
    		} 	
    	}
    	//System.out.println("tsPoint: "+tsPoint+" Y="+ts.getYs()[tsPoint]);
    	// update h and t to match tree node/event
    	h = hEvent;
    	t = tsTimes[0] - h;
    	return lhinterval;
    }
	
	
	protected double processIntervalSegment(int tsPoint, double duration) {
		double segmentLh = -computeLambdaSum(tsPoint) * duration;
		boolean negInf = updateLineProbs(tsPoint, duration); 
		if (negInf) {
			return Double.NEGATIVE_INFINITY;
		}
		return segmentLh;
	}
	
	  /* return: true if an error occurred */
    protected boolean updateLineProbs(int tsPoint, double dt) { 
 
		DoubleMatrix A = stateProbabilities.getLineageStateSum();
		DoubleMatrix Y = ts.getYs()[tsPoint];
		DoubleMatrix F = ts.getFs()[tsPoint];
		DoubleMatrix G = ts.getGs()[tsPoint];
		DoubleMatrix mul = Y.sub(A).div(Y);  
		
		/*
		 * (before: If there is more state probability in a deme than individuals
		 * return the false flag indicating an error)
		 * Fix coefficient to [0,1] range
		 */
		if(mul.min()<0 || mul.max() > 1) { 
			//System.out.println("Y<A t: "+t+" Y "+Y+" A: "+A+" -- fixing --");
			for(int i=0; i < mul.length; i++) {
				final double v = mul.get(i);
				if (v < 0.0) { mul.put(i, 0.0); }
				if (v > 1.0) { mul.put(i, 1.0); }
			}
		}
		
		if (Double.isNaN(mul.get(0)))
			{ System.out.println("NaN"); return true; }
		
		
		DoubleMatrix QT = computeQTrans(Y, F, G, mul);
		
		// dP(i)/dt = Q*Pi , i lineage, Pi column vector
		// dP(i)/dt = Pi*QT, QT=trans(Q); Pi,dPi row vectors
		// transposed version more efficient due to jblas column-major layout
	    // We need the state probabilities as row vectors
		
		/* Compute lineage probabilities */
		DoubleMatrix newP = matrixExponential(QT, dt);
		
		// Update extant probabilities: p*Q
		stateProbabilities.mulExtantProbabilities(newP, true);
		return false;		
    }
     
	
	   // original matrix calculation.
    protected DoubleMatrix computeQTrans(DoubleMatrix Y, DoubleMatrix F, DoubleMatrix G, DoubleMatrix Coef) {

    	DoubleMatrix Q = DoubleMatrix.zeros(numStates,numStates);
    	double rateOutByBirth, rateOutByMigration, totalRate;
    	for (int k = 0; k < numStates; k++) 
    	{
    		double rowSum = 0.0;
    		for (int l = 0; l < numStates; l++) 
    		{
    			if (k != l) 
    			{		// off-diagonal
    				if (Y.get(k) > 0) 
    				{
    					rateOutByBirth = F.get(l,k) * Coef.get(l) / Y.get(k);      // Birth in other deme (Backwards in time)
    					rateOutByMigration = G.get(l,k) / (Y.get(k)); 			// State change of lineages (Backwards in time)
    					totalRate = rateOutByBirth + rateOutByMigration;
    					Q.put(k, l, totalRate);
    					rowSum += totalRate;
    				} 
    				else
    				{																// diagonal
    					Q.put(k, l, 0.0);
    				}
    			}
    		}
    		Q.put(k,k,-rowSum);
    	}
    	return Q;
	
    }
 
	
	   // If Q is diagonal, we could use expA(i,i) = exp(A(i,i)), expA(i,j) = 0
    protected DoubleMatrix matrixExponential(DoubleMatrix Q, double dt){
    	return MatrixFunctions.expm(Q.mul(dt));
    }   
    
    /* deprecated, not good */
    protected DoubleMatrix computeQ(DoubleMatrix Y, DoubleMatrix F, DoubleMatrix G, DoubleMatrix Coef) {    	
    	DoubleMatrix Q = DoubleMatrix.zeros(numStates,numStates);
   	
    	double rateOutByBirth, rateOutByMigration, totalRate;
		for (int k = 0; k < numStates; k++) /* k row */
		{
			double colSum = 0.0, diag;
			if (Y.get(k) > 0) {
				for (int l = 0; l < numStates; l++) 
				{				
					rateOutByBirth = F.get(l,k) * Coef.get(l) / Y.get(k);
					//rateOutByBirth = F.get(l,k)  / Y.get(k);
					rateOutByMigration = G.get(l,k) / (Y.get(k));
					totalRate = rateOutByBirth + rateOutByMigration;
					Q.put(k, l, totalRate);
					colSum += totalRate;				
					
				}
				diag = Q.get(k,k);
				Q.put(k,k,diag-colSum);
			}
			// else skip and leave row k with zero values
			// Q.put(k, l, 0.0);		
		}
		return Q;   	
    }

    /*  Iterative version */
    public double computeLambdaSum(int t, int childIdx1, int childIdx2) {
    	double lambda;
    	
    	DoubleMatrix F,Y;
    	Y = ts.getYs()[t];
    	F = ts.getFs()[t];	
    	
		DoubleMatrix pa, pvec1, pvec2;		
		pvec1 = stateProbabilities.getStateProbsFromIndex(childIdx1);
		pvec2 = stateProbabilities.getStateProbsFromIndex(childIdx2);
		/* previous version */
	    DoubleMatrix coalRates = DoubleMatrix.zeros(numStates, numStates);
	    for (int k = 0; k < numStates; k++) {
	    	for (int l = 0; l < numStates; l++) {            	
	    		final double Yk = Y.get(k);	
	    		final double Yl = Y.get(l);	
	    		//System.out.println("numStates="+numStates+"dim:"+F.rows+", "+F.columns);
	    		lambda = F.get(k,l) / (Yk*Yl) 
	    				* (pvec1.get(k) * pvec2.get(l)
	    						+ pvec1.get(l) * pvec2.get(k));
	    		coalRates.put(k, l, lambda);
	    	}
	    }
	    lambda = coalRates.sum();
	    pa = coalRates.rowSums().div(lambda).transpose(); // row-vector
	    /* pa := new state probabilities of the lineage dt after coalescing */

    	return lambda;
    }
    

}
