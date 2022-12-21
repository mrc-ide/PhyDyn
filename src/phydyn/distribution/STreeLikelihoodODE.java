package phydyn.distribution;

import beast.base.core.Citation;
import beast.base.core.Description;
import beast.base.core.Input;
import beast.base.core.Log;
import beast.base.evolution.tree.TraitSet;
import phydyn.evolution.tree.coalescent.STreeIntervals;
import phydyn.model.PopModel;
import phydyn.model.TimeSeriesFGY;
import phydyn.util.General.IntegrationMethod;

/**
 * @author Igor Siveroni
 */

@Description("Calculates the probability of a beast.tree  under a structured population model "
		+ "using the framework of Volz (2012). Lineage state probabilities are calculated by"
		+ "solving ODEs at each step in the tree's interval")
@Citation("Erik M. Volz. 2012. Complex population dynamics and the coalescent under neutrality."
		+ "Genetics. 2013 Nov;195(3):1199. ")


	
enum EquationsType { PL, PL1, PL2, QL, LogQL };


public class STreeLikelihoodODE extends STreeLikelihood {

	
	public final static double MIN_H = 1.0e-6;
	public IntegrationMethod method;
	public boolean fixedStepSize;
	public double stepSize, aTol, rTol;
	public int order;
	public EquationsType eqType;
	private static final EquationsType defaultEquations =  EquationsType.PL2;
	
	
	public Input<Boolean> solvePLInput = new Input<>(   // legacy  - remove soon
			"solvePL", "Solve dP equations instead of dQ");
	
	public Input<EquationsType> equationsInput = new Input<EquationsType>(
			"equations", "Type of likelihood and state differential equations", defaultEquations, EquationsType.values());
		
	public Input<IntegrationMethod> methodInput = new Input<>(
			 "method","Integration method", IntegrationMethod.CLASSICRK, IntegrationMethod.values() );

	public Input<Double> stepSizeInput = new Input<>("stepSize",
				"ODE solver stepsize", new Double(0.001));	 
	 
	public Input<Double> rTolInput = new Input<>(
			 "rTol", "relative tolerance", new Double(0.0001));
	 
	public Input<Double> aTolInput = new Input<>(
			 "aTol", "absolute tolerance", new Double(0.00001));
	 
	public Input<Integer> orderInput = new Input<>(
			 "order", "order(k) of adaptive size integration method", new Integer(3)); 
	 
	
	 
	 private SolverIntervalODE solver=null; 
	 
	 public STreeLikelihoodODE() {}
	 
	 public STreeLikelihoodODE(PopModel model, STreeIntervals intervals,TraitSet typeTrait) {
		 // inputs.get(i++).setValue(object, this);
		 treeIntervalsInput.setValue(intervals, this);
		 popModelInput.setValue(model, this);
		 equationsInput.setValue(defaultEquations, this);
		 if (typeTrait!=null)
			 typeTraitInput.setValue(typeTrait, this);
		 initAndValidate();
	 }
	 
	 @Override
	 public void initAndValidate() {
		 super.initAndValidate(); /* important: call first */
		 fixedStepSize = true;
		 
		 method = methodInput.get();
		 
		 //Log.warning("Method = "+ method.toString() + " / " +method.name());
		 
		 // they all have defaults
		 stepSize = stepSizeInput.get();
		 aTol = aTolInput.get();
		 rTol = rTolInput.get();
		 order = orderInput.get();
		 
		 if (solvePLInput.get()!=null) {
			 Log.warning("(phydyn) STreeLikelihood: solvePL option deprecated.\nUse equations = 'PL | PL1 | PL2 | QL' instead.\n"
			 		+ "Ignoring solvePL. Using default value of equations (equations='PL2') if equations parameter is not set.");
		 }
		 // Equations / solver.
		 if (equationsInput.get()!=null) {
			 eqType = equationsInput.get();			 
			 // if there's solverInput, even if redundant, they must agree
			 
		 } else { // legacy
			throw new IllegalArgumentException("Unexpected behaviour - equations paramter must have a default");
		 }
		 switch (eqType) {
		 case PL1:
			 if (popModel.isConstant())
				solver = new SolverPL1Constant(this);
			 else
			 	solver = new SolverPL1(this); 
			 break;
		 case PL2:
			 if (popModel.isConstant())
				 solver = new SolverPL2Constant(this);
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
		 //solver.setDebug(true);;
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
