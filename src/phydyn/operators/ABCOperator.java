package phydyn.operators;




import java.text.DecimalFormat;

import beast.core.Description;
import beast.core.Input;
import beast.core.Operator;
import beast.core.parameter.RealParameter;
import beast.util.Randomizer;

/**
 * 
 * changed by Nicola Felix Mueller from the scale operator
 *
 */
@Description("Adds a fixed value or multiplies by a fixed value. Is always accepted")
public class ABCOperator extends Operator {

	   public final Input<RealParameter> parameterInput = new Input<>("parameter", "if specified, this parameter is scaled",
	            Input.Validate.REQUIRED);
	    public final Input<Double> scaleFactorInput = new Input<>("scaleFactor", "scaling factor: larger means more bold proposals", 1.0);
	    public final Input<Boolean> scaleAllInput =
	            new Input<>("scaleAll", "if true, all elements of a parameter (not beast.tree) are scaled, otherwise one is randomly selected",
	                    false);
	    final public Input<Integer> degreesOfFreedomInput = new Input<>("degreesOfFreedom", "Degrees of freedom used when " +
	            "scaleAllIndependently=false and scaleAll=true to override default in calculation of Hasting ratio. " +
	            "Ignored when less than 1, default 0.", 0);
	    final public Input<Boolean> multiplyInput = new Input<>("multiply", "flag to indicate that the scale factor is automatically changed in order to achieve a good acceptance rate (default true)", true);
	    final public Input<Double> scaleUpperLimit = new Input<>("upper", "Upper Limit of scale factor", 1.0 - 1e-8);
	    final public Input<Double> scaleLowerLimit = new Input<>("lower", "Lower limit of scale factor", 1e-8);
	    final public Input<Integer> onlyScaleInput = new Input<Integer>(
	    		"onlyScale",
	    		"if only the n-th entry of a real parameter is scaled");
	    
	    /**
	     * shadows input *
	     */
	    private double m_fScaleFactor;

	    private double upper, lower;
	    /**
	     * flag to indicate this scales trees as opposed to scaling a parameter *
	     */
	    boolean m_bIsTreeScaler = true;
	    boolean doMultiplication = false;

	    @Override
	    public void initAndValidate() {
	        m_fScaleFactor = scaleFactorInput.get();
	        upper = scaleUpperLimit.get();
	        lower = scaleLowerLimit.get();
	        // Do multiplication or add value
	        if(multiplyInput.get() != null) doMultiplication = multiplyInput.get().booleanValue();
	    }


	    protected boolean outsideBounds(final double value, final RealParameter param) {
	        final Double l = param.getLower();
	        final Double h = param.getUpper();

	        return (value < l || value > h);
	        //return (l != null && value < l || h != null && value > h);
	    }


	    /**
	     * override this for proposals,
	     *
	     * @return log of Hastings Ratio, or Double.NEGATIVE_INFINITY if proposal should not be accepted *
	     */
	    @Override
	    public double proposal() {

	        try {

//	            double hastingsRatio;

	            // not a tree scaler, so scale a parameter
	            final boolean scaleAll = scaleAllInput.get();
//	            final int degreesOfFreedom = degreesOfFreedomInput.get();
//	            final boolean scaleAllIndependently = scaleAllIndependentlyInput.get();

	            final RealParameter param = parameterInput.get(this);

	            assert param.getLower() != null && param.getUpper() != null;

	            final int dim = param.getDimension();

	            if (scaleAll) {
	                // update all dimensions
	                // hasting ratio is dim-2 times of 1dim case. would be nice to have a reference here
	                // for the proof. It is supposed to be somewhere in an Alexei/Nicholes article.
//	                final int df = (degreesOfFreedom > 0) ? degreesOfFreedom - 2 : dim - 2;
//	                hastingsRatio = df * Math.log(scale);

	                // all Values assumed independent!
	                for (int i = 0; i < dim; i++) {
	                	final double newValue;
	                	if (doMultiplication)
	                		newValue = param.getArrayValue(i) * m_fScaleFactor;
	                	else
	                		newValue = param.getArrayValue(i) + m_fScaleFactor;

	                    if (outsideBounds(newValue, param)) {
	                        return Double.NEGATIVE_INFINITY;
	                    }
	                    
//	                    System.out.println(param.getArrayValue(i));
//	                    System.out.println(newValue);
//	                    System.exit(0);
	                    param.setValue(i, newValue);
	                }
	            } 
	        	else {

	                // which position to scale
	                final int index;
	        		if (onlyScaleInput.get()==null){
		                if (null != null) {
		                	index = Randomizer.nextInt(dim);
		                } else {
		                    // any is good
		                    index = Randomizer.nextInt(dim);
		                }
	        		}else{
	        			index = onlyScaleInput.get();
	        		}

	                final double oldValue = param.getValue(index);

	                if (oldValue == 0) {
	                    // Error: parameter has value 0 and cannot be scaled
	                    return Double.NEGATIVE_INFINITY;
	                }

	                double newValue;
	                
	                
	            	if (doMultiplication)
	            		newValue = param.getValue(index) * m_fScaleFactor;
	            	else
	            		newValue = param.getValue(index) * m_fScaleFactor;
	            	
	                
	                param.setValue(index, newValue);
	                // provides a hook for subclasses
	                //cleanupOperation(newValue, oldValue);
	            }

	            return Double.POSITIVE_INFINITY;

	        } catch (Exception e) {
	            // whatever went wrong, we want to abort this operation...
	            return Double.NEGATIVE_INFINITY;
	        }
	    }


	    /**
	     * automatic parameter tuning *
	     */
	    @Override
	    public void optimize(final double logAlpha) {
//	        if (optimiseInput.get()) {
//	            double delta = calcDelta(logAlpha);
//	            delta += Math.log(1.0 / m_fScaleFactor - 1.0);
//	            setCoercableParameterValue(1.0 / (Math.exp(delta) + 1.0));
//	        }
	    }

	    @Override
	    public double getCoercableParameterValue() {
	        return m_fScaleFactor;
	    }

	    @Override	
	    public void setCoercableParameterValue(final double value) {
	        m_fScaleFactor = Math.max(Math.min(value, upper), lower);
	    }

	    @Override
	    public String getPerformanceSuggestion() {
	        final double prob = m_nNrAccepted / (m_nNrAccepted + m_nNrRejected + 0.0);
	        final double targetProb = getTargetAcceptanceProbability();

	        double ratio = prob / targetProb;
	        if (ratio > 2.0) ratio = 2.0;
	        if (ratio < 0.5) ratio = 0.5;

	        // new scale factor
	        final double sf = Math.pow(m_fScaleFactor, ratio);

	        final DecimalFormat formatter = new DecimalFormat("#.###");
	        if (prob < 0.10) {
	            return "Try setting scaleFactor to about " + formatter.format(sf);
	        } else if (prob > 0.40) {
	            return "Try setting scaleFactor to about " + formatter.format(sf);
	        } else return "";
	    }
	
	
	
	

}
