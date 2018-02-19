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

	public int numParams, numVectorParams, numVectorValues;
	public String[] paramNames, paramVectorNames;
	Map<String, Integer> paramsMap;
	
	public double[] paramValues;
	public double[][] paramVectorValues;
	
	// todo: store related
	protected double[] storedParamValues;
	protected boolean modifiedValues;
	
	
	@Override
	public void initAndValidate()  {
		// Calculate number of parameters
		numParams = 0;
		numVectorParams=0; numVectorValues=0;
		paramValues= null;
		paramVectorValues=null;
		for (ParamValue param: paramsInput.get()) {
			if (param.isVector) {
				numVectorParams +=1;
				numVectorValues += param.values.getDimension();
			} else {
				numParams += param.names.size();
			}
		}
		paramNames = new String[numParams];
		paramValues = new double[numParams];
		// vectors
		paramVectorNames = new String[numVectorParams];
		paramVectorValues = new double[numVectorParams][];
		// todo: store related
		storedParamValues = new double[numParams];
		// Fill out arrays
		int idx = 0, n, idxV=0;
		paramsMap = new HashMap<>();
		for (ParamValue p: paramsInput.get()) {			
			if (p.isVector) {
				n = p.values.getDimension();
				paramVectorValues[idxV] = new double[n];
				paramVectorNames[idxV] = p.names.get(0);
				for(int i=0; i<n;i++) {
					paramVectorValues[idxV][i] = p.values.getValue(i);
				}				
				paramsMap.put(paramVectorNames[idxV], idxV);
				idxV++;
			} else {
				n = p.names.size();
				for(int i=0; i<n;i++) {
					paramValues[idx] = p.values.getValue(i);
					paramNames[idx] = p.names.get(i);
					paramsMap.put(paramNames[idx], idx); // stores idx used to access value array
					idx++;
				}	
			}
		}
		modifiedValues = true;
		
	}
	
	// Calculate 
	public void updateValues() {
		if (!modifiedValues) return;
		int idx = 0, n, idxV=0;
		for (ParamValue p: paramsInput.get()) {
			if (p.isVector) {
				n = p.values.getDimension();
				for(int i=0; i<n;i++) {
					paramVectorValues[idxV][i] = p.values.getValue(i);				
				}
				idxV++;
			} else {
				n = p.names.size();
				for(int i=0; i<n;i++) {
					paramValues[idx] = p.values.getValue(i);
					idx++;
				}	
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
