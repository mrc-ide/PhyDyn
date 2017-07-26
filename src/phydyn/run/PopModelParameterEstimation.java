package phydyn.run;

import java.lang.Math;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleBounds;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.BOBYQAOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.PowellOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;

import beast.core.Input;
import beast.core.Runnable;
import phydyn.distribution.STreeLikelihood;
import phydyn.model.PopModelODE;

public class PopModelParameterEstimation extends Runnable implements MultivariateFunction {

	public Input<STreeLikelihood> densityInput = new Input<>(
			"density","Population Model");
	
	protected STreeLikelihood density;
	protected PopModelODE model;
	
	@Override
	public void initAndValidate()  {
		density = densityInput.get();
		model = density.getModel();
	}

	@Override
	public void run() throws Exception {
		// Let's find optimum for beta0, beta1, beta2
		NelderMeadSimplex optMethod = new NelderMeadSimplex(3);
		
		/*
		SimplexOptimizer optimizer = new SimplexOptimizer(1e-5, 1e-10);
		final PointValuePair optimum =
				optimizer.optimize(
					new MaxEval(150), 
	                new ObjectiveFunction(this), 
	                GoalType.MAXIMIZE, 
	                new InitialGuess(new double[]{ Math.log(.6), Math.log(.2), Math.log(0.05) }), 
	                optMethod);
		 */
	     // new NelderMeadSimplex(new double[]{ 0.2, 0.2 }));
		
		/*
		SimplexOptimizer optimizer = new SimplexOptimizer(1e-5, 1e-10);
		final PointValuePair optimum =
				optimizer.optimize(
					new MaxEval(200), 
	                new ObjectiveFunction(this), 
	                GoalType.MAXIMIZE, 
	                new InitialGuess(new double[]{ .6, .2, 0.05 }), 
	                optMethod);
		*/
		
		
		//PowellOptimizer optimizer = new PowellOptimizer(1e-8, 1e-5, 1e-4, 1e-4);
		BOBYQAOptimizer optimizer = new BOBYQAOptimizer(2*3+1+2); // 2*point.length + 1+additional
		final PointValuePair optimum =
				optimizer.optimize(
					new MaxEval(150), 
	                new ObjectiveFunction(this), 
	                GoalType.MAXIMIZE, 
	                new InitialGuess(new double[]{ 1.0, 1.0, 1.0 }),
	                new SimpleBounds(new double[] { 0.0 , 0.0 , 0.0 },
                           new double[] { 3.5 , 3.5, 3.5 }));
	
		
		double[] point = optimum.getPoint();
		System.out.print("point= ");
		for(int i=0; i< point.length; i++) System.out.print("  "+ point[i]);
		System.out.println(" ");
		System.out.println("value = "+ optimum.getValue());
		
	}

	@Override
	public double value(double[] args) {
		double beta0, beta1, beta2, logP;
		//beta0 = Math.exp(args[0]);
		//beta1 = Math.exp(args[1]);
		//beta2 = Math.exp(args[2]);
		beta0 = args[0];
		beta1 = args[1];
		beta2 = args[2];
		try {
			model.updateRate("beta0", beta0);
			model.updateRate("beta1", beta1);
			model.updateRate("beta2", beta2);
		} catch (Exception e) {
			System.out.println("Incorrect parameter name");
			e.printStackTrace();
		}
		try {
			logP = density.calculateLogP();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logP =  Double.NaN;
		}
		System.out.println("new args:"+args[0]+","+args[1]+","+args[2]+" logP: "+logP);
		return logP;
	}
	
	

}
