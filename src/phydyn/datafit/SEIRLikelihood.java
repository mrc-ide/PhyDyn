package phydyn.datafit;

import phydyn.model.TimeSeriesFGY;

public class SEIRLikelihood extends TrajectoryFit {
	
	@Override
	public void initAndValidate() {
		super.initAndValidate();
		double[] colTime = this.getColumnAsDouble(0);
		double[] colInfected = this.getColumnAsDouble(1);
		System.out.println("--- initialising seirlh --");
		for(int i = 0; i < numrows; i++) {
			System.out.println(colTime[i]+" - "+colInfected[i]);
		}
	}

	@Override
    public double calculateLogP() {
		// This will be be called twice (here and by STreeLikelihoodODE)
		// OK as long as DateTrait is forward (can we sample datetraits?).
		// solution: include tree paramater in popmodel and optimise recalculation.
		
		// this fails. PopModel's end-time is currently set by STreeLikelihood by using tree information.
		// TODO: Add a tree parameter to PopModel.
		// todonow: set tree field from STlh to get t1 internally (popmodel)
		boolean reject = popModel.update();
		
		
		if (reject) {
			logP = Double.NEGATIVE_INFINITY;
			return logP;
		}
		TimeSeriesFGY ts = popModel.getTimeSeries();
		
		// may need to check t in [t0,t1]
		double t0 = popModel.getStartTime();
		double t1 = 1; // popModel.getEndTime();
		System.out.println("t0 = "+t0+" t1 = "+t1);
		
		int nps = ts.getNumTimePoints();
		double tst0 = ts.getTime(0);
		double tst1 = ts.getTime(nps-1);
		System.out.println("t0 = "+tst0+" t1 = "+tst1);
		
		
		
        logP = 0;
        return logP;
    }
	
	
	
}
