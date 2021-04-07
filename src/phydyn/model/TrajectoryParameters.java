package phydyn.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import beast.core.CalculationNode;
import beast.core.Input;
import beast.core.Input.Validate;
import beast.core.parameter.RealParameter;
import beast.core.util.Log;
import beast.evolution.tree.coalescent.STreeIntervals;
import phydyn.analysis.PopModelAnalysis;
import phydyn.analysis.XMLFileWriter;
import phydyn.util.General.IntegrationMethod;

public class TrajectoryParameters extends CalculationNode {

	 public Input<IntegrationMethod> methodInput = new Input<>(
			 "method","Integration method",IntegrationMethod.CLASSICRK, IntegrationMethod.values());
	 
	 public Input<Double> rTolInput = new Input<>(
			 "rTol", "relative tolerance");
	 
	 public Input<Double> aTolInput = new Input<>(
			 "aTol", "absolute tolerance");
	 
	 public Input<Integer> orderInput = new Input<>(
			 "order", "order(k) of adaptive size integration method");
	 
	 public Input<RealParameter> t0Input = new Input<>(
			 "t0","Initial t (timeseries end)");   // igor - removed requied condition
	 
	 public Input<STreeIntervals> intervalsInput = new Input<>(
			 "treeIntervals","Tree interval used to set t0 = time of root", Validate.XOR, t0Input);  
	 
	 public Input<Double> t1Input = new Input<>(
			 "t1","Final t (timeseries end)");
	 
	 /* We could use 'step size' instead  */
	 public Input<Integer> integrationStepsInput = new Input<>(
			 "integrationSteps","Integration steps",Validate.REQUIRED);
	 
	 public Input<Integer> timeseriesStepsInput = new Input<>(
			 "timeseriesSteps","Number of steps in timeries");
	 
	 public Input<List<ParamValue>> initialValuesInput = new Input<>(
	            "initialValue",
	            "Initial value of state variable involved in the birth-death/migration process. Must"
	            + "match a variable",
	            new ArrayList<>());
	 
	 public double rTol, aTol;
	 public  int integrationSteps, timeseriesSteps, order;
	 public IntegrationMethod method;
	 public boolean fixedStepSize;
	 
	 // new
	 // public double t0;  -- testing
	 //public boolean t0Set;  -- testing
	 STreeIntervals intervals=null;
	 
	 public double t1;
	 public boolean t1Set;
	 	 
	 Map<String, Integer> paramsMap;
	 public int numParams;
	 public String[] paramNames;
	 protected boolean modifiedValues;
	 // Parameters
	 //protected double t0; // sampled - best take value from input
	 //protected double storedT0;
	 public double[] paramValues;
	 protected double[] storedParamValues;
	 

	@Override
	public void initAndValidate()  {
		
		integrationSteps = integrationStepsInput.get();
		if (timeseriesStepsInput.get()==null) {
			timeseriesSteps = integrationSteps;
		} else {
			timeseriesSteps = timeseriesStepsInput.get();
			if (timeseriesSteps > integrationSteps) {
				timeseriesSteps = integrationSteps;
				System.out.println("Number of timeseries steps must be <= integration steps. timeseriesSteps = "+timeseriesSteps);
			}
		}
		
		// new: checking for vector parameters
		for (ParamValue pValue: initialValuesInput.get()) {
			if (pValue.isVector) {
				throw new IllegalArgumentException("Parameter Value "+pValue.names+": Vector parameters "
						+ " not allowed as initial values");
			}
		}
		
	
		/*  -- testing
		if (t1Input.get()!=null) {
			t0 = t0Input.get().getValue();
			t0Set = true;
		} else {
			t0Set = false;
		}
		*/
		
		if (t0Input.get()==null) {
			if (intervalsInput.get()==null) {
				throw new IllegalArgumentException("Programming error: t0 XOR intervals");
			} else {
				intervals = intervalsInput.get();
			}
		}
		
		t1Set=false;
		if (t1Input.get()!=null) {
			t1 = t1Input.get();
			t1Set = true;
		}
		/* Integration Method */
		method = methodInput.get();
		fixedStepSize = true;
		
		if (!fixedStepSize) { // defaults: 0.001, 0.000001;
			if (rTolInput.get()==null) rTol=0.001; else rTol = rTolInput.get();
			if (aTolInput.get()==null) aTol=0.000001; else aTol = aTolInput.get();
			if (orderInput.get()==null) order=4; else order = orderInput.get();
			// need to check valid values of 'order'
		}
		paramValues = null;
		storedParamValues = null;
		paramsMap = new HashMap<>();
	}
	
	//@Override
	//public boolean requiresRecalculation() {
	//	System.out.println("Trajectory Parameters -- requiresRecalculation()");
	//	return true;
	//}
	
	/*
	boolean hasStartTime() {
		if (t0Input.get() != null ) return true;
		return t0Set;
	}
	*/
	
	/*
	void setStartTime(double newT0) {
		if (t0Input.get() != null ) {  // this shouldn't happen - bad use
			t0Input.get().setValue(newT0);
		}
		t0 = newT0;
		t0Set = true;
	}
	*/
	
	public double getStartTime() {
		if (t0Input.get()!=null)
			return t0Input.get().getValue();
		else {
			//System.out.println("*-> [t1,duration,troot] = "+t1+","+intervals.getTotalDuration()+","+(t1 - intervals.getTotalDuration() ));
			return t1 - intervals.getTotalDuration() - 0.0001;
			/*
			if (!t0Set)
				throw new IllegalArgumentException("(phydyn) Value of t0 has not been set");
			return t0;
			*/
		}
	}
	
	
	
