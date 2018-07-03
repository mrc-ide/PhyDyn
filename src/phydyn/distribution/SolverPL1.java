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

public class SolverPL1 extends SolverIntervalODE implements FirstOrderDifferentialEquations {
	private double[] pl0, pl1;
	private double tsTimes0;
	private int tsPointLast;
	private TimeSeriesFGY ts;

	private int numExtant, dimensionP;
	
	static double MIN_Y = 1e-12 ;

	public SolverPL1(STreeLikelihoodODE stlh) {
		super(stlh);
		dimensionP = numStates; // temporary value
		// we could set tsTimes0 and setMinP HERE
	}
	
	
	public void solve(double h0, double h1, int lastPoint, STreeLikelihoodODE stlh) { 
		tsPointLast = lastPoint;
		tsTimes0 = stlh.tsTimes0;
		ts = stlh.ts;
		StateProbabilities sp = stlh.stateProbabilities;

		numExtant = sp.getNumExtant();
		dimensionP = numStates*numExtant;
		
		// initialise arrays
		pl0 = new double[dimensionP+1];
		pl1 = new double[dimensionP+1];
		
		// Copy extant to array
		sp.copyProbabilitiesToArray(pl0); // column = probs		
		pl0[dimensionP] = 0.0;

		foi.integrate(this, h0, pl0, h1, pl1);		
		
		// copy new state probabilities
		sp.copyProbabilitiesFromArray(pl1);
				
		if (stlh.setMinP) {
			sp.setMinP(stlh.minP);
		}

		logLh = -pl1[dimensionP];
		return;
	}

	@Override
	public void computeDerivatives(double h, double[] pl, double[] dpl)
			throws MaxCountExceededException, DimensionMismatchException {
		
		int tsPointCurrent = ts.getTimePoint(tsTimes0-h, tsPointLast);
		
		tsPointLast = tsPointCurrent;

		FGY fgy = ts.getFGY(tsPointCurrent);
		DVector Y = fgy.Y; 
		DMatrix F = fgy.F; 
		DMatrix G = fgy.G; 
		
		if (forgiveY) Y.maxi(1.0); else Y.maxi(MIN_Y);
		
		DMatrix R = F.add(G);
		// divide each row by Y
		R.diviRowVector(Y);		
		DVector sumCols = R.columnSums(); // row vector
		for(int i=0; i < numStates; i++)
			R.put(i,i,R.get(i,i)-sumCols.get(i)); // diagonal
				
		DMatrix P =  new DMatrix(numStates,numExtant,pl);
		
		//P.maxi(0.0); // added due to negative probabilities	I-- igor
		
		DMatrix Pnorm = new DMatrix(P);  // P.dup();
		Pnorm.diviRowVector(Pnorm.columnSums());  // normalise columns		
		
		double accum, dL = 0.0;
		
		DVector A = P.rowSums(); // column vector  igor: was Avec	
		//DVector Anorm = Pnorm.rowSums();
		//DVector AmPnorm; // = DoubleMatrix.zeros(numStates, 1);
		DVector AmP, pcol;
		DMatrix PdivY = P.divColumnVector(Y);
		DMatrix PnormdivY = Pnorm.divColumnVector(Y);
		DMatrix FdivY = F.divRowVector(Y);
		
		DMatrix dP = new DMatrix(numStates,numExtant,dpl);
		R.mmuli(P,dP); 
		int k,l,z;
		// dL = -dP.sum();  -- this is zero
		int kz =0, klF; // P.start == dP.start == Pnorm.start == 0
		
		
		for (z = 0; z < numExtant; z++){
			// dPik.col(z) = R * Pik.col(z) ; 			
			//Ampik = clamp( A - Pik.col(z), 0., INFINITY);
			pcol = P.getColumn(z);  // no memory allocation for buffer
			AmP = A.sub(pcol);
			AmP.maxi(0.0);
			//AmPnorm = Anorm.sub(Pnorm.getColumn(z));
			//AmPnorm.maxi(0.0);
			for (k = 0; k < numStates; k++){
				accum = 0;
				klF = F.start + k;	
				for( l = 0; l < numStates; l++){
					// accum += (P.get(k,z)/Y.get(k)) * F.get(k,l) * AmP.get(l) / Y.get(l);
					// dL += (Pnorm.get(k,z)/Y.get(k)) * F.get(k,l) * AmP.get(l) / Y.get(l);					
					accum += (PdivY.data[kz]) * FdivY.data[klF] * AmP.get(l) ;
					dL += (PnormdivY.data[kz]) *  FdivY.data[klF] * AmP.get(l) ;
					//dPik(k,z) -= (Pik(k,z)/Y(k)) * F(k,l) * Ampik(l) / Y(l) ; 
					//dL += (Pik(k,z)/Y(k)) * F(k,l) * Ampik(l) / Y(l) ; 
					klF += numStates;
				}
				dP.data[kz] -= accum;
				// dP.put(k, z,dP.get(k,z)-accum);
				//dL += accum;
				kz++;
			}
		}		
		dpl[dimensionP] = dL;

	}

	@Override
	public int getDimension() {
		return dimensionP+1;  // variable - let's hope it's not a problem
	}

}
