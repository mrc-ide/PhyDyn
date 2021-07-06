package phydyn.covariate.distribution;

import java.util.List;
import java.util.Map;

import beast.core.BEASTObject;


public abstract class ParamDistribution extends BEASTObject {
	

	@Override
	public void initAndValidate() {
		// TODO Auto-generated method stub

	}
	
	public abstract int numParameters();
	
	public abstract List<String> getParameters();
	
	public abstract void updateParameters(Map<String,Double> env);
	
	public double logDensity(double x) {
		return 0;
	}

}