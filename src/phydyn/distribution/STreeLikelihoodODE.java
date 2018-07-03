package phydyn.distribution;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.jblas.DoubleMatrix;

import beast.core.Citation;
import beast.core.Description;
import beast.core.Input;
import phydyn.model.TimeSeriesFGY;

/**
 * @author Igor Siveroni
 */

@Description("Calculates the probability of a beast.tree  under a structured population model "
		+ "using the framework of Volz (2012). Lineage state probabilities are calculated by"
		+ "solving ODEs at each step in the tree's interval")
@Citation("Erik M. Volz. 2012. Complex population dynamics and the coalescent under neutrality."
		+ "Genetics. 2013 Nov;195(3):1199. ")

enum IntegrationMethod { MIDPOINT, CLASSICRK, GILL
	,HIGHAMHALL 
	,ADAMSBASHFORTH 
	,ADAMSMOULTON  
	};
	
enum EquationsType { PL1, PL2, QL, LogQL };


public class STreeLikelihoodODE extends STreeLikelihood {
	
	public final static double MIN_H = 1.0e-6;
	public IntegrationMethod method;
	public boolean fixedStepSize, setMinP;
	public double stepSize, aTol, rTol, minP;
	public int order;
	public EquationsType eqType;
	
	public Input<Boolean> solvePLInput = new Input<>(
			"solvePL", "Solve dP equations instead of dQ");
	
	public Input<String> equationsInput = new Input<>(
			"equations", "Type of likelihood and state differential equations");
	
	public Input<Double> stepSizeInput = new Input<>("stepSize",
			"ODE solver stepsize", new Double(0.01));
	
	 public Input<String> methodInput = new Input<>(
			 "method","Integration method");
	 
	 public Input<Double> rTolInput = new Input<>(
			 "rTol", "relative tolerance", new Double(0.0001));
	 
	 public Input<Double> aTolInput = new Input<>(
			 "aTol", "absolute tolerance", new Double(0.00001));
	 
	 public Input<Integer> orderInput = new Input<>(
			 "order", "order(k) of adaptive size integration method", new Integer(3)); 
	 
	 public Input<Double> minPInput = new Input<>("minP",
			 "minimum value of state probilities i.e. avoid zero");
	 
	 private SolverIntervalODE solver=null; 
	 
	 @Override
	 public void initAndValidate() {
		 super.initAndValidate(); /* important: call first */
		 fixedStepSize = true;
		 if (methodInput.get()==null) {  // set to default: ClassicalRungeKutta
			 method = IntegrationMethod.CLASSICRK;
		 } else {
			 String strMethod = methodInput.get();
			 if (strMethod.equals("midpoint")) method = IntegrationMethod.MIDPOINT;
			 else if (strMethod.equals("classicrk")) method = IntegrationMethod.CLASSICRK;
			 else if (strMethod.equals("gill")) method = IntegrationMethod.GILL;
			 else if (strMethod.equals("adams-bashforth")) { method = IntegrationMethod.ADAMSBASHFORTH; fixedStepSize=false; }
			 else if (strMethod.equals("adams-moulton")) { method = IntegrationMethod.ADAMSMOULTON; fixedStepSize=false; }
			 else if (strMethod.equals("higham-hall")) { method = IntegrationMethod.HIGHAMHALL; fixedStepSize=false; }
			 else throw new IllegalArgumentException("Unknown STreeLikelihood integration method: "+strMethod+
						" - use: midpoint/classicrk/gill adaptive: adams-bashforth, adams-moulton, higham-hall");
		 }
		 // they all have defaults
		 stepSize = stepSizeInput.get();
		 aTol = aTolInput.get();
		 rTol = rTolInput.get();
		 order = orderInput.get();
		 setMinP=false;
		 if (minPInput.get()!=null) {
			 minP = minPInput.get();
			 if (minP > 0.1) {
				 throw new IllegalArgumentException("Minimum state probability value must be less than 0.1");
			 }
			 setMinP=true;
		 }
		 // Equations / solver.
		 if (equationsInput.get()!=null) {
			 String eq = equationsInput.get();
			 if (eq.equals("PL")) eqType = EquationsType.PL1;
			 else if (eq.equals("PL1")) eqType = EquationsType.PL1;
			 else if (eq.equals("PL2")) eqType = EquationsType.PL2;
			 else if (eq.equals("QL")) eqType = EquationsType.QL;
			 else if (eq.equals("LogQL")) eqType = EquationsType.LogQL;
			 else 
				 throw new IllegalArgumentException("Invalid Equations type. Use: PL1, PL2, QL or LogQL");			 
			 // if there's solverInput, even if redundant, they must agree
			 if (solvePLInput.get()!=null) {
				 if ((solvePLInput.get() && (eqType!=EquationsType.PL1)) ||
					 (!solvePLInput.get() && (eqType!=EquationsType.QL))) {
					 throw new IllegalArgumentException("Incompatible values of solverPL and equations");
				 }
			 } 
		 } else { // legacy
			 eqType = EquationsType.PL1; // default
			 if ( (solvePLInput.get()!=null)&& (!solvePLInput.get())) {				 
					 eqType = EquationsType.QL;									
			 }			 
		 }
		 switch (eqType) {
		 case PL1:
			 if (popModel.isConstant())
				solver = new SolverPLConstant(this);
			 else
			 	solver = new SolverPL1(this); 
			 break;
		 case PL2:
			 if (popModel.isConstant())
				 solver = new SolverPLConstant(this);
			 else
				solver = new SolverPL2(this); 
			    //solver = new SolverPL2Slow(this); 
			 break;
		 case QL:
			 if (popModel.isConstant()) {
				 solver = new SolverQLConstant(this);
			 } else
				 solver = new SolverQL(this); 
			 break;
		 default:
			 solver = new SolverLogQL(this);
		 }
		 
	 }
	 
	 public boolean initValues() {
		 if (super.initValues())
			 return true;
		 solver.initValues(this);
		 return false;
	 }


	 /* updates t,h,tsPoint and lineage probabilities */
	 protected double processInterval(int interval, double intervalDuration, TimeSeriesFGY ts) {
		 double lh=0.0;
		 double hEvent = h + intervalDuration; 		// event height
		 double tEvent = ts.getTime(0) - hEvent;      // event time
		 // Update probs and compute likelihood contribution
		 if (intervalDuration > MIN_H) {
			 solver.solve(h,hEvent,tsPoint,this);
			 lh = solver.getLogLh();
		 }
		 //if (interval==15) solver.setDebug(false);
		 /* update tsPoint, h and t */

		 if (ts.getTime(tsPoint) > tEvent) {
			 while (ts.getTime(tsPoint+1) > tEvent) {
				 tsPoint++;
			 }
		 }		 
		 h = hEvent;
		 t = ts.getTime(0) - h;
		 return lh;
	 }

	
}
