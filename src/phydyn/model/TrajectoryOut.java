package phydyn.model;

import java.io.FileWriter;

import org.jblas.DoubleMatrix;

import beast.core.Input;
import beast.core.Input.Validate;
import beast.core.Runnable;
import phydyn.model.TimeSeriesFGY.FGY;

public class TrajectoryOut extends Runnable {
	
	public Input<PopModel> modelInput = new Input<>("model","Complex Population Model", Validate.REQUIRED);
	public Input<String> fileInput = new Input<>("file", "output trajectory file");
	// file type. Currently only option is csv (default)
	
	private String fileName;
	
	/**
	 * initAndValidate is supposed to check validity of values of inputs, and initialise. 
	 * If for some reason this fails, the most appropriate exception to throw is 
	 * IllegalArgumentException (if the combination of input values is not correct)
	 * or otherwise a RuntimeException.
	 */	
	@Override
	public void initAndValidate() {
		if (fileInput.get()==null)
			fileName = "trajectory.csv";
		else
			fileName = fileInput.get();
	}

	@Override
	public void run() throws Exception {
				
		modelInput.get().update();
		modelInput.get().printModel();  // print equations
		TimeSeriesFGY ts = modelInput.get().getTimeSeries();
		ts.reverse();
		
		FileWriter writer = new FileWriter(fileName);
		
		//double[] timePoints = ts.getTimePoints();
		//DoubleMatrix[] Ys = ts.getAllYs();
		
		int n = ts.lengthYall();
		int numPoints = ts.getNumTimePoints();
		
		String[] demeNames = modelInput.get().demeNames;
		String[] nonDemeNames = modelInput.get().nonDemeNames;
		
		writer.append("t");
		for(int i=0; i < demeNames.length; i++)
			writer.append(","+demeNames[i]);
		for(int i=0; i < nonDemeNames.length; i++)
			writer.append(","+nonDemeNames[i]);
		writer.append("\n");
		
		for(int i = 0; i < numPoints; i++) {
			FGY fgy = ts.getFGY(i);
			writer.append(""+ts.getTime(i));
			//System.out.println("F="+fgy.F);
			//System.out.println("G="+fgy.G);
			for(int j = 0; j < n; j++) {
				writer.append(","+fgy.Yall.get(j));
			}
			writer.append("\n");
		}
		
		writer.flush();
	    writer.close();
		

	}

}
