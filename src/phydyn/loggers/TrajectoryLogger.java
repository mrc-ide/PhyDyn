package phydyn.loggers;

import java.io.PrintStream;

import org.jblas.DoubleMatrix;

import beast.core.CalculationNode;
import beast.core.Input;
import beast.core.Input.Validate;
import beast.core.Loggable;
import phydyn.model.PopModelODE;
import phydyn.model.TimeSeriesFGY;
import phydyn.model.TimeSeriesFGY.FGY;
import phydyn.model.TimeSeriesFGYStd;
import phydyn.util.DVector;

public class TrajectoryLogger extends CalculationNode implements Loggable {
	
	final public Input<PopModelODE> popModelInput =  new Input<>("popModel",
	        "Population model to log.", Validate.REQUIRED);
	
	final public Input<Integer> frequencyInput = new Input<>("pointFrequency",
	        "Frequency between points in time series to be displayed");
	
        //final public Input<Integer> numPoints = new Input<>("pointFrequency",
        //	        "Number of points in time series to be logged");
	
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
		
		FGY fgy; // = ts.getFGY(0);
		DVector y; // = ySeries[0];
		int j;
		int idx = timeseries.getNumTimePoints() - 1;
		// The timeseries runs backwards in time
		// first - line
		fgy = timeseries.getFGY(idx);
		y = fgy.Yall;
		out.print(timeseries.getTime(idx)); // don't print sample number
		for(j = 0; j < popModel.yLength; j++) {
			out.print("\t"+y.get(j));
		}
		out.println("");

		// other lines
		idx -= frequency;
		while (idx > 0) {
			fgy = timeseries.getFGY(idx);
			y =  fgy.Yall;  //ySeries[idx];
			out.print(sample+"\t"+timeseries.getTime(idx));
			for(j = 0; j < popModel.yLength; j++) {
				out.print("\t"+y.get(j));
			}
			out.println("");
			idx -= frequency;
		}
		y = timeseries.getFGY(0).Yall;
		out.print(sample+"\t"+timeseries.getTime(0));
		for(j = 0; j < popModel.yLength; j++) {
			out.print("\t"+y.get(j));
		}
		out.println("");
	}

	@Override
	public void close(PrintStream out) {
		

	}

}
