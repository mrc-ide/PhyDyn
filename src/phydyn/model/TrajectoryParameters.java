package phydyn.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import beast.core.BEASTObject;
import beast.core.CalculationNode;
import beast.core.Input;
import beast.core.Input.Validate;
import beast.core.parameter.RealParameter;

public class TrajectoryParameters extends CalculationNode {

	 public Input<String> methodInput = new Input<>(
			 "method","Integration method",Validate.REQUIRED);
	 
	 public Input<Double> rTolInput = new Input<>(
			 "rTol", "relative tolerance");
	 
	 public Input<Double> aTolInput = new Input<>(
			 "aTol", "absolute tolerance");
	 
	 public Input<Integer> orderInput = new Input<>(
			 "order", "order(k) of adaptive size integration method");
	 
	 public Input<RealParameter> t0Input = new Input<>(
			 "t0","Initial t (timeseries end)",Validate.REQUIRED);
	 
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
	 
	 protected double rTol, aTol;
	 protected int integrationSteps, timeseriesSteps, order;
	 protected IntegrationMethod method;
	 protected boolean fixedStepSize;
	 protected double t1;
	 	 
	 Map<String, Integer> paramsMap;
	 protected int numParams;
	 protected String[] paramNames;
	 protected boolean modifiedValues;
	 // Parameters
	 //protected double t0; // sampled - best take value from input
	 //protected double storedT0;
	 protected double[] paramValues;
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
		
		//t0 = t0Input.get().getValue(); // RealParameter - removed 
		if (t1Input.get()!=null) {
			t1 = t1Input.get();
		}
		/* Integration Method */
		String strMethod = methodInput.get();
		fixedStepSize = true;
		if (strMethod.equals("euler")) method = IntegrationMethod.EULER;
		else if (strMethod.equals("midpoint")) method = IntegrationMethod.MIDPOINT;
		else if (strMethod.equals("classicrk")) method = IntegrationMethod.CLASSICRK;
		else if (strMethod.equals("gill")) method = IntegrationMethod.GILL;
		else if (strMethod.equals("adams-bashforth")) { method = IntegrationMethod.ADAMSBASHFORTH; fixedStepSize=false; }
		else if (strMethod.equals("adams-moulton")) { method = IntegrationMethod.ADAMSMOULTON; fixedStepSize=false; }
		else if (strMethod.equals("higham-hall")) { method = IntegrationMethod.HIGHAMHALL; fixedStepSize=false; }
		else throw new IllegalArgumentException("Unknown integration method: "+strMethod+
				" - use: euler/midpoint/classicrk/gill adaptive: adams-bashforth, adams-moulton, higham-hall");
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
	
	double getStartTime() {
		return t0Input.get().getValue();
	}
	
	/* Must call if (and only if) t1 is not supplied in XML input 
	 * If called with t1Input present, throw exception (for the time being) */
	void setEndTime(double t) throws Exception {
		if (t1Input.get()!=null) {
			throw new Exception("Error: Trying to set t1 (already provided as input)");
		}		
		t1 = t;
		System.out.println("Setting t1 = "+t1);
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
					System.out.println("Warning: Unknown variable name "+ pValue.names.get(i)+" in population parameters object");
				}
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
	
	public double getParam(String paramName) throws Exception {
		Integer idx = paramsMap.get(paramName);
		if (idx==null) {
			throw new IllegalArgumentException("Unknow Population Parameter: "+paramName);
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
