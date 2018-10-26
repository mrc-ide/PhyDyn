package phydyn.model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import beast.core.CalculationNode;
import beast.core.Input;
import phydyn.analysis.PopModelAnalysis;
import phydyn.analysis.XMLFileWriter;

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
	
	public Double getParam(String paramName) {
		for(int i=0; i < paramNames.length; i++) {
			if (paramName.equals(paramNames[i]))
				return paramValues[i];
		}
		return null;
	}
	
	public void print() {
		System.out.print("rates: ");
		for(int i =0; i < numParams; i++) {
			System.out.print(paramValues[i] + " ");
		}
		System.out.println(" ");
	}

	@Override
	public String toString() {
		String s = "model-parameters = {\n";
		for(int i=0; i < paramNames.length; i++) {
			s += "  "+paramNames[i]+" = "+paramValues[i]+";\n";
		}
		for(int i=0; i < paramVectorNames.length; i++) {
			s += "  "+paramVectorNames[i]+" = { ";
			s += paramVectorValues[i][0];  // there should at least be one element
			for(int j=1; j < paramVectorValues[i].length; j++) {
				s += ", "+paramVectorValues[i][j];
			}
			s += "};\n";
		}
		return s+"}";
	}
	
	public String writeXML(XMLFileWriter writer, PopModelAnalysis analysis, String mparamID) throws IOException {
		String paramxml = "<param spec=\"ParamValue\" names=\"*x*\" values=\"*v*\"/>";
		String vectorxml = "<param spec=\"ParamValue\" vector=\"true\" names=\"*x*\" values=\"*v*\"/>";
		writer.tabAppend("<rates spec=\"ModelParameters\" id='**'> ".replace("**",mparamID)+"\n");
		writer.tab();
		// <param spec="ParamValue" names="beta0" values="0.0001"/>
		String s, paramName, paramID;
	    //<param spec="ParamValue" vector="true" names="KP" values="1 2 3"/>		
		for(int i=0; i < paramNames.length; i++) {
			paramName = paramNames[i];
			s = paramxml.replace("*x*",paramName);
			paramID = analysis.getParamID(paramName); // is it being sampled?
			if (paramID==null) {	
				s = s.replace("*v*",Double.toString(paramValues[i]));
			} else {
				s = s.replace("*v*","@"+paramID);
			}
			writer.tabAppend(s+"\n");
		}
		String vs;
		for(int i=0; i < paramVectorNames.length; i++) {
			s = vectorxml.replace("*x*",paramVectorNames[i]);
			vs = Double.toString(paramVectorValues[i][0]);  // there should at least be one element
			for(int j=1; j < paramVectorValues[i].length; j++) {
				vs += " "+  Double.toString(paramVectorValues[i][j]);
			}
			s = s.replace("*v*",vs);
			writer.tabAppend(s+"\n");
		}
	    //</rates>  
		writer.untab();
		writer.tabAppend("</rates>\n");
		return mparamID;
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
