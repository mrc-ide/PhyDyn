package phydyn.distribution;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;


//import org.jblas.DoubleMatrix;

import phydyn.model.TimeSeriesFGY;
import phydyn.model.TimeSeriesFGY.FGY;
import phydyn.model.TimeSeriesFGYStd;
import phydyn.util.DMatrix;
import phydyn.util.DVector;

public class Solverfwd extends SolverIntervalForward implements FirstOrderDifferentialEquations {
	
	private int numStatesSQ;	
	private double[] q0, q1;
	 
	private int tsPointLast;
	private TimeSeriesFGY ts;
	private FirstOrderIntegrator foi;

	 static double MIN_Y = 1e-12 ;
	 
	public Solverfwd(STreeLikelihood stlh) {  // assuming the number of states never change
		super(stlh);
		numStatesSQ = numStates*numStates;
		q0 = new double[numStatesSQ];
    	q1 = new double[numStatesSQ];
	}
	
	
	public void solve(double t0, double t1, int lastPoint, STreeLikelihood stlh) {
		ts = stlh.ts;		
		tsPointLast = lastPoint;
		
		// prepare q arrays
		int k=0;
		for(int i = 0; i < numStates; i++) {
			for(int j=0; j < numStates; j++) {
				if (i==j) 
					q0[k] = 1.0;
				else 
					q0[k] = 0.0;
				q1[k] = 0.0;
				k++;
			}
		}
		if ((t1-t0)<0.00000001) return;
		double stepSize=0.001;
		if ((t1-t0) < 100*stepSize) {
			stepSize = Math.max(0.000001,(t1-t0)/100);
		}
		foi = new ClassicalRungeKuttaIntegrator(stepSize);
		foi.integrate(this, t0, q0, t1, q1);
						
		DMatrix Q = new DMatrix(numStates,numStates,q1);
		
		// stlh.stateProbabilities.mulExtantProbabilities(Q.transpose(), true);
		stlh.stateProbabilities.mulExtantProbabilities(Q, true);
	}

	@Override
	public void computeDerivatives(double t, double[] q, double[] dq)
			throws MaxCountExceededException, DimensionMismatchException {

		
		int tsPointCurrent = ts.getTimePoint(t, tsPointLast);		
		tsPointLast = tsPointCurrent;
		FGY fgy = ts.getFGY(tsPointCurrent);
		DVector Y = fgy.Y; // ts.getYs()[tsPointCurrent];
		DMatrix F = fgy.F; // ts.getFs()[tsPointCurrent];
		DMatrix G = fgy.G; // ts.getGs()[tsPointCurrent];
		
		//DoubleMatrix D = DoubleMatrix.zeros(numStates);
		DVector D = fgy.D;
		DMatrix Q = new DMatrix(numStates,numStates,q);
				
		
		int k,z,l,idx;
		//dQQ
		idx=0; // dq[idx] = dQ(k,z)
		for (z = 0; z < numStates; z++){ // col of Q
			for (k = 0; k < numStates; k++){ //row of Q
				dq[idx] = 0. ; 
				for (l = 0; l < numStates; l++){
					if (k!=l){
						if ( Q.get(l,z) > 0) {
							dq[idx] += (F.get(l,k)/2. + G.get(l,k))*Q.get(l,z)/Math.max(Q.get(l,z),Y.get(l));
						}
						if (Q.get(k,z) > 0) {
							dq[idx] -= (F.get(k,l)/2. + G.get(k,l))*Q.get(k,z)/Math.max(Q.get(k,z),Y.get(k));
						}
					}
				}
				// death:
				if (Q.get(k,z) > 0) {
					dq[idx] -= Q.get(k,z) * D.get(k) / Math.max(Q.get(k,z), Y.get(k));
				}
				idx++;
			}
		}
			
	}


	@Override
	public int getDimension() {
		return numStatesSQ;
	}

}
