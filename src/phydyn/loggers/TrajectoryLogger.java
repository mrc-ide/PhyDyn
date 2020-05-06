package phydyn.loggers;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.jblas.DoubleMatrix;

import beast.core.CalculationNode;
import beast.core.Input;
import beast.core.Input.Validate;
import beast.core.Loggable;
import phydyn.model.MatrixEquationObj;
import phydyn.model.MatrixEquationObj.EquationType;
import phydyn.model.PopModelODE;
import phydyn.model.TimeSeriesFGY;
import phydyn.model.TimeSeriesFGY.FGY;
import phydyn.model.TimeSeriesFGYStd;
import phydyn.util.DMatrix;
import phydyn.util.DVector;

public class TrajectoryLogger extends CalculationNode implements Loggable {
	
	final public Input<PopModelODE> popModelInput =  new Input<>("popModel",
	        "Population model to log.", Validate.REQUIRED);
	
	final public Input<Integer> frequencyInput = new Input<>("pointFrequency",
	        "Frequency between points in time series to be displayed");
	
	private enum RateType { ALLF, ALLG, RATEG, RATEF };
	
	final public Input<String> ratesInput = new Input<>("logrates",
			"rates to log form birth and migration matrices");
	//final public Input<Integer> numPoints = new Input<>("numPoints",
	//        "Number of points in time series to be logged");
	
	// we could also add a point distance input parameter

	private PopModelODE popModel;
	private int frequency=1;
	// private boolean useFrequency=false, usePoints=false;
	private int numLogRates = 0;
	boolean allF, allG;
	