	/* Not any more: Must call if (and only if) t1 is not supplied in XML input 
	 *       If called with t1Input present, throw exception  
	 *  We can change t1 if t1 provided eg if date trait provided    */
	void setEndTime(double t)  {		
		//if (t1Input.get()!=null) {  
		//	throw new Exception("Error: Trying to set t1 (already provided as input)");
		//}		
		t1 = t;
		t1Set = true;
		//System.out.println("Setting t1 = "+t1);
	}
	
	void unsetEndTime() {
		t1Set = false;
	}
	
	boolean hasEndTime() {
		return t1Set;
	}
	
	/* Sets up initial value parameters for model */
	/* TODO: make sure ALL state variables are initialised */
	public boolean initAndValidateModel(PopModelODE model)  {
		int i;
		Integer idx;
		// t1 should come from the tree later
		//if (t1==null) {
		//	throw new Exception("PopParameters: End Time missing");
		//}
		numParams = model.yLength;
		paramValues = new double[numParams];
		storedParamValues = new double[numParams];
		paramNames = new String[numParams];
		// Initialise index map
		for(i=0; i < numParams;i++) {
			paramNames[i]=null;
			paramsMap.put(model.yNames[i], i);
		}
        // All variables must be initialized
		for (ParamValue pValue: initialValuesInput.get()) {
			for(i=0; i < pValue.names.size(); i++) {
				idx = paramsMap.get(pValue.names.get(i));
				if (idx!=null) {
					if (paramNames[idx]!=null) {
						System.out.println("Error: Second initialisation of variable "+paramNames[idx]);
						return true;  // there's an error
					}
					paramNames[idx] = pValue.names.get(i);
					paramValues[idx] = pValue.values.getValue(i);
				} else {
					Log.warning("Warning: Unknown variable name '"+ pValue.names.get(i)+"' in population parameters object. Ignored.");
				}
			}
		}
		
		// Previously, non initialised population variables were set to 0. Now we throw an initialisation error.
		for(i=0; i < numParams; i++) {
			if (paramNames[i]==null) {
				Log.warning("Error: Population variable '"+model.yNames[i]+"' not initialised in TrajectoryParameters");
				throw new IllegalArgumentException("Population variable '"+model.yNames[i]+"' not initialised in TrajectoryParameters");
			}
		}
				
		/* print initial Values */
		//for(i=0; i < numParams; i++) {
		//	System.out.println(paramNames[i]+" = "+paramValues[i]);		
		//}
		modifiedValues = true;
		return false; // no error
	}
	
	// todo: See how this can be interleaved (or avoid) the MCMC machinery
	public boolean updateParam(String paramName, double paramValue) {
		Integer idx = paramsMap.get(paramName);
		if (idx==null) {
			return false;
		}
		paramValues[idx] = paramValue;
		return true;
	}
	
	public Double getParam(String paramName){
		Integer idx = paramsMap.get(paramName);
		if (idx==null) {
			//throw new IllegalArgumentException("Unknow Population Parameter: "+paramName);
			return null;
		}
		return paramValues[idx];
	}
	
	public void updateValues() {
		Integer idx;
		for (ParamValue pValue: initialValuesInput.get()) {
			// only bother if parameter value has changed
			if (true) {  // (pValue.isDirtyCalculation())  --- came back and fix this
				for(int i=0; i < pValue.names.size(); i++) {
					idx = paramsMap.get(pValue.names.get(i));
					if (idx!=null) {						
						//System.out.println("update "+pValue.names.get(i)+" = "+pValue.values.getValue(i));
						paramValues[idx] = pValue.values.getValue(i);
					} else {
						// parameter is not used by model
					}
				}
			}
		}
		
	}
	
	public void print() {
		System.out.print("traj: ");
		for(int i =0; i < numParams; i++) {
			System.out.print(paramValues[i] + " ");
		}
		System.out.println(" ");
	}

	@Override
	public String toString() {
		String space2 = "  ";
		String space4 = "    ";
		String s = "trajectory-parameters = {\n";
		if (t0Input.get()!=null) { // -- testing
			s += "  t0 = "+ Double.toString(this.getStartTime());
		} else {
			s += "  t0 = "+ "<troot>";
		}
		if (t1Set) {
			s += " t1 = "+ Double.toString(t1);
		}
		s += "\n";
		s += "  method="+methodInput.get()+"; ";
		s += "integrationSteps="+integrationSteps+";\n";
		if (!fixedStepSize) {
			s += "  order="+order+"; aTol="+aTol+" rTol="+rTol+"\n";
		}
		s += space2 + "initial-vales = {\n";
		for(int i=0; i < paramNames.length; i++) {
			s += space4+paramNames[i]+" = "+ paramValues[i] + ";\n";
		}
		s += space2+"}; \n";
		return s+"}";
	}
	
	
		
	/* Calculation Node Interface */
	
	/*
	@Override
	public boolean requiresRecalculation() {
		// called if any of the Inputs was updated (paramvalues or t0)		
		modifiedValues = true;
		return true;
	}
	
	@Override
	public void store() {
		//System.arraycopy(paramValues, 0, storedParamValues, 0, numParams);  -- ojo
		
		//storedT0 = t0;
		modifiedValues = false;
	}
	
	public void restore() {
		//double[] tmp = paramValues;  -- ojo
		//paramValues = storedParamValues;
		//storedParamValues = paramValues;
		
		//     t0 = storedT0;
		modifiedValues = false;
	}
	*/

}
