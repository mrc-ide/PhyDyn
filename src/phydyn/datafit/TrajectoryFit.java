package phydyn.datafit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import beast.core.Distribution;
import beast.core.Input;
import beast.core.State;
import beast.core.util.Log;
import beast.core.Input.Validate;
import phydyn.model.PopModel;

public abstract class TrajectoryFit extends Distribution {
	
	private static String defaultSeparator = " ";
	public Input<PopModel> popModelInput = new Input<>("popmodel","Population Model",Validate.REQUIRED);
	public Input<String> dataInput = new Input<>("data", "Data entered in table format",Validate.REQUIRED);
	public Input<Boolean> headerInput = new Input<>("header", "Flag indicating if headers are included in data/table", false);
	public Input<String> separatorInput = new Input<>("separator", "Character used as data separator", defaultSeparator);
	
	PopModel popModel;
	String[] headers;
	int numrows, numcols;
	ArrayList<String>[] columns;
	
	@Override
	public void initAndValidate() {
		super.initAndValidate();
		popModel = popModelInput.get();
		// separator input must be a single character
		if (separatorInput.get().length() != 1) 
			throw new IllegalArgumentException("separator must be a single character");
		headers=null;
		readData();
		printTable();
	}
	
	public double[] getColumnAsDouble(int col) {
		if (col >= numcols || col <0 ) {
			Log.err("Column number out of bounds: "+col+" ( numcols = "+numcols+" )");
			return null;
		}
		double[] columnVector = new double[numrows];
		for(int row=0; row < numrows; row++) {
			try {
				columnVector[row] = Double.parseDouble(columns[col].get(row));
			} catch (Exception e) {
				Log.err("Problems parsing data as double: "+ columns[col].get(row));
				return null;
			}
		}
		return columnVector;
	}
	
	@SuppressWarnings("unchecked")
	private void readData() {
		BufferedReader reader = new BufferedReader(new StringReader(dataInput.get()));
		// first line determines number of columns
		String[] row = readRow(reader);
		if (row==null)
			throw new IllegalArgumentException("Failed to read data table - empty");
		numcols = row.length;
		if (numcols < 2)
			throw new IllegalArgumentException("Data table must have at least 2 columns");
		System.out.println("Data Points: (numcols="+numcols+")");
		if (headerInput.get()) {
			headers = row;	
			row = readRow(reader);
		} else {
			numcols = row.length;
		}
		columns = (ArrayList<String>[])new ArrayList[numcols];
		for(int i=0; i<numcols; i++)
			columns[i] = new ArrayList<String>();
		int rownumber = 0;
		while (row != null) {
			if (row.length != numcols)
				throw new IllegalArgumentException("(data) Incorrect number of columns at row "+rownumber+" . Must have "+numcols+" columns.");
			for(int i = 0; i < numcols; i++)
				columns[i].add(row[i]);
			rownumber++;
			row = readRow(reader);			
		}
		numrows = rownumber;
	}
	
	/* read next line - skips empty lines. 
	 * return: trimmed line / null if end of string
	 */
	private String[] readRow(BufferedReader reader) {
		String str = null;
		while(true) {
			try {
				str = reader.readLine();
			} catch (Exception e) {
				throw new IllegalArgumentException("Error while reading dataInput");
			}
			if (str==null) break;
			str = str.trim();
			if (str.length()>0) break;
		}
		if (str==null) return null;
		String[] row = str.split(separatorInput.get());
		for(int i=0; i < row.length; i++) {
			row[i] = row[i].trim();
		}
		return row;
	}
	
	void printTable() {
		int row,col;
		System.out.println("------ table -------");
		if (headers!=null) {
			for(col=0; col < numcols-1; col++)
				System.out.print(headers[col]+separatorInput.get());
			System.out.println(headers[col]);
		}
		for(row=0; row < numrows; row++) {
			for(col=0; col < numcols-1; col++)
				System.out.print(columns[col].get(row)+separatorInput.get());
			System.out.println(columns[col].get(row));
		}
		System.out.println("------ end table -------");
	}
	
	/**
     * @return the normalised probability (density) for this distribution.
     *         Note that some efficiency can be gained by testing whether the
     *         Distribution is dirty, and if not, call getCurrentLogP() instead
     *         of recalculating.
     */
	//@Override
    //public double calculateLogP() {
    //    logP = 0;
    //    return logP;
    //}
	
     
    /* 
     * Methods inherited from Distribution - not used. Default implementation provided. 
     * */
    
    /**
     * @return a list of unique ids for the state nodes that form the argument
     */
	@Override
	public List<String> getArguments() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
     * @return a list of unique ids for the state nodes that make up the conditions
     */
	@Override
	public List<String> getConditions() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
     * This method draws new values for the arguments conditional on the current value(s) of the conditionals.
     * <p/>
     * The new values are overwrite the argument values in the provided state.
     *
     * @param state  the state
     * @param random random number generator
     */
	@Override
	public void sample(State state, Random random) {
		// TODO Auto-generated method stub

	}

}
