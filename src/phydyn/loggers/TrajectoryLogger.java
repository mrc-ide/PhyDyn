package phydyn.loggers;

import java.io.PrintStream;

import org.jblas.DoubleMatrix;

import beast.core.CalculationNode;
import beast.core.Input;
import beast.core.Input.Validate;
import beast.core.Loggable;
import phydyn.model.PopModelODE;
import phydyn.model.TimeSeriesFGY;

public class TrajectoryLogger extends CalculationNode implements Loggable {
	
	final public Input<PopModelODE> popModelInput =  new Input<>("popModel",
	        "Population model to log.", Validate.REQUIRED);
	
	final public Input<Integer> frequencyInput = new Input<>("pointFrequency",
	        "Frequency between points in time series to be displayed");
	
	final public Input<Integer> numPoints = new Input<>("pointFrequency",
	        "Number of points in time series to be logged");
	
	// we could also add a point distance input parameter

	private PopModelODE popModel;
	private int frequency=1;
	private boolean useFrequency=false, usePoints=false;
	
	public TrajectoryLogger() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initAndValidate() {
		popModel = popModelInput.get();		
		if (frequencyInput.get() != null) {
			frequency = frequencyInput.get();
		} 
		if (frequency < 1) frequency = 1;
	}

	@Override
	public void init(PrintStream out) {
		out.print("t");
		for (int i = 0; i < popModel.yLength; i++) {
			out.print("\t"+popModel.yNames[i]);
		}
	}

	@Override
	public void log(int sample, PrintStream out) {
		TimeSeriesFGY timeseries = popModel.getTimeSeries();
		DoubleMatrix[] ySeries = timeseries.getAllYs();
		double[] timePoints = timeseries.getTimePoints();
		DoubleMatrix y = ySeries[0];
		int j;
		int idx = timePoints.length - 1;	
		// The timeseries runs backwards in time
		// first line -
		y = ySeries[idx];
		out.print(timePoints[idx]); // don't print sample number
		for(j = 0; j < popModel.yLength; j++) {
			out.print("\t"+y.get(j));
		}
		out.println("");

		// other lines
		idx -= frequency;
		while (idx > 0) {
			y = ySeries[idx];
			out.print(sample+"\t"+timePoints[idx]);
			for(j = 0; j < popModel.yLength; j++) {
				out.print("\t"+y.get(j));
			}
			out.println("");
			idx -= frequency;
		}
		y = ySeries[0];
		out.print(sample+"\t"+timePoints[0]);
		for(j = 0; j < popModel.yLength; j++) {
			out.print("\t"+y.get(j));
		}
		out.println("");
	}

	@Override
	public void close(PrintStream out) {
		

	}

}
