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

public class SolverQLConstant extends SolverIntervalODE implements FirstOrderDifferentialEquations {
	
	 private double[] ql0, ql1;
	 
	 private double tsTimes0;
	 private int tsPointLast, numStatesSQ;
	 private TimeSeriesFGY ts;
	 private double sumA0;
	 private DVector A0;
	 private DVector a;
	 //boolean negQ;
	 int iterations;
	 boolean isDiagF=false;
	 
	 private double[] diagF, diagFa;
	 private DMatrix F,G,FG, FdivY2, Gtrans;
	 private DMatrix QdivY, Qnorm;
	 private DVector FdivY2vector, sumColumnsG, diagG  ;
	 DVector Y;

	 static double MIN_Y = 1e-12 ;
	 
	public SolverQLConstant(STreeLikelihoodODE stlh) {  // assuming the number of states never change
		super(stlh);
    	numStatesSQ = numStates*numStates;
    	ql0 = new double[numStatesSQ+1];
    	ql1 = new double[numStatesSQ+1];
    	QdivY = new DMatrix(numStates,numStates);
    	Qnorm = new DMatrix(numStates,numStates);
    	a = new DVector(numStates);

    	isDiagF = stlh.popModel.isDiagF();
    	diagF = new double[numStates];
    	diagFa = new double[numStates];
    	// we could set tsTimes0 and setMinP HERE
	}
	
