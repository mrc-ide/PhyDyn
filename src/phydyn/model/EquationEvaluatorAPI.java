package phydyn.model;

import beast.base.core.Description;

@Description("Interface between PopModelODE (trajectory generator) and the equation/definitions evaluators")
public interface EquationEvaluatorAPI {
	

	
	public void updateRate(String rateName, double v);
	
	public void updateRate(int rateIndex, double v);
	
	public void updateRates(double[] rateValues);
	
	public void updateRateVectors(double[][] rateVectorValues);
		
	public void updateYs(double[] yValues);
	
	public void updateT0T1(double[] values);
	
	public void updateT(double value);
	
	public void executeDefinitions();
	
	public void evaluateEquations(double[] results);
	
}
