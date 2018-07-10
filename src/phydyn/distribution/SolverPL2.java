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

/*
 * @author Igor Siveroni
 * Proposed new version of PL equation systems - under development
 */

public class SolverPL2 extends SolverIntervalODE implements FirstOrderDifferentialEquations {
	private double[] pl0, pl1;
	private double tsTimes0;
	private int tsPointLast;
	private TimeSeriesFGY ts;

	private int numExtant, dimensionP;
	
	// temps
	private DVector u;
	private DMatrix R, Phi;
	
	static double MIN_Y = 1e-12 ;

	public SolverPL2(STreeLikelihoodODE stlh) {
		super(stlh);
		dimensionP = numStates; // temporary value
		// we could set tsTimes0 and setMinP HERE
		u = new DVector(numStates);
		R = new DMatrix(numStates,numStates);
		Phi = new DMatrix(numStates,numStates);
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
		
		// no buffer allocation - straight from/to ode solver
		DMatrix P =  new DMatrix(numStates,numExtant,pl);
		
		
		DMatrix Pnorm = new DMatrix(P);  // P.dup();
		Pnorm.diviRowVector(Pnorm.columnSums());  // normalise columns		
				
		// DVector A = P.rowSums(); // pre-allocate - in-place
		DVector Anorm = Pnorm.rowSums(); // pre-allocate - in-place
		DVector anorm = Anorm.div(Y);  // pre-allocate - in-place
		DMatrix PnminusAn = Pnorm.subColumnVector(Anorm);
		
		u.put(1.0);
		u.subi(anorm);  // (1-na)
		// u <= 1
		u.maxi(0.0);
		// 0 <= u <= 1
		
		// compute R		
		F.muliColumnVector(u,R);
		R.addi(G);
		R.diviRowVector(Y);
		for(int i=0; i < numStates; i++)
			R.put(i,i,0); // set diagonal to zero
		DVector sumCols = R.columnSums(); // row vector
		for(int i=0; i < numStates; i++)
			R.put(i,i,R.get(i,i)-sumCols.get(i)); // set diagonal
		
		DMatrix dP = new DMatrix(numStates,numExtant,dpl);
		R.mmuli(P,dP); 
		
		
		F.diviRowVector(Y,Phi);
		Phi.diviColumnVector(Y);
		
		DMatrix PhiSum = Phi.transposeSum();

		// Matrix PnormdivY = Pnorm.divColumnVector(Y);
		
		int k,l,z;
		
		double phisum, Pnorm_kz;
		
		double accum, dL = 0.0;
		int kz  = 0;
		int lz,lk;
		for (z = 0; z < numExtant; z++){
			lk = 0;		
			for (k = 0; k < numStates; k++){
				accum = 0;
				Pnorm_kz = Pnorm.data[kz];
				// Pnorm_kz = Pnorm.get(k, z);
				lz = z * numStates;
				for( l = 0; l < numStates; l++) {
					// phisum = PhiSum.get(l, k) * (-PnminusAn.data[lz]);
					phisum = PhiSum.data[lk] * (-PnminusAn.data[lz]);
					//accum += Pz.get(k) * phisum * (Anorm.get(l)-Pnorm.get(l, z));
					accum += P.data[kz] * phisum;
					//dL += nPik(k,z) * (phi(k,l)+phi(l,k)) * (nA(l)- nPik(l,z))/ 2.; 
					dL += Pnorm_kz * phisum  /2.0;
					lz++; lk++;
				}
				dP.data[kz] -= accum;
				//dP.put(k,z, dP.get(k,z) - accum    );
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
