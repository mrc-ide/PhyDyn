package phydyn.distribution;

import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.AdamsBashforthIntegrator;
import org.apache.commons.math3.ode.nonstiff.AdamsMoultonIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math3.ode.nonstiff.GillIntegrator;
import org.apache.commons.math3.ode.nonstiff.HighamHall54Integrator;
import org.apache.commons.math3.ode.nonstiff.MidpointIntegrator;



public abstract class SolverInterval {
	protected int numStates;
	protected boolean forgiveY=false;
	protected FirstOrderIntegrator foi;
	protected double logLh;
	
	public SolverInterval(STreeLikelihoodODE stlh) {
		numStates = stlh.numStates;
		forgiveY = stlh.forgiveYInput.get();		
		// Instantiate solver once
		double minStep = 0.0;
		double maxStep = 0.5;
		switch(stlh.method) {
		case MIDPOINT: foi = new MidpointIntegrator(stlh.stepSize); break;
		case CLASSICRK: foi = new ClassicalRungeKuttaIntegrator(stlh.stepSize); break;
		case GILL: foi = new GillIntegrator(stlh.stepSize); break;
		case ADAMSBASHFORTH: foi = new AdamsBashforthIntegrator(stlh.order,minStep,maxStep,stlh.aTol,stlh.rTol); break;
		case ADAMSMOULTON: foi = new AdamsMoultonIntegrator(stlh.order,minStep,maxStep,stlh.aTol,stlh.rTol); break;
		case HIGHAMHALL: foi = new HighamHall54Integrator(minStep,maxStep,stlh.aTol,stlh.rTol); break;
		}
	}
	
	public double getLogLh() { return logLh; }
	
	public abstract void solve(double h0, double h1, int lastPoint, STreeLikelihoodODE stlh);

}
