package phydyn.distribution;

import phydyn.model.TimeSeriesFGY;

public class STreeLikelihoodConstant extends STreeLikelihood {

	public STreeLikelihoodConstant() {
		// TODO Auto-generated constructor stub
	}
	
	 /* updates t,h,tsPoint and lineage probabilities */
	 protected double processInterval(int interval, double intervalDuration, TimeSeriesFGY ts) {
		 double lh=0.0;
		 double hEvent = h + intervalDuration; 		// event height
		 double tEvent = ts.getTime(0) - hEvent;      // event time
		 // Update probs and compute likelihood contribution
		 
		
		 // Given that FGY is constant, we should be able to come up with an exponential version
		 
		 
		 /* update tsPoint, h and t */
		 if (ts.getTime(tsPoint) > tEvent) {
			 while (ts.getTime(tsPoint+1) > tEvent) {
				 tsPoint++;
			 }
		 }		 
		 h = hEvent;
		 t = ts.getTime(0) - h;
		 
		 return lh;
	 }

}
