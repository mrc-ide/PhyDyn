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
 * New version of PL equations - constant population version
 */

public class SolverPL2Constant extends SolverIntervalODE implements FirstOrderDifferentialEquations {
	private double[] pl0, pl1;
	private boolean isDiagF;
	private TimeSeriesFGY ts;
	
	private DMatrix F,G;
	DVector Y;

	private int numExtant, dimensionP;
	
	// temps
	private DVector u;
	private DMatrix R, Phi, PhiSum, Pnorm;
	private DVector phiDiag, y2;
	
	static double MIN_Y = 1e-12 ;

	public SolverPL2Constant(STreeLikelihoodODE stlh) {
		super(stlh);
		dimensionP = numStates; // temporary value
		// we could set tsTimes0 and setMinP HERE
		u = new DVector(numStates);		
		R = new DMatrix(numStates,numStates);
		Phi = new DMatrix(numStates,numStates);
		isDiagF = stlh.popModel.isDiagF();
		//isDiagF = false;
		phiDiag = new DVector(numStates);
		y2 = new DVector(numStates);
		System.out.println("PL2 Constant!- isDiagF"+isDiagF);
	}
	
	public boolean initValues(STreeLikelihoodODE stlh) {
		// Pre-compute constant quantities
		// This should be done for the whole tree, not just the interval
		System.out.println("Init Values");
		ts = stlh.ts;
		FGY fgy = ts.getFGY(1);
		Y = fgy.Y;
		if (forgiveY) Y.maxi(1.0); else Y.maxi(MIN_Y);
		F = fgy.F; 
		G = fgy.G; 
		if (isDiagF) {
			F.getDiagonal(phiDiag);
			Y.squarei(y2);
			phiDiag.divi(y2);
			// phiDiag_i = Fii/ (Yi^2)
		} else {
			F.diviRowVector(Y,Phi);
			Phi.diviColumnVector(Y);
			PhiSum = Phi.transposeSum();
		}
		if (isDiagF) {
			R.put(0);
			R.addi(G);
			R.diviRowVector(Y);
			DVector sumCols = R.columnSums(); // row vector
			for(int i=0; i < numStates; i++)
				R.put(i,i,R.get(i,i)-sumCols.get(i)); // set diagonal
		}
		return false;
	}
	
	
	public void solve(double h0, double h1, int lastPoint, STreeLikelihoodODE stlh) { 		
		ts = stlh.ts;
		StateProbabilities sp = stlh.stateProbabilities;

		numExtant = sp.getNumExtant();
		dimensionP = numStates*numExtant;
		Pnorm =  new DMatrix(numStates,numExtant);
		
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
				
		// no buffer allocation - straight from/to ode solver
		DMatrix P =  new DMatrix(numStates,numExtant,pl);		
		
		// normalise P and store in Pnorm
		P.diviRowVector(P.columnSums(),Pnorm);
				
		// DVector A = P.rowSums(); // pre-allocate - in-place
		DVector Anorm = Pnorm.rowSums(); // pre-allocate - in-place
		DVector anorm = Anorm.div(Y);  // pre-allocate - in-place
		DMatrix PnminusAn = Pnorm.subColumnVector(Anorm);
		
		if (!isDiagF) {
			u.put(1.0);
			u.subi(anorm);  // (1-na)
			// u <= 1
			u.maxi(0.0);
			// 0 <= u <= 1
			// compute R		
			F.muliColumnVector(u,R);  // R = F*u
			R.addi(G);  // R = R+G  --> R = F*u + G
			R.diviRowVector(Y); // R = R /r Y			
			for(int i=0; i < numStates; i++)
				R.put(i,i,0); // set diagonal to zero
			DVector sumCols = R.columnSums(); // row vector
			for(int i=0; i < numStates; i++)
				R.put(i,i,R.get(i,i)-sumCols.get(i)); // set diagonal
		}
				
		
		DMatrix dP = new DMatrix(numStates,numExtant,dpl);
		R.mmuli(P,dP);  // dP = RxP
		
		int k,l,z;
		
		double phisum, Pnorm_kz;
		
		double accum, dL = 0.0;
		int kz  = 0;
		int lz,lk;
		
		if (isDiagF) {
			for (z = 0; z < numExtant; z++){
				lk = 0;		
				for (k = 0; k < numStates; k++){
					phisum = phiDiag.data[k] * (-PnminusAn.data[kz]);
					dL          += Pnorm.data[kz] * phisum;
					dP.data[kz] -= P.data[kz] * phisum * 2.0;
					kz++;
				}	
			}
		} else {
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
						dL += Pnorm_kz * phisum /2.0;
						lz++; lk++;
					}
					dP.data[kz] -= accum;
					//dP.put(k,z, dP.get(k,z) - accum    );
					kz++;
				}	
			}
		}		
		
		dpl[dimensionP] = dL;

	}

	@Override
	public int getDimension() {
		return dimensionP+1;
	}

}
