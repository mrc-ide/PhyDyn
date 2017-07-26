package phydyn.model;

import java.util.Map;

public class EquationMachine implements EquationEvaluatorAPI {
	PopModelODE model;
	PopModelCompiler compiler;
	int[] constantAddresses;
	String[] constantNames;
	int[] t0t1Addresses;
	int tAddress;
	int[] yAddresses;
	int[] rateAddresses;
	int[] defAddresses;
	PMStackMachine machine;
	 
	public EquationMachine(PopModelODE model, SemanticChecker checker) {
		this.model = model;
		compiler = new PopModelCompiler();
		constantAddresses = new int[checker.usedConstants.size()]; // load only used constants
		constantNames = new String[checker.usedConstants.size()];
		int i=0;
		double[] constantValues = new double[checker.usedConstants.size()];
		for (Map.Entry<String, Double> entry : checker.usedConstants.entrySet())
        {
			constantNames[i] = entry.getKey();
			constantValues[i] = entry.getValue();
        }
		// Time variables
		if (checker.useT0T1) {
			t0t1Addresses = new int[2];			
		}
		yAddresses = new int[model.yNames.length];
		rateAddresses = new int[model.modelParams.numParams];  // igor mod
		defAddresses = new int[model.defNames.size()];	
		/* Assign addresses to variable names */
		compiler.updateEnv(constantNames, constantAddresses);
		if (checker.useT0T1) {
			compiler.updateEnv(SemanticChecker.T0T1, t0t1Addresses);
		}
		if (checker.useT) {
			tAddress = compiler.updateEnv(SemanticChecker.T);
		}
		compiler.updateEnv(model.yNames, yAddresses);
		compiler.updateEnv(model.modelParams.paramNames, rateAddresses);  // igor mod
		compiler.updateEnv(model.defNames, defAddresses);
		/* now, compile */
		int stackSize, maxStackSize=0;
		for(MatrixEquation eq: model.equations) {
			stackSize = eq.compile(compiler);
			if (stackSize > maxStackSize) maxStackSize=stackSize;
		}
		for (Definition def: model.definitions) {
			stackSize = def.compile(compiler);
			if (stackSize > maxStackSize) maxStackSize=stackSize;
		}
		/* create the stack machine. we need to know number of variables (addresses) */
		machine = new PMStackMachine(compiler.getEnvSize(), maxStackSize);
		machine.updateEnv(constantAddresses, constantValues);
		// Load initial values
		compiler =  null; // finished with compiler
	}
	
	public void updateRate(String rateName, double v) {
		int address = compiler.getAddress(rateName);
		machine.updateEnv(address, v);
	}
	
	/* improve this - use offset */
	public void updateRate(int rateIndex, double v) {
		int address = compiler.getAddress(model.modelParams.paramNames[rateIndex]);
		machine.updateEnv(address, v);
	}
	
	public void updateRates(double[] rateValues) {
		machine.updateEnv(rateAddresses, rateValues);
	}
	
	public void updateYs(double[] yValues) {
		machine.updateEnv(yAddresses, yValues);
	}
	
	public void updateT0T1(double[] values) {
		machine.updateEnv(t0t1Addresses, values);
	}
	
	public void updateT(double value) {
		machine.updateEnv(tAddress, value);
	}
	
	public void executeDefinitions() {
		for(Definition def: model.definitions) {
			machine.execute(def.code);
		}
	}
	
	public void evaluateEquations(double[] results) {
		int i=0;
		for(MatrixEquation eq: model.equations) {
			//System.out.println("Executing: "+eq.equationStringInput.get());
			results[i++] = machine.execute(eq.code);
		}
	}
	
	

}
