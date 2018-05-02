package phydyn.model;

import java.util.Collections;

import phydyn.util.DMatrix;
import phydyn.util.DVector;

public class TimeSeriesFGYConstant implements TimeSeriesFGY {
	private int numDemes, numNonDemes, numPoints;
	private double[] timePoints=null;
	FGY fgy=null;

	public TimeSeriesFGYConstant(PopModel model, double t0, double t1, int numPoints) {
		this.numDemes = model.numDemes;
		this.numNonDemes = model.numNonDemes;
		this.numPoints = numPoints;
		// initialize time point array
		if (numPoints<2)
			numPoints=2;
		timePoints = new double[numPoints];
		timePoints[0] = t0;
		timePoints[numPoints-1] = t1;
		double step = (t1-t0)/(numPoints-1);
		double t = t0;
		for(int idx=1; idx<(numPoints-1); idx++) {
			t += step;
			timePoints[idx] = t; 
		}
		
	}
	
	@Override
	public int lengthYall() {
		return fgy.Yall.length;
	}

	@Override
	public void addFGY(double t, DMatrix F, DMatrix G, double[] y, DVector D) {
		if (fgy!=null)
			return;
		int i,j;
    	DVector Ynew,YnewAll;
    	double[] yDemes = new double[numDemes]; 
    	for(i=0; i < numDemes; i++) { yDemes[i] = y[i]; }
    	Ynew = new DVector(numDemes,yDemes); /*  keeps buffer yDemes */    	
    	// copy all y's 
    	double[] yall = new double[numDemes+numNonDemes];
    	for(i=0; i < numDemes+numNonDemes; i++) { yall[i] = y[i]; }
    	YnewAll = new DVector(numDemes+numNonDemes,yall);   	
		// add FG -- using copy constructor
    	DMatrix Fnew = new DMatrix(F);
    	DMatrix Gnew = new DMatrix(G);   	
    	DVector Dnew;
    	if (D==null) Dnew=null;
    	else {
    		Dnew = new DVector(D);
    	}    		
		fgy = new FGY(Fnew,Gnew,YnewAll, Ynew, Dnew);

	}

	@Override
	public void reverse() {
		double temp;
		int i,j;
		for(i=0,j=timePoints.length-1; i<j ; i++, j--) {
			temp = timePoints[i];
			timePoints[i] = timePoints[j];
			timePoints[j] = temp;
		}
		return;
	}

	@Override
	public int getNumTimePoints() {
		return numPoints;
	}

	/* input: index in timepoint array */
	@Override
	public double getTime(int tp) {
		return timePoints[tp];
	}

	@Override
	public FGY getFGY(int tp) {
		return fgy;
	}
	
	@Override
	public FGY getFGYfromTime(double t, int tp) {
		return fgy;
	}

	@Override
	public DVector getY(int tp) {
		return fgy.Y;
	}

	@Override
	public DVector getYall(int tp) {
		return fgy.Yall;
	}
	
	public void setMinY(int minY) {
		fgy.Y.maxi(minY);	
	}

	@Override
	public TimeSeriesFGY toFixedStep(int numPoints, PopModelODE model) {
		if (numPoints<2)
			numPoints=2;
		if (numPoints==this.numPoints)
			return this;
		double t0 = timePoints[0];
		double t1 = timePoints[numPoints-1];
		TimeSeriesFGY ts = new TimeSeriesFGYConstant(model,t0,t1,numPoints);
		ts.addFGY(0.0, fgy.F, fgy.G, fgy.Yall.data,fgy.D);
		return ts;
	}

	@Override
	public int getTimePoint(double tTarget, int pStart) {
		// Assumming that the timepoint is going to be used to access the FGY
		// it makes no difference what we return
		return 0;
	}


	

}
