package phydyn.model;

import java.util.List;

import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;
import org.jblas.DoubleMatrix;

//import org.jblas.DoubleMatrix;

import java.util.ArrayList;
import java.util.Collections;

public class TimeSeriesFGYStd implements TimeSeriesFGY {
	// FirstOrderDifferentialEquations model;
	private int numDemes, numNonDemes, numPoints;
	boolean isFixedStep;
	List<Double> timePointsList;

	List<FGY> FGYlist;
	
	private boolean isAscending=true;
	/* array representations */
	public double[] timePoints;	
	public FGY[] FGYs;
	

	// TimeSeriesFGY(int numDemes, FirstOrderDifferentialEquations model)
	TimeSeriesFGYStd(PopModelODE model) {
		this.numDemes = model.numDemes;
		this.numNonDemes = model.numNonDemes;
		this.isFixedStep = model.fixedStepSize;
		numPoints=0;
		timePointsList = new ArrayList<Double>();		
		FGYlist = new ArrayList<FGY>();
	}
    
	public int lengthYall() {
		return numDemes+numNonDemes;
	}
	
    /* Creates a new entry for time step t with deme values stored in y */
    /* the size of y is >= nstates since it also contains the nonDeme elements */
	public void addFGY(double t, DoubleMatrix F, DoubleMatrix G, double[] y, DoubleMatrix D) {
    	int i;
    	DoubleMatrix Ynew,YnewAll;
    	timePointsList.add(t);
    	double[] yDemes = new double[numDemes]; 
    	for(i=0; i < numDemes; i++) { yDemes[i] = y[i]; }
    	Ynew = new DoubleMatrix(numDemes,1,yDemes); /*  keeps buffer yDemes */    	
    	// copy all y's 
    	double[] yall = new double[numDemes+numNonDemes];
    	for(i=0; i < numDemes+numNonDemes; i++) { yall[i] = y[i]; }
    	YnewAll = new DoubleMatrix(numDemes+numNonDemes,1,yall); /*  keeps buffer yDemes */   	
    	numPoints++;
		// add FG
    	DoubleMatrix Fnew = new DoubleMatrix();
    	DoubleMatrix Gnew = new DoubleMatrix();	
    	Fnew.copy(F);
    	Gnew.copy(G);
    	DoubleMatrix Dnew;
    	if (D==null) Dnew=null;
    	else {
    		Dnew = new DoubleMatrix();
    		Dnew.copy(D);
    	}    		
    	FGYlist.add(new FGY(Fnew,Gnew,Ynew,YnewAll,Dnew));
	}
    
    public void reverse() {
    	timePoints = null; 
    	
    	// Ys = null; allYs = null; Fs = Gs = null;
    	
    	Collections.reverse(timePointsList);
    	Collections.reverse(FGYlist);
    	isAscending = false;
    }
        
    public double[] getTimePoints() {
    	if (timePoints == null) {
    		timePoints = new double[timePointsList.size()];
    		for(int i=0; i < timePointsList.size(); i++) {
    			timePoints[i] = (double)(timePointsList.get(i));   	
    		}   
    	}
    	return timePoints;
    }
    
	@Override
	public FGY getFGY(int tp) {
		if (FGYs == null) {
			FGYs = new FGY[FGYlist.size()];
			FGYlist.toArray(FGYs);
		}
		return FGYs[tp];
	}
	
	@Override
	public FGY getFGYfromTime(double t, int tp) {
		int newTp = getTimePoint(t,tp);
		return FGYs[newTp];
	}
	
	@Override
	public DoubleMatrix getY(int tp) {
		return this.getFGY(tp).Y;
	}

	@Override
	public DoubleMatrix getYall(int tp) {
		return this.getFGY(tp).Yall;
	}
    
	public void setMinY(int minY) {
		for(int i=0; i < numPoints; i++) {
			FGYlist.get(i).Y.maxi(minY);
		}		
	}

	

