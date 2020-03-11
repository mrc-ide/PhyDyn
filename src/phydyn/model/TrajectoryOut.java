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
	public Input<String> RfileInput = new Input<>("Rfile", "output trajectory file");
	
	private String fileName;
	private PopModel popModel;
	
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
		popModel = modelInput.get();
		
		if (!popModel.hasEndTime()) {
			throw new IllegalArgumentException("Trajectory End Time (t0) unspecified - quitting");
		}
	}

	@Override
	public void run() throws Exception {	
		popModel.update();
		popModel.printModel();  // print equations
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
		
	    if (RfileInput.get()!= null) {
	    	try {
	    		generateRfile();
	    	} catch(Exception e) {
	    		e.printStackTrace();
	    		System.out.println("Failed to generate R file");
	    	}
	    }
	}
	
	private void generateRfile() throws Exception {
		PopModelODE modelODE=null;
		if (popModel instanceof PopModelODE)
			modelODE = (PopModelODE) popModel;
		else
			throw new RuntimeException("Expecting PopModelODE");
		PopModelODETranslator trans = new PopModelODETranslator(modelODE);		
		FileWriter writer = new FileWriter(RfileInput.get());
		final String strR = trans.GenerateR();
		writer.append(strR);
		writer.flush();
	    writer.close();
		System.out.println("R code generated:\n"+strR);
	}
	

}
