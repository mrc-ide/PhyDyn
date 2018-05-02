package phydyn.model;


import phydyn.util.DMatrix;
import phydyn.util.DVector;

public interface TimeSeriesFGY {
	
	public class FGY {
		public  DMatrix F, G;
		public DVector D, Y, Yall;
		public FGY(DMatrix inF, DMatrix inG, DVector inY, 
				DVector inYall, DVector inD) { 
			F = inF; G = inG; Y = inY; Yall = inYall;D = inD; 
		}
	}
	
	public int lengthYall();
	
	public void addFGY(double t, DMatrix F, DMatrix G, double[] y, DVector D);

	public void reverse();
	
	
	public int getNumTimePoints();
	public double getTime(int tp);
	public FGY getFGY(int tp);
	public FGY getFGYfromTime(double t, int tp);
	public DVector getY(int tp);
	public DVector getYall(int tp);
	public void setMinY(int minY);
	
	public TimeSeriesFGY toFixedStep(int numPoints, PopModelODE model);
	
	public int getTimePoint(double tTarget, int pStart);
	

}

