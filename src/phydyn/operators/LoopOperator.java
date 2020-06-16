/**
 * 
 */
package phydyn.operators;

import beast.core.Input;
import beast.core.Operator;
import beast.core.parameter.RealParameter;

/**
 * @author igor
 *
 */
public class LoopOperator extends Operator {
	
	public final Input<RealParameter> parameterInput = new Input<>("parameter", "parameter to be looped over",
	            Input.Validate.REQUIRED);
	 
	//public final Input<Double> spanInput = new Input<>("span","(length po) span to be looped over", Input.Validate.REQUIRED);
	 
	public final Input<Integer> numpointsInput = new Input<>("numpoints","number of points / number of loop interations+1",Input.Validate.REQUIRED);
	public final Input<Double> lboundInput = new Input<>("lbound","lower bound  (start) of loop",Input.Validate.REQUIRED);
	public final Input<Double> rboundInput = new Input<>("rbound","upper bound  (start) of loop",Input.Validate.REQUIRED);

	private int samplePoint=0; // 0 to numPoints-1
	private double span;
	private double startv, endv;
	private int numPoints;
	
	protected boolean outsideBounds(final double value, final RealParameter param) {
	        final Double l = param.getLower();
	        final Double h = param.getUpper();
	        return (value < l || value > h);
	        //return (l != null && value < l || h != null && value > h);
	}
	
	public void initAndValidate() {
		final RealParameter param = parameterInput.get(this);
		
		if (param.getDimension() > 1)
			throw new IllegalArgumentException("(LoopOperator) Parameter has more than one dimension");
		if (outsideBounds(lboundInput.get(),param)) {
			System.out.println(lboundInput.get());
			throw new IllegalArgumentException("(LoopOperator) lower bound outside parameter bounds");
		}
		if (outsideBounds(rboundInput.get(),param)) {
			throw new IllegalArgumentException("(LoopOperator) upper bound outside parameter bounds");
		}
		numPoints = numpointsInput.get();
		span = (rboundInput.get() - lboundInput.get())/(numPoints-1);
		if (span>0) {
			startv = lboundInput.get();
			endv = rboundInput.get();
		} else {
			endv = lboundInput.get();
			startv = rboundInput.get();
		}
		
		

	}

	/**
     * Always accepted / return Double.POSITIVE_INFINITY
     *
     * @return log of Hastings Ratio, or Double.NEGATIVE_INFINITY if proposal should not be accepted *
     */
	public double proposal() {
		final RealParameter param = parameterInput.get(this);
		// sample from samplePoint;
		double newValue;
		if (samplePoint==0) {
			newValue = startv; samplePoint++;
		} else if (samplePoint == (numPoints-1)) {
			newValue = endv; samplePoint = 0;
			// or shall we exit?
		} else {
			newValue = startv + span*samplePoint; samplePoint++;
		}
		
		param.setValue(0, newValue); 
		return Double.POSITIVE_INFINITY;
	}

}
