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

public class SolverNewPL extends SolverIntervalODE implements FirstOrderDifferentialEquations {
	private double[] pl0, pl1;
	private double tsTimes0;
	private int tsPointLast;
	private TimeSeriesFGY ts;

	private int numExtant, dimensionP;
	
	static double MIN_Y = 1e-12 ;

	public SolverNewPL(STreeLikelihoodODE stlh) {
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
					
		// no buffer allocation - straight from/to ode solver
		DMatrix P =  new DMatrix(numStates,numExtant,pl);
		DMatrix dP = new DMatrix(numStates,numExtant,dpl);
		
		DMatrix Pnorm = new DMatrix(P);  // P.dup();
		Pnorm.diviRowVector(Pnorm.columnSums());  // normalise columns		
				
		// DVector A = P.rowSums(); // pre-allocate - in-place
		DVector Anorm = Pnorm.rowSums(); // pre-allocate - in-place
		DVector anorm = Anorm.div(Y);  // pre-allocate - in-place
		
		// DVector u = new DVector(numStates);
		
		DMatrix Phi = F.divRowVector(Y);
		Phi.diviColumnVector(Y);		

		DMatrix PnormdivY = Pnorm.divColumnVector(Y);
		
		int k,l,z;

		
		double accum, dL = 0.0;
		for (z = 0; z < numExtant; z++){
			DVector u = PnormdivY.getColumn(z).sub(anorm);
			u.add(1.0);
			u.maxi(0.0);
			
			DMatrix R = F.mulColumnVector(u);
			R.addi(G);
			R.diviRowVector(Y);
				
			DVector sumCols = R.columnSums(); // row vector
			for(int i=0; i < numStates; i++)
				R.put(i,i,R.get(i,i)-sumCols.get(i)); // set diagonal
			
			// pointer to column
			DVector dPz = dP.getColumn(z);
			DVector Pz = P.getColumn(z);
			
			// dPik.col(z) = R * Pik.col(z) ; 
			dPz.copy(Pz.rmul(R));
			
			for (k = 0; k < numStates; k++){
				accum = 0;
				for( l = 0; l < numStates; l++) {
					//dPik(k,z) -= Pik(k,z) * (phi(k,l)+phi(l,k)) * (nA(l)- nPik(l,z)); 
					accum += Pz.get(k)*(Phi.get(k,l)+Phi.get(l,k))*(Anorm.get(l)-Pnorm.get(l, z));
					//dL += nPik(k,z) * (phi(k,l)+phi(l,k)) * (nA(l)- nPik(l,z))/ 2.; 
					dL += Pnorm.get(k,z)*(Phi.get(k,l)+Phi.get(l,k))*(Anorm.get(l)-Pnorm.get(l, z))/2.0;
				}
				dPz.put(k, dPz.get(k) - accum  );
			}
			
			
		}
		
		dpl[dimensionP] = dL;

	}

	@Override
	public int getDimension() {
		return dimensionP+1;  // variable - let's hope it's not a problem
	}

}
