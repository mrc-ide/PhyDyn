package phydyn.model;

import java.util.ArrayList;
import java.util.List;

import beast.core.CalculationNode;
import beast.core.Input;

import java.util.HashMap;
import java.util.Map;

public class ModelParameters extends CalculationNode {
	
	public Input<List<ParamValue>> paramsInput = new Input<>(
            "param",
            "Parameter values used by the model equations",
            new ArrayList<>());

	public int numParams;
	public String[] paramNames;
	Map<String, Integer> paramsMap;
	
	public double[] paramValues;
	protected double[] storedParamValues;
	protected boolean modifiedValues;
	
	
	@Override
	public void initAndValidate()  {
		// Calculate number of parameters
		numParams = 0;
		for (ParamValue param: paramsInput.get()) {
			numParams += param.names.size();
		}
		paramNames = new String[numParams];
		paramValues = new double[numParams];
		storedParamValues = new double[numParams];
		// Fill out arrays
		int idx = 0,n;
		paramsMap = new HashMap<>();
		for (ParamValue p: paramsInput.get()) {
			n = p.names.size();
			for(int i=0; i<n;i++) {
				paramValues[idx] = p.values.getValue(i);
				paramNames[idx] = p.names.get(i);
				paramsMap.put(p.names.get(i), idx); // stores idx used to access value array
				idx++;
			}			
		}
		modifiedValues = true;
		
	}
	
	// Calculate 
	public void updateValues() {
		if (!modifiedValues) return;
		int idx = 0, n;
		for (ParamValue p: paramsInput.get()) {
			n = p.names.size();
			for(int i=0; i<n;i++) {
				paramValues[idx] = p.values.getValue(i);
				idx++;
			}			
		}
		
	}
	
	public void print() {
		System.out.print("rates: ");
		for(int i =0; i < numParams; i++) {
			System.out.print(paramValues[i] + " ");
		}
		System.out.println(" ");
	}
	
	
	/*
	@Override
	public boolean requiresRecalculation() {
		// called if any of the Inputs was updated - no need to check more
		modifiedValues = true;
		return true;
	}
	
	@Override
	public void store() {
		//System.arraycopy(paramValues, 0, storedParamValues, 0, numParams);
		modifiedValues = false;
	}
	
	public void restore() {
		//double[] tmp = paramValues;
		//paramValues = storedParamValues;
		//storedParamValues = tmp;
		modifiedValues = false;
	}
	*/
	

}
