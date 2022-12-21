package phydyn.covariate.distribution;

import beast.base.core.Input;
import beast.base.core.Input.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Normal extends ParamDistribution {
	
	public final Input<String> meanInput = new Input<>("mean", "Normal distribution mean", Validate.REQUIRED);
	public final Input<String> sigmaInput = new Input<>("sigma", "Normal distribution standard deviation", Validate.REQUIRED);
	
	private boolean isMeanConstant, isSigmaConstant;
	private double mean, sigma;
	private String meanParameter, sigmaParameter;
	
	private List<String> parameters;
	
	@Override
	public void initAndValidate() {
		isMeanConstant = isSigmaConstant = true;
		mean = 0; sigma = 1;
		parameters = new ArrayList<String>();
		String p = meanInput.get().trim();
		try {
			mean = Double.parseDouble(p);			
		} catch(Exception e) {
			isMeanConstant = false;
			parameters.add(p);
			meanParameter = p;
		}
		p = sigmaInput.get().trim();
		try {
			sigma = Double.parseDouble(p);
		} catch(Exception e) {
			isSigmaConstant = false;
			parameters.add(p);
			sigmaParameter = p;
		}		

	}

	@Override
	public int numParameters() {
		return parameters.size();
	}
	
	@Override
	public List<String> getParameters() {
		return new ArrayList<String>(parameters);
	}

	@Override
	public void updateParameters(Map<String, Double> env) {
		if (!isMeanConstant) {
			mean = env.get(meanParameter);
		}
		if (!isSigmaConstant)
			sigma = env.get(sigmaParameter);
	}
	
	public double logDensity(double x) {
		double ld = -Math.log(sigma*Math.sqrt(2*Math.PI))- 0.5*(x - mean)*(x-mean)/(sigma*sigma);
		return ld;
	}

}
