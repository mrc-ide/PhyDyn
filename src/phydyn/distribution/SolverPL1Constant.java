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

public class SolverPL1Constant extends SolverIntervalODE implements FirstOrderDifferentialEquations {
	private double[] pl0, pl1;
	
	private double tsTimes0;
	private int tsPointLast;
	private TimeSeriesFGY ts;
	//private double sumA0;

	private DVector AmP;
	private int numExtant, dimensionP;
	private DMatrix F,G,R,FdivY;
	private DMatrix Pnorm;
	DVector Y;
	double[] inv2Ne;
	boolean isDiagF;
	
	static double MIN_Y = 1e-12 ;

	public SolverPL1Constant(STreeLikelihoodODE stlh) {
		super(stlh);
		dimensionP = numStates; // temporary value		
		isDiagF = stlh.popModel.isDiagF();
		// we could set tsTimes0 and setMinP HERE
		//System.out.println("PL1 Constant!");
	}
	
	public boolean initValues(STreeLikelihoodODE stlh) {
		// Pre-compute constant quantities
		// This should be done for the whole tree, not just the interval
		ts = stlh.ts;
		FGY fgy = ts.getFGY(1);
		Y = fgy.Y; 
		F = fgy.F; 
		G = fgy.G; 
		R = F.add(G);
		// divide each row by Y
		R.diviRowVector(Y); 
		
		DVector sumCols = R.columnSums(); // row vector
		for(int i=0; i < numStates; i++)
			R.put(i,i,R.get(i,i)-sumCols.get(i)); // diagonal
		
		AmP = new DVector(numStates); // = DoubleMatrix.zeros(numStates, 1);
		
		if (isDiagF) {
			inv2Ne = new double[numStates];
			for(int i=0; i<numStates;i++) {
				inv2Ne[i] = F.get(i,i)/Y.get(i)/Y.get(i);
			} 
		} else {
			//DoubleMatrix Ydm = new DoubleMatrix(numStates,1,Y.data); // igor: temp
			FdivY = F.divRowVector(Y); // F.divRowVector(Ydm)
			FdivY.diviColumnVector(Y); // igor: new dmatrix implementations
		}
		
		return false;
	}
	
	
	public void solve(double h0, double h1, int lastPoint, STreeLikelihoodODE stlh) { 
		tsPointLast = lastPoint;
		tsTimes0 = stlh.tsTimes0;
		
		StateProbabilities sp = stlh.stateProbabilities;
		//A0 = sp.getLineageStateSum(); // numStates column vector

		numExtant = sp.getNumExtant();
		dimensionP = numStates*numExtant;
		Pnorm =  new DMatrix(numStates,numExtant);
				
		// initialise arrays
		pl0 = new double[dimensionP+1];
		pl1 = new double[dimensionP+1];

		// Copy extant to array
		sp.copyProbabilitiesToArray(pl0); 
		pl0[dimensionP] = 0.0;
				
		if ((h1-h0) < stlh.stepSize) {	
			FirstOrderIntegrator newfoi;
			newfoi = new ClassicalRungeKuttaIntegrator((h1-h0)/10);
			if (debug) System.out.println("--- length="+(h1-h0));
			newfoi.integrate(this, h0, pl0, h1, pl1);
		}  else {
			foi.integrate(this, h0, pl0, h1, pl1);
		}
		
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
		if (forgiveY) Y.maxi(1.0); else Y.maxi(MIN_Y);
		
		int idx,k,l,z;
		
		DMatrix P =  new DMatrix(numStates,numExtant,pl);
		
		// DMatrix Pnorm = new DMatrix(P); // P.dup(); 
		
		
		//Pnorm.diviRowVector(Pnorm.columnSums());  // normalise columns		
		
		P.diviRowVector(P.columnSums(), Pnorm); 
		
		double accum, dL = 0.0;
		
		DVector A = P.rowSums(); // column vector		
		
		
		// DMatrix dP = R.mmul(P);
		//double[] dpData = dP.data;
		
		// dP.data = dpl
		DMatrix dP = new DMatrix(numStates,numExtant,dpl);
		R.mmuli(P,dP);
		
		// dL = -dP.sum();  -- this is zero
		idx=0;
		double fdivy;
		for (z = 0; z < numExtant; z++){
			// dPik.col(z) = R * Pik.col(z) ; 			
			//Ampik = clamp( A - Pik.col(z), 0., INFINITY);
			
			//AmP = A.sub(P.getColumn(z));
			
			A.subi( P.getColumn(z), AmP );

			/*
			for (k = 0; k < numStates; k++){
				accum = 0;
				for( l = 0; l < numStates; l++){
					fdivy = FdivY.get(k,l); // Fkl/(YkYl)
					accum += (pdata[idx]*fdivy) * AmP.get(l) ;
					dL += (Pnorm.data[idx]*fdivy) * AmP.get(l);
					
				}
				dpData[idx] = dpData[idx]-accum;
				idx++;
				// dP.put(k, z,dP.get(k,z)-accum);
			}
			*/
			if (isDiagF) { 
				for(k=0; k < numStates; k++) {
					dpl[idx] -= (pl[idx]* inv2Ne[k]* AmP.get(k) );
					dL += (Pnorm.data[idx]* inv2Ne[k] * AmP.get(k));
					idx++;
				}
			} else {
				for (k = 0; k < numStates; k++){
					accum = 0;
					for( l = 0; l < numStates; l++){
						fdivy = FdivY.get(k,l); // Fkl/(YkYl)
						accum += (pl[idx]*fdivy) * AmP.get(l) ;
						dL += (Pnorm.data[idx]*fdivy) * AmP.get(l);
						
					}
					dpl[idx] = dpl[idx]-accum;
					idx++;
					// dP.put(k, z,dP.get(k,z)-accum);
				}
			}
			
		}
		
		
		//idx = 0;
		//double[] dPdata = dP.data;
		//for(k = 0; k < dimensionP; k++) {
		//	dpl[idx] = dPdata[idx];
		//	idx++;
		//}
		dpl[dimensionP] = dL;

	}

	@Override
	public int getDimension() {
		return dimensionP+1;  
	}

}
