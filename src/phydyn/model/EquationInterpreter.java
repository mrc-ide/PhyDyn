package phydyn.model;

public class EquationInterpreter implements EquationEvaluatorAPI {
	PopModelODE model;	
	PopModelInterpreter interpreter;
	 
	public EquationInterpreter(PopModelODE model, SemanticChecker checker) {
		this.model = model;
		/* initialise interpreter */
		interpreter = new PopModelInterpreter(checker);
	}
	
	public void updateRate(String rateName, double v) {
		interpreter.updateEnv(rateName, v);
	}
	
	public void updateRate(int rateIndex, double v) {
		interpreter.updateEnv(model.modelParams.paramNames[rateIndex], v);
	}
	
	
	public void updateRates(double[] paramValues) {
		interpreter.updateEnv(model.modelParams.paramNames, paramValues);
	}
	
	public void updateRateVectors(double[][] rateVectorValues) {
		interpreter.updateEnv(model.modelParams.paramVectorNames, rateVectorValues);
	}
	
	public void updateYs(double[] yValues) {
		interpreter.updateEnv(model.yNames, yValues);
	}
	
	public void updateT0T1(double[] values) {
		interpreter.updateEnv(SemanticChecker.T0T1, values);
	}
	
	public void updateT(double value) {
		interpreter.updateEnv(SemanticChecker.T, value);
	}
	
	public void executeDefinitions() {
		for(Definition def: model.definitions) {
			interpreter.evaluate(def.tree);
		}
	}
	
	public void evaluateEquations(double[] results) {
		int i=0;
		for(MatrixEquation eq: model.equations) {
			results[i++] = interpreter.evaluate(eq.tree);
		}
	}

}