	private LoggedRate[] loggedRates;
	//private RateType[] matrixName;
	//private String[] logRateHeader;
	//private int[] demeRow, demeColumn;
	
	
	private List<MatrixEquationObj> births, migs, logbirths, logmigs;
	
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
		if (ratesInput.get()!=null) {

			processRatesString(ratesInput.get());
		}
		if (this.numLogRates > 0) {
			System.out.print("Logging rates: ");
			for(int i=0; i<numLogRates; i++) {
				System.out.print(loggedRates[i].header+ " ");
			}
			System.out.println(" ");
		}		
	}

	@Override
	public void init(PrintStream out) {
		out.print("t");
		for (int i = 0; i < popModel.yLength; i++) {
			out.print("\t"+popModel.yNames[i]);
		}
		if (numLogRates>0) {
			for(int i=0; i < numLogRates; i++) {
				out.print("\t"+loggedRates[i].header);
			}
		}
	}

	@Override
	public void log(int sample, PrintStream out) {
		TimeSeriesFGY timeseries = popModel.getTimeSeries();
		
		FGY fgy; // = ts.getFGY(0);
		DVector y; // = ySeries[0];
		DMatrix F,G;
		//int j;
		int idx = timeseries.getNumTimePoints() - 1;
		// The timeseries runs backwards in time
		// first - line
		fgy = timeseries.getFGY(idx);
		y = fgy.Yall;
		out.print(timeseries.getTime(idx)); // don't print sample number
		for(int j = 0; j < popModel.yLength; j++) {
			out.print("\t"+y.get(j));
		}
		if (numLogRates>0) {
			F = fgy.F; G = fgy.G;
			for(int j = 0; j < numLogRates; j++) {
				if (loggedRates[j].type == RateType.RATEF) {
					out.print("\t"+F.get(loggedRates[j].row, loggedRates[j].column));
				} else {
					out.print("\t"+G.get(loggedRates[j].row, loggedRates[j].column));
				}
			}
		}
		out.println("");

		// other lines
		idx -= frequency;
		while (idx > 0) {
			fgy = timeseries.getFGY(idx);
			y =  fgy.Yall;  //ySeries[idx];
			out.print(sample+"\t"+timeseries.getTime(idx));
			for(int j = 0; j < popModel.yLength; j++) {
				out.print("\t"+y.get(j));
			}
			if (numLogRates>0) {
				F = fgy.F; G = fgy.G;
				for(int j = 0; j < numLogRates; j++) {
					if (loggedRates[j].type == RateType.RATEF) {
						out.print("\t"+F.get(loggedRates[j].row, loggedRates[j].column));
					} else {
						out.print("\t"+G.get(loggedRates[j].row, loggedRates[j].column));
					}
				}
			}
			out.println("");
			idx -= frequency;
		}
		// last line - idx = 0
		fgy = timeseries.getFGY(0);
		y = timeseries.getFGY(0).Yall;
		out.print(sample+"\t"+timeseries.getTime(0));
		for(int j = 0; j < popModel.yLength; j++) {
			out.print("\t"+y.get(j));
		}
		if (numLogRates>0) {
			F = fgy.F; G = fgy.G;
			for(int j = 0; j < numLogRates; j++) {
				if (loggedRates[j].type == RateType.RATEF) {
					out.print("\t"+F.get(loggedRates[j].row, loggedRates[j].column));
				} else {
					out.print("\t"+G.get(loggedRates[j].row, loggedRates[j].column));
				}
			}
			//for(int j = 0; j < numLogRates; j++) {
			//	if (matrixName[j]== RateType.RATEF) {
			//		out.print("\t"+F.get(demeRow[j], demeColumn[j]));
			//	} else {
			//		out.print("\t"+G.get(demeRow[j], demeColumn[j]));
			//	}
			//}
		}
		out.println("");
	}

	@Override
	public void close(PrintStream out) {
		
	}
	
	void processRatesString(String ratesStr) {
		//System.out.println("Processing: "+ratesStr);
		// row = model.indexOf( model.demeNames, originName);
		ratesStr = ratesStr.trim();
		if (ratesStr.equals("all")) {
			ratesStr = "F  G";
		}
		String[] ratesArray = ratesStr.trim().split("\\s+");
		if (ratesArray.length == 0) {
			System.out.println("Warning: logrates parameter (TrajectoryLogger) empty");
			return;
		}
				
		allF = allG = false;
		births = new ArrayList<MatrixEquationObj>();
		migs = new ArrayList<MatrixEquationObj>();
		logbirths =  new ArrayList<MatrixEquationObj>();
		logmigs = new ArrayList<MatrixEquationObj>();
		for(MatrixEquationObj eq: popModel.equations) {
			if (eq.type==EquationType.BIRTH) births.add(eq);
			else if (eq.type==EquationType.MIGRATION) migs.add(eq);
		}
		for (String rateStr : ratesArray) {
			processRate(rateStr);
		}
		numLogRates = logbirths.size()+logmigs.size();
		// initlaize arrays
		loggedRates = new LoggedRate[numLogRates];
		
		int i=0;
		for(MatrixEquationObj eq: logbirths) {
			loggedRates[i] = new LoggedRate(eq.getLHS(),RateType.RATEF,eq.row,eq.column);
			i++;
		}
		for(MatrixEquationObj eq: logmigs) {
			loggedRates[i] = new LoggedRate(eq.getLHS(),RateType.RATEG,eq.row,eq.column);
			i++;
		}
		
	}
	
	private class LoggedRate {
		String header;
		RateType type;
		int row, column;
		LoggedRate(String h, RateType t, int r, int c){
			header=h; type=t; row=r; column=c;
		}
	}
	
	void processRate(String rateStr) {
		
		final RateToken rate = parseRate(rateStr);
				
		if (rate.type==RateType.ALLF) {
			allF=true; logbirths=births; return;
		}
		if (rate.type==RateType.ALLG) {
			allG=true; logmigs=migs; return;
		}
		
		boolean logRate=false;
		if (rate.type==RateType.RATEF) {
			if (allF) return;
			for(MatrixEquationObj eq: births) {
				if (rate.row==eq.row && rate.column==eq.column) {
					logbirths.add(eq); logRate=true; break;
				}
			}			
		} else if (rate.type==RateType.RATEG) {
			if (allG) return;
			for(MatrixEquationObj eq: migs) {
				if (rate.row==eq.row && rate.column==eq.column) {
					logmigs.add(eq); logRate=true; break;
				}
			}
		}
		if (!logRate)
			System.out.println("(TrajectoryLogger) Not logging "+rateStr+": not defined by popmodel equation.");
		return;
	}
	
	private class RateToken {
		public RateType type;
		public int row,column;
		RateToken(RateType t) {
			if (t!=RateType.ALLG && t!=RateType.ALLF)
				throw new IllegalArgumentException("Programming error: Incorrect rate type passed to RateToken");
			type=t;
		}
		RateToken(RateType t, int r, int c) {
			if (t!=RateType.RATEF && t!=RateType.RATEG)
				throw new IllegalArgumentException("Programming error: Incorrect rate type passed to RateToken");
			type=t; row=r; column=c;
		}
	}
	
	// Grammar: generates AST. rateStr tirmmed 
	RateToken parseRate(String rateStr) {
		List<String> tokens = new ArrayList<String>();
		// syntax: ('F'|'G') [ '(' <deme> ',' <deme> ')' ]
		if (rateStr.equals("F")) return new RateToken(RateType.ALLF);
		if (rateStr.equals("G")) return new RateToken(RateType.ALLG);
		int state = 0;
		int firstIdx=0,idx=0;

		char match= 'x';
		while(idx < rateStr.length()) {
			final char chr = rateStr.charAt(idx);
			switch (state) {
			case 0: 
			case 2:
			case 4:
				if (chr=='(' || chr==')' || chr==',') {  // marks end of id
					if (idx<=firstIdx)
						throw new IllegalArgumentException("Error parsing lograte entry "+rateStr);
					tokens.add(rateStr.substring(firstIdx, idx));	// check indices
					match = (state==0) ? '(' : ((state==2) ? ',' : ')');
					state++;
				} else {
					idx++; 
				}
				break;
			case 1: 
			case 3:
			case 5:
				if (chr==match) { state++; idx++; firstIdx=idx;}
				else throw new IllegalArgumentException("Error parsing matrix entry "+rateStr+": expecting '"+match+"'");
				break;
			default:
				System.out.println("state "+state);
				throw new IllegalArgumentException("Error parsing matrix entry "+rateStr);
			}
		}
		// we must have three tokens in list
		if (state != 6) throw new IllegalArgumentException("Error parsing matrix entry "+rateStr); 
		if (tokens.size() != 3) {
			throw new IllegalArgumentException("Error parsing matrix entry "+rateStr);
		}
		String matrix = tokens.get(0);
		// check rows and columns
		String deme = tokens.get(1).trim();
		int row = popModel.indexOf(popModel.demeNames, deme);
		if (row<0) {
			throw new IllegalArgumentException("(Trajectory Logger): Error parsing logrates. Unknown deme name "+ deme+" in "+rateStr);
		}
		deme = tokens.get(2).trim();
		int column = popModel.indexOf(popModel.demeNames, deme);
		if (column<0) {
			throw new IllegalArgumentException("(Trajectory Logger): Error parsing logrates. Unknown deme name "+ deme+" in "+rateStr);
		}
				
		if (matrix.equals("F")) return new RateToken(RateType.RATEF,row,column);
		else if (matrix.equals("G")) return new RateToken(RateType.RATEG,row,column);
		throw new IllegalArgumentException("Error parsing matrix entry "+rateStr+": expecting matrix name F or G ");
	}
	

}