	public boolean initValues(STreeLikelihoodODE stlh) {
		// Pre-compute constant quantities
		// This should be done for the whole tree, not just the interval
		ts = stlh.ts;
		FGY fgy = ts.getFGY(1);
		Y = fgy.Y; 
		if (forgiveY) Y.maxi(1.0); else Y.maxi(MIN_Y);
		F = fgy.F; 
		G = fgy.G; 
		Gtrans = G.transpose();
		FG = F.add(G);
		if (isDiagF) {  // igor: check that this works! note FdivY2
			FdivY2vector = new DVector(numStates);
			for(int i = 0; i < numStates; i++) {
				diagF[i] = F.get(i,i);
				FdivY2vector.data[i] = diagF[i]/Y.get(i)/Y.get(i);
			}
			sumColumnsG = G.columnSums();
			//diagG = G.sub(DoubleMatrix.diag(sumColumnsG));
			
		} else {
			// DoubleMatrix Ydm = new DoubleMatrix(numStates,1,Y.data); // igor: temp
			FdivY2 = (F.divRowVector(Y)).divRowVector(Y);  // igor: changed to Y
		}
		return false;
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
		
		//negQ=false;	
		iterations=0;
		
		if ((h1-h0) < stlh.stepSize) {	
			FirstOrderIntegrator newfoi;
			newfoi = new ClassicalRungeKuttaIntegrator((h1-h0)/10);
			if (debug) System.out.println("--- length="+(h1-h0));
			newfoi.integrate(this, h0, ql0, h1, ql1);
		}  else {
			foi.integrate(this, h0, ql0, h1, ql1);
		}
		
		
		DMatrix Q = new DMatrix(numStates,numStates,ql1);
		 
		Q.diviRowVector(Q.columnSums());  // normalise columns
		 
		logLh = -ql1[numStatesSQ]; // likelihood - negative 
		// TODO: check if sign has to be modified in main loop
		if (Double.isNaN(logLh)) logLh = Double.NEGATIVE_INFINITY;

		// Update state probabilities in Likelihood object 
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
		//if (debug) iterations++;
		//if (iterations>100) throw new IllegalArgumentException("Too many iterations");
		//if (debug) System.out.println("... comp deriv");
		
		int tsPointCurrent = ts.getTimePoint(tsTimes0-h, tsPointLast);		
		tsPointLast = tsPointCurrent;		
		
		int idx;				
		
		DMatrix Q =  new DMatrix(numStates,numStates,ql);
		Q.diviColumnVector(Y,QdivY); // QdivY = Q /row Y		
		
		QdivY.clampi(0.0,  1.0);
				
		Q.diviRowVector(Q.columnSums(),Qnorm); // Qnorm = Q /row Q.colSums
		
		DVector A = A0.rmul(Qnorm); // changed new function - old  Qnorm.mmul(A0);	
		
		A.divi(A.sum());  // normalise
		A.muli(sumA0);    // sum of A = sum(A0)
		//A = A.mul(sumA0).div(A.sum());
		
		// DVector a = A.div(Y);  // column vector 
		A.divi(Y,a);
		for(int i=0; i < numStates; i++) {
			diagFa[i] = diagF[i]* a.data[i];
		}

		double dL = 0;

		//DoubleMatrix GQ = G.mmul(QdivY);		
		double accum;		
		int k,l,z;
		idx=0;
		double deltaDL = 0;
		double qdivy;
		int lk, lz;
		if (isDiagF) { /* F is diagonal */			
			for (z = 0; z < numStates; z++){
				lk = Gtrans.start;
				for (k = 0; k < numStates; k++){
					accum = 0;
					lz = z*numStates;
			        for(l=0; l < numStates; l++) {
			        	accum += Gtrans.data[lk] * QdivY.data[lz];     // QdivY.get(l,z);
			        	lk++; lz++;
			        	// accum += G.get(k,l) *  QdivY.get(l,z);  
		        		//accum -= G.get(l,k) * QdivY.get(k,z)
			        }
			        // Matrix[idx] = Matrix(k,z)
			        accum -= QdivY.data[idx] * (sumColumnsG.get(k) + diagFa[k] );
			        // accum -= QdivY.data[idx] * (sumColumnsG.get(k) + diagF[k] * a.get(k) );	             	
					dql[idx] = accum;  //dQ.put(k,z,accum);
					idx++;
				}
			}
			
			for (k= 0; k < numStates; k++){
				if (A.get(k) >= 1. )
					deltaDL = (A.get(k) ) * ((A.get(k)-1.) ) * FdivY2vector.get(k); 
				else
					deltaDL = a.get(k) * diagFa[k]; // giagFa =  diagF * a
				dL += deltaDL;
			}

		} else { /* F has general form */
			
			for (z = 0; z < numStates; z++){
			    for (k = 0; k < numStates; k++){
			        accum = 0; 
			        qdivy = QdivY.data[idx];
			        for(l=0; l < numStates; l++) {
			        	if (k != l) {
			        		//if (Q.get(l,z) > 0) {
			        		//accum += FG.get(k,l) *  Q.get(l,z)/ Math.max(Q.get(l,z), Y.get(l));
			        		accum += FG.get(k,l) *  QdivY.get(l,z); 
			        		//}
			        		//if (Q.get(k,z) > 0) {
			        			//accum -= FG.get(l,k) *  Q.get(k,z)/  Math.max(Q.get(k,z), Y.get(k));
			        			// accum -= FG.get(l,k) *  QdivY.get(k,z); 
			        		accum -= FG.get(l,k) * qdivy; 
			        		//}
			        	}
			        	//if (Q.get(k,z) > 0) {
			        		//accum -= F.get(k,l) * a.get(l) * Q.get(k,z)/  Math.max(Q.get(k,z), Y.get(k));
			        		//accum -= F.get(k,l) * a.get(l) * QdivY.get(k,z);  // replaced by sumRowsF
			        	accum -= F.get(k,l) * a.get(l) * qdivy;
			        	//}
			        }
			        	
			        dql[idx] = accum; //dQ.put(k,z,accum);
			        
			        if (z == k && A.get(z) >= 1. ){
						 //deltaDL = (A.get(k) / Y.get(k)) * ((A.get(k)-1.) / Y.get(k)) * F.get(k,l) ; 
						 deltaDL = (A.get(k) ) * ((A.get(k)-1.)) * FdivY2.get(k,z) ;
					 } else {
						 deltaDL = a.get(z) * a.get(k) * F.get(k,z);
					 }
					 dL += deltaDL;
			        			        
			        idx++; // (k,z)
			       }
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
