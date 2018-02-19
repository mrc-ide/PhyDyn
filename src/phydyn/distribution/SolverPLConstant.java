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

public class SolverPLConstant extends SolverIntervalODE implements FirstOrderDifferentialEquations {
	private double[] pl0, pl1;
	
	private double tsTimes0;
	private int tsPointLast, sizeP;
	private TimeSeriesFGY ts;
	private double sumA0;
	private DoubleMatrix A0;
	private int numExtant, dimensionP;
	DoubleMatrix F,G,Y,R,FdivY;
	double[] inv2Ne;
	boolean isDiagF;
	
	static double MIN_Y = 1e-12 ;

	public SolverPLConstant(STreeLikelihoodODE stlh) {
		super(stlh);
		dimensionP = numStates; // temporary value		
		isDiagF = stlh.popModel.isDiagF();
		// we could set tsTimes0 and setMinP HERE
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
		DoubleMatrix sumCols = R.columnSums(); // row vector
		for(int i=0; i < numStates; i++)
		R.put(i,i,R.get(i,i)-sumCols.get(i)); // diagonal
		
		if (isDiagF) {
			inv2Ne = new double[numStates];
			for(int i=0; i<numStates;i++) {
				inv2Ne[i] = F.get(i,i)/Y.get(i)/Y.get(i);
			} 
		} else {
			FdivY = F.divRowVector(Y);
			FdivY.diviColumnVector(Y);
		}
		
		return false;
	}
	
	
	public void solve(double h0, double h1, int lastPoint, STreeLikelihoodODE stlh) { 
		tsPointLast = lastPoint;
		tsTimes0 = stlh.tsTimes0;
		
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
		

		pl0[dimensionP] = 0.0;
				
		if ((h1-h0) < stlh.stepSize) {	
			FirstOrderIntegrator newfoi;
			newfoi = new ClassicalRungeKuttaIntegrator((h1-h0)/10);
			if (debug) System.out.println("--- length="+(h1-h0));
			newfoi.integrate(this, h0, pl0, h1, pl1);
		}  else {
			foi.integrate(this, h0, pl0, h1, pl1);
		}
		
		// foi.integrate(this, h0, pl0, h1, pl1);
			
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
		}
		if (stlh.setMinP) {
			sp.setMinP(stlh.minP);
		}
		logLh = -pl1[idx];
		return;
	}

	@Override
	public void computeDerivatives(double h, double[] pl, double[] dpl)
			throws MaxCountExceededException, DimensionMismatchException {
		
		int tsPointCurrent = ts.getTimePoint(tsTimes0-h, tsPointLast);
		
		tsPointLast = tsPointCurrent;


		if (forgiveY) Y.maxi(1.0); else Y.maxi(MIN_Y);
		
		
		
		double[] pdata = new double[dimensionP];
		
		int idx,k,l,z;
		idx = 0;
		for(k = 0; k < dimensionP; k++) {
			pdata[idx] = pl[idx];
			idx++;
		}
		DoubleMatrix P =  new DoubleMatrix(numStates,numExtant,pdata);
		
		DoubleMatrix Pnorm = P.dup();
		Pnorm.diviRowVector(Pnorm.columnSums());  // normalise columns		
		

		double accum, dL = 0.0;
		
		DoubleMatrix A = P.rowSums(); // column vector		
		DoubleMatrix AmP; // = DoubleMatrix.zeros(numStates, 1);
		DoubleMatrix dP = R.mmul(P);
		double[] dpData = dP.data;
		
		// dL = -dP.sum();  -- this is zero
		idx=0;
		double fdivy;
		for (z = 0; z < numExtant; z++){
			// dPik.col(z) = R * Pik.col(z) ; 
			
			//Ampik = clamp( A - Pik.col(z), 0., INFINITY);
			AmP = A.sub(P.getColumn(z));
			AmP.maxi(0.0);
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
					dpData[idx] = dpData[idx]- (pdata[idx]* inv2Ne[k]* AmP.get(k) );
					dL += (Pnorm.data[idx]* inv2Ne[k] * AmP.get(k));
					idx++;
				}
			} else {
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
			}
			
		}
		
		
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
