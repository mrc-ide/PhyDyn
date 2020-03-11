package phydyn.distribution;

import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.AdamsBashforthIntegrator;
import org.apache.commons.math3.ode.nonstiff.AdamsMoultonIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math3.ode.nonstiff.EulerIntegrator;
import org.apache.commons.math3.ode.nonstiff.GillIntegrator;
import org.apache.commons.math3.ode.nonstiff.HighamHall54Integrator;
import org.apache.commons.math3.ode.nonstiff.MidpointIntegrator;

public abstract class SolverIntervalODE {
	protected int numStates;
	protected boolean forgiveY=false;
	protected FirstOrderIntegrator foi;
	protected double logLh;
	protected boolean debug;
	
	public SolverIntervalODE(STreeLikelihoodODE stlh) {
		numStates = stlh.numStates;
		forgiveY = stlh.forgiveYInput.get();		
		// Instantiate solver once
		initIntegrator(stlh);
		debug=false;
	}
	
	protected void initIntegrator(STreeLikelihoodODE stlh) {
		double minStep = 0.0;
		double maxStep = 0.5;
		switch(stlh.method) {
		case EULER: foi = new EulerIntegrator(stlh.stepSize);
		case MIDPOINT: foi = new MidpointIntegrator(stlh.stepSize); break;
		case CLASSICRK: foi = new ClassicalRungeKuttaIntegrator(stlh.stepSize); break;
		case GILL: foi = new GillIntegrator(stlh.stepSize); break;
		case ADAMSBASHFORTH: foi = new AdamsBashforthIntegrator(stlh.order,minStep,maxStep,stlh.aTol,stlh.rTol); break;
		case ADAMSMOULTON: foi = new AdamsMoultonIntegrator(stlh.order,minStep,maxStep,stlh.aTol,stlh.rTol); break;
		case HIGHAMHALL: foi = new HighamHall54Integrator(minStep,maxStep,stlh.aTol,stlh.rTol); break;
		}
	}
	
	public boolean initValues(STreeLikelihoodODE stlh) {
		return false;
	}
	
	public void setDebug(boolean b) { debug = b; }
	
	public double getLogLh() { return logLh; }
	
	public abstract void solve(double h0, double h1, int lastPoint, STreeLikelihoodODE stlh);

}