    @Override
    public TimeSeriesFGY toFixedStep(int numPoints, PopModelODE model) {
    	int n,idx;
    	double t0, t1, t, f;
    	DoubleMatrix y;
    	TimeSeriesFGYStd ts = new TimeSeriesFGYStd(model);
    	ts.isFixedStep = true;
    	double[] timePoints = this.getTimePoints();  // access timePoints Array
    	
    	//DoubleMatrix[] allYs = this.getAllYs();
    	
    	n = timePoints.length;
    	t0 = timePoints[0];
    	t1 = timePoints[n-1];
    	// tstep = (t1-t0)/(numPoints-1);
    	// Process first point
    	y = this.getYall(0);  // allYs[0];
    	model.updateMatrices(t0, y.data);
    	
    	ts.addFGY(t0,model.births,model.migrations, y.data, model.deaths);
    	
    	t = t0;
    	if (numPoints==1) return ts;
    	idx = 0;
    	y = new DoubleMatrix(y.rows,y.columns);
    	for(int i=1; i < numPoints-1; i++) {
    		// more precise
    		t = t0 + (t1-t0)*i/(numPoints-1);
    		// look for next index
    		// we could use getTimeInterval (compare first)
    		while (t > timePoints[idx+1]) {
    			idx++;
    		}
    		// timePoints[idx] < t <= timePoints[idx+1]
    		// f = (t-t1)/(t2-t1)
    		f = (t-timePoints[idx])/(timePoints[idx+1]-timePoints[idx]);
    		// y = y1 + (y2-y1)*f
    		this.getYall(idx+1).subi(this.getYall(idx),y);
    		y.muli(f);  // in-place
    		y.addi(this.getYall(idx)); // in-place
        	model.updateMatrices(t, y.data);
        	ts.addFGY(t,model.births,model.migrations, y.data,model.deaths);
    	}
    	// Process last point
    	y = this.getYall(n-1);
    	model.updateMatrices(t1, y.data);
    	ts.addFGY(t1,model.births,model.migrations, y.data, model.deaths);
    	return ts;
    }
    
    /* Get closest Time Point. Initial guess = pStart */
    public int getTimePoint(double tTarget, int pStart) {
    	int p,interval = pStart;
    	getTimePoints(); // prefer to work with arrays
    	interval = getTimeInterval(tTarget,pStart);
    	// descending tTarget in [timePoints[i+1],timePoints[i]]
    	if (interval<0) 
    		p=0;
    	else if (interval > numPoints-2) {
    		p = numPoints-1;  // last point
    	} else {  // choose between extremes
    		p = interval;
    		if (isAscending) {
    			if ((tTarget-timePoints[interval]) > (timePoints[interval+1] - tTarget))
    				p = interval+1;
    		} else { /* reverse order */
    			if ((tTarget-timePoints[interval+1]) < (timePoints[interval] - tTarget))
    				p = interval+1;
    		}
    	}
    	return p;
    }
    
    // Intervals: There are (numPoints-1) time intervals, labelled from 0 to (numPoints-2)
    // Time points that fall outside the timeseries are assigned interval number -1 or (numPoints-2)
    // Interval: Given ta<=tb, t in [ta,tb} iffi t>=ta && t<tb 
    // Ascending: [timePoints[interval], timePoint[interval+1]}
    // Descending: [timePoints[interval+1], timePoint[interval]}
    private int getTimeInterval(double tTarget, int intervalStart) {
    	int interval;
    	// correct initial interval if needed
    	if (intervalStart<0) interval = 0;
    	else if (intervalStart> numPoints-2) interval = numPoints-2;
    	else interval = intervalStart;
    	if (isAscending) {
    		while ((interval >= 0) && (interval <= numPoints-2)) {
    			if (tTarget >= timePoints[interval+1]) interval++;
    			else if (tTarget < timePoints[interval]) interval--;
    			else break;
    		}
    	} else { // reverse order
    		while ((interval >= 0) && (interval <= numPoints-2)) {
    			if (tTarget >= timePoints[interval]) interval--;
    			else if (tTarget < timePoints[interval+1]) interval++;
    			else break;
    		} 		
    	} 	
    	return interval;
    }



	@Override
	public int getNumTimePoints() {
		return this.numPoints;
	}

	@Override
	public double getTime(int tp) {
		getTimePoints(); // convert to array
		return timePoints[tp];
	}

  
    

}
