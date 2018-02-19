package phydyn.distribution;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.jblas.DoubleMatrix;

import phydyn.model.TimeSeriesFGY;
import phydyn.model.TimeSeriesFGY.FGY;
import phydyn.model.TimeSeriesFGYStd;

public class SolverPL extends SolverIntervalODE implements FirstOrderDifferentialEquations {
	private double[] pl0, pl1;
	
	private double tsTimes0;
	private int tsPointLast, sizeP;
	private TimeSeriesFGY ts;
	private double sumA0;
	private DoubleMatrix A0;
	private int numExtant, dimensionP;
	
	static double MIN_Y = 1e-12 ;

	public SolverPL(STreeLikelihoodODE stlh) {
		super(stlh);
		dimensionP = numStates; // temporary value		
		// we could set tsTimes0 and setMinP HERE
	}
	
	
	
	public void solve(double h0, double h1, int lastPoint, STreeLikelihoodODE stlh) { 
		tsPointLast = lastPoint;
		tsTimes0 = stlh.tsTimes0;
		ts = stlh.ts;
		StateProbabilities sp = stlh.stateProbabilities;
		A0 = sp.getLineageStateSum(); // numStates column vector
		sumA0 = A0.sum();
		numExtant = sp.getNumExtant();
		dimensionP = numStates*numExtant;
		
		// initialise arrays
		pl0 = new double[dimensionP+1];
		pl1 = new double[dimensionP+1];
		int idx=0;
		DoubleMatrix probs;
		
		// Copy extant to array
		sp.copyProbabilitesToArray(pl0); // column = probs
		
		//for(int i = 0; i < numExtant; i++) {
		//	probs = stlh.extantProbs[i];
		//	for(int j=0; j < numStates; j++) {
		//		pl0[idx++] = probs.get(j);
		//	}
		//}
		pl0[dimensionP] = 0.0;
		
		//for(int i=0; i <= dimensionP; i++) System.out.print(pl0[i]+ " ");
		//System.out.println("");
		foi.integrate(this, h0, pl0, h1, pl1);		
		
		// copy new state probabilities
		idx=0;
		//DoubleMatrix A = DoubleMatrix.zeros(numStates);
		
		for(int i = 0; i < numExtant; i++) {
			//probs = stlh.extantProbs[i];
			probs = sp.getExtantProbsFromIndex(i);
			//System.out.println("probs: "+probs);
			//A.addi(probs);
			for(int j=0; j < numStates; j++) {
				probs.put(j,pl1[idx]);
				idx++;
			}
			probs.maxi(0.0);
			probs.divi(probs.sum());
			
			//System.out.println("probs "+i+" "+ sp.getStateProbsFromIndex(i));
		}
		if (stlh.setMinP) {
			sp.setMinP(stlh.minP);
		}
		//System.out.println("Asum="+A.sum()+"  A="+A);
		logLh = -pl1[idx];
		// set lh
		return;
	}

	@Override
	public void computeDerivatives(double h, double[] pl, double[] dpl)
			throws MaxCountExceededException, DimensionMismatchException {
		
		int tsPointCurrent = ts.getTimePoint(tsTimes0-h, tsPointLast);
		
		tsPointLast = tsPointCurrent;

		FGY fgy = ts.getFGY(tsPointCurrent);
		DoubleMatrix Y = fgy.Y; 
		DoubleMatrix F = fgy.F; 
		DoubleMatrix G = fgy.G; 
		
		if (forgiveY) Y.maxi(1.0); else Y.maxi(MIN_Y);
		
		DoubleMatrix R = F.add(G);
		// divide each row by Y
		R.diviRowVector(Y);
		DoubleMatrix sumCols = R.columnSums(); // row vector
		for(int i=0; i < numStates; i++)
		R.put(i,i,R.get(i,i)-sumCols.get(i)); // diagonal
		double[] pdata = new double[dimensionP];
		
		int idx,k,l,z;
		idx = 0;
		for(k = 0; k < dimensionP; k++) {
			pdata[idx] = pl[idx];
			idx++;
		}
		DoubleMatrix P =  new DoubleMatrix(numStates,numExtant,pdata);
		
		// DoubleMatrix dP = DoubleMatrix.zeros(numStates, numExtant);
		//if (h < 0.02) System.out.println("h="+h+" P="+P);

		//P.maxi(0.0); // added due to negative probabilities	I-- igor
		
		DoubleMatrix Pnorm = P.dup();
		Pnorm.diviRowVector(Pnorm.columnSums());  // normalise columns		
		
		// Check for A
		
		//System.out.println("P="+P);
		
		double accum, dL = 0.0;
		
		DoubleMatrix A = P.rowSums(); // column vector		
		DoubleMatrix Anorm = Pnorm.rowSums();
		DoubleMatrix a = A.div(Y); 
		DoubleMatrix AmP, AmPnorm; // = DoubleMatrix.zeros(numStates, 1);
		DoubleMatrix dP = R.mmul(P);
		
		// dL = -dP.sum();  -- this is zero
		for (z = 0; z < numExtant; z++){
			// dPik.col(z) = R * Pik.col(z) ; 
			
			//Ampik = clamp( A - Pik.col(z), 0., INFINITY);
			AmP = A.sub(P.getColumn(z));
			AmP.maxi(0.0);
			AmPnorm = Anorm.sub(Pnorm.getColumn(z));
			AmPnorm.maxi(0.0);
			//if (h < 00.2) System.out.println("AmP="+AmP);
			for (k = 0; k < numStates; k++){
				accum = 0;
				for( l = 0; l < numStates; l++){
					accum += (P.get(k,z)/Y.get(k)) * F.get(k,l) * AmP.get(l) / Y.get(l);
					dL += (Pnorm.get(k,z)/Y.get(k)) * F.get(k,l) * AmP.get(l) / Y.get(l);
					//dPik(k,z) -= (Pik(k,z)/Y(k)) * F(k,l) * Ampik(l) / Y(l) ; 
					//dL += (Pik(k,z)/Y(k)) * F(k,l) * Ampik(l) / Y(l) ; 
				}
				//if (h < 00.2) System.out.print(accum+" ");
				dP.put(k, z,dP.get(k,z)-accum);
				//dL += accum;
			}
			//if (h < 00.2) System.out.println("");
		}
		// from solveQL
		//for (k= 0; k < numStates; k++){
		//    for (l =0 ; l < numStates; l++){			
		//    	if (k == l && A.get(k) >= 1. ){
		//    		dL += (A.get(k) / Y.get(k)) * ((A.get(k)-1.) / Y.get(k)) * F.get(k,l) ; 
		//      	} else {
		//      		dL += a.get(k) * a.get(l) * F.get(k,l);
		//      	}
		//    }
		//}
		//if (h < 00.2) System.out.println("DL="+dL);
		// copy dP to array
		
		
		idx = 0;
		double[] dPdata = dP.data;
		for(k = 0; k < dimensionP; k++) {
			dpl[idx] = dPdata[idx];
			idx++;
		}
		dpl[idx] = dL;

	}

	@Override
	public int getDimension() {
		return dimensionP+1;  // variable - let's hope it's not a problem
	}

}
