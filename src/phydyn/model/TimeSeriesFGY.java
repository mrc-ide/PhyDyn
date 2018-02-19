package phydyn.model;

import org.jblas.DoubleMatrix;

public interface TimeSeriesFGY {
	
	public class FGY {
		public  DoubleMatrix F, G, Y, Yall, D;
		public FGY(DoubleMatrix inF, DoubleMatrix inG, DoubleMatrix inY, 
				DoubleMatrix inYall, DoubleMatrix inD) { 
			F = inF; G = inG; Y = inY; Yall = inYall;D = inD; 
		}
	}
	
	public int lengthYall();
	
	public void addFGY(double t, DoubleMatrix F, DoubleMatrix G, double[] y, DoubleMatrix D);

	public void reverse();
	
	
	public int getNumTimePoints();
	public double getTime(int tp);
	public FGY getFGY(int tp);
	public FGY getFGYfromTime(double t, int tp);
	public DoubleMatrix getY(int tp);
	public DoubleMatrix getYall(int tp);
	public void setMinY(int minY);
	
	public TimeSeriesFGY toFixedStep(int numPoints, PopModelODE model);
	
	public int getTimePoint(double tTarget, int pStart);
	

}

