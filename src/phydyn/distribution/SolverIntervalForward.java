package phydyn.distribution;

public abstract class SolverIntervalForward {
	protected int numStates;

	
	public SolverIntervalForward(STreeLikelihood stlh) {
		numStates = stlh.numStates;
		
	}
	
	public boolean initValues(STreeLikelihood stlh) {
		return false;
	}
	
	
	public abstract void solve(double h0, double h1, int lastPoint, STreeLikelihood stlh);

}
