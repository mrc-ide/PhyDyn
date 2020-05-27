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
		// todo: optimise popmodel udat to prevent unsnecessary recalculation
		
		TimeSeriesFGY ts = popModel.getTimeSeries();
		
		// may need to check t in [t0,t1]
		double t0 = popModel.getStartTime();
		double t1 = popModel.getEndTime(); // popModel.getEndTime();
		System.out.println("t0 = "+t0+" t1 = "+t1);
		
		int nps = ts.getNumTimePoints();  // reverse time
		double tst1 = ts.getTime(0);
		double tst0 = ts.getTime(nps-1);
		System.out.println("tst0 = "+tst0+" tst1 = "+tst1);
		
		
		
        logP = 0;
        return logP;
    }
	
	
	
}
