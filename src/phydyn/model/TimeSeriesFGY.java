package phydyn.model;

import java.util.List;

import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;
import org.jblas.DoubleMatrix;

//import org.jblas.DoubleMatrix;

import java.util.ArrayList;
import java.util.Collections;

public class TimeSeriesFGY {
	// FirstOrderDifferentialEquations model;
	int numDemes, numNonDemes, numPoints;
	boolean isFixedStep;
	List<Double> timePointsList;
	List<DoubleMatrix> Ylist, allYlist;
	List<DoubleMatrix> Flist, Glist;
	private boolean isAscending=true;
	/* array representations */
	public double[] timePoints;
	DoubleMatrix[] Fs,Gs, Ys, allYs;
	private boolean addedStep=false;
	

	// TimeSeriesFGY(int numDemes, FirstOrderDifferentialEquations model)
	TimeSeriesFGY(PopModelODE model) {
		this.numDemes = model.numDemes;
		this.numNonDemes = model.numNonDemes;
		this.isFixedStep = model.fixedStepSize;
		numPoints=0;
		timePointsList = new ArrayList<Double>();
		Ylist = new ArrayList<DoubleMatrix>();
		allYlist = new ArrayList<DoubleMatrix>();
		Flist = new ArrayList<DoubleMatrix>();
		Glist = new ArrayList<DoubleMatrix>();
	}
    
    
    /* Creates a new entry for time step t with deme values stored in y */
    /* the size of y is >= nstates since it also contains the nonDeme elements */
    public void addY(double t,double[] y) {
    	//System.out.println("Added y at t="+t+" y0="+y[0]);
    	int i,j;
    	DoubleMatrix Ynew;
    	timePointsList.add(t);
    	double[] yDemes = new double[numDemes]; 
    	for(i=0; i < numDemes; i++) { yDemes[i] = y[i]; }
    	Ynew = new DoubleMatrix(numDemes,1,yDemes); /*  keeps buffer yDemes */ 
    	Ylist.add(Ynew);
    	// copy all y's 
    	double[] yall = new double[numDemes+numNonDemes];
    	for(i=0; i < numDemes+numNonDemes; i++) { yall[i] = y[i]; }
    	Ynew = new DoubleMatrix(numDemes+numNonDemes,1,yall); /*  keeps buffer yDemes */
    	allYlist.add(Ynew); 	
    	numPoints++;
    	addedStep = true;
    }
    
    public void addFG(double t, DoubleMatrix F, DoubleMatrix G) {
    	if (!addedStep) return;
    	//System.out.println("Added FG");
    	/* no checking yet */
    	DoubleMatrix Fnew = new DoubleMatrix();
    	DoubleMatrix Gnew = new DoubleMatrix();
    	Fnew.copy(F);
    	Gnew.copy(G);
    	Flist.add(Fnew);
    	Glist.add(Gnew);
    	addedStep = false;
    }
    
    public void reverse() {
    	timePoints = null; Ys = null; allYs = null; Fs = Gs = null;
    	Collections.reverse(timePointsList);
    	Collections.reverse(Flist);
    	Collections.reverse(Glist);
    	Collections.reverse(Ylist);
    	Collections.reverse(allYlist);
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
    
    public DoubleMatrix[] getYs() {
    	if (Ys == null) {
    		Ys = new DoubleMatrix[Ylist.size()];
    		Ylist.toArray(Ys);
    	}
    	return Ys;
    }
    
    public DoubleMatrix[] getAllYs() {
    	if (allYs == null) {
    		allYs = new DoubleMatrix[allYlist.size()];
    		allYlist.toArray(allYs);
    	}
    	return allYs;
    }
    
    public DoubleMatrix[] getFs() {
    	if (Fs == null) {
    		Fs = new DoubleMatrix[Flist.size()];
    		Flist.toArray(Fs);
    	}
    	return Fs;
    }
    
    public DoubleMatrix[] getGs() {
    	if (Gs == null) {
    		Gs = new DoubleMatrix[Flist.size()];
    		Glist.toArray(Gs);
    	}
    	return Gs;
    }
    
    public TimeSeriesFGY toFixedStep(int numPoints, PopModelODE model) {
    	int n,idx;
    	double t0, t1, t, f;
    	DoubleMatrix y;
    	TimeSeriesFGY ts = new TimeSeriesFGY(model);
    	ts.isFixedStep = true;
    	double[] timePoints = this.getTimePoints();  // access timePoints Array
    	DoubleMatrix[] allYs = this.getAllYs();
    	n = timePoints.length;
    	t0 = timePoints[0];
    	t1 = timePoints[n-1];
    	// tstep = (t1-t0)/(numPoints-1);
    	// Process first point
    	y = allYs[0];
    	ts.addY(t0, y.data);
    	model.updateMatrices(t0, y.data);
    	ts.addFG(t0,model.births,model.migrations);
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
    		allYs[idx+1].subi(allYs[idx],y);
    		y.muli(f);  // in-place
    		y.addi(allYs[idx]); // in-place
    		ts.addY(t, y.data);
        	model.updateMatrices(t, y.data);
        	ts.addFG(t,model.births,model.migrations);
    	}
    	// Process last point
    	y = allYs[n-1];
    	ts.addY(t1, y.data);
    	model.updateMatrices(t1, y.data);
    	ts.addFG(t1,model.births,model.migrations);
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
    public int getTimeInterval(double tTarget, int intervalStart) {
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
    
  
    

}
