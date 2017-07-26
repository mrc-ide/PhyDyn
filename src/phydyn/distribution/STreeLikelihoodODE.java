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

/**
 * @author Igor Siveroni
 */

@Description("Calculates the probability of a beast.tree using under a structured population model "
		+ "using the framework of Volz (2012). Lineage state probabilities are calculated by"
		+ "solving ODEs at each step in the tree's interval")
@Citation("Erik M. Volz. 2012. Complex population dynamics and the coalescent under neutrality."
		+ "Genetics. 2013 Nov;195(3):1199. ")

enum IntegrationMethod { MIDPOINT, CLASSICRK, GILL
	,HIGHAMHALL 
	,ADAMSBASHFORTH 
	,ADAMSMOULTON  
	};


public class STreeLikelihoodODE extends STreeLikelihood {
	
	public final static double MIN_H = 1.0e-8;
	public IntegrationMethod method;
	public boolean fixedStepSize, setMinP;
	public double stepSize, aTol, rTol, minP;
	public int order;
	
	public Input<Boolean> solvePLInput = new Input<>(
			"solvePL", "Solve dP equations instead of dQ", new Boolean(false));
	
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
	 
	 private SolverInterval solver=null; 
	 
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
		 if (solvePLInput.get()) {
			 solver = new SolverPL(this);
		 } else {
			 solver = new SolverQL(this);
		 }
	 }


	 /* updates t,h,tsPoint and lineage probabilities */
	 protected double processInterval(int interval, double intervalDuration, double[] tsTimes ) {
		 double lh=0.0;
		 double hEvent = h + intervalDuration; 		// event height
		 double tEvent = tsTimes[0] - hEvent;      // event time  
		 		 
		 // New call - update probs and compute likelihood contribution
		 // sorry, no dynamic dispatch yet
		 if (intervalDuration > MIN_H) {
			 solver.solve(h,hEvent,tsPoint,this);
			 lh = solver.getLogLh();
		 }
		 
		 /* update tsPoint, h and t */
		 while (tsTimes[tsPoint+1] > tEvent) {
	    		tsPoint++;
		 }		 
		 h = hEvent;
		 t = tsTimes[0] - h;
		 return lh;
	 }

	
}
