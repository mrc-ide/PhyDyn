package phydyn.model;

import beast.core.CalculationNode;
import beast.evolution.tree.TraitSet;
import beast.evolution.tree.Tree;

public abstract class PopModel extends CalculationNode {
	
	public int numDemes, numNonDemes; // m = numDemes
	public String[] demeNames, nonDemeNames;
	boolean diagF;

	@Override
	public void initAndValidate() {

	}
	
	/* Legacy needed by Density class -  default implementation */
	public abstract String getName();
	public int getNumStates() { return numDemes; }
	
	
	public abstract TimeSeriesFGY getTimeSeries();
	
	public abstract boolean hasEndTime();
	public abstract double getEndTime();
	public abstract void setEndTime(double newt1);
	public abstract void unsetEndTime();
	
	public abstract boolean hasStartTime();
	public abstract void setStartTime(double t0);
	public abstract double getStartTime();
	
	
	public void setDiagF(boolean b) { diagF = b; }
	public boolean isDiagF() { return diagF; }
	public boolean isConstant() { return false; }
	
	// Replace this with call to hashmap
	public int indexOf(String[] a, String s) {
		for(int i=0; i < a.length; i++)
			if (a[i].equals(s))
				return i;
		return -1;
	}
	
	/* Maps stateNames to stateNumbers 0,...,m-1 */
	public int getStateFromName(String name) {
		return this.indexOf(demeNames,name);
	}
	
	public String getDemesString(String sep) {
		if (numDemes<1) return "";
		String str = demeNames[0];
		for(int i = 1; i < numDemes; i++) {
			str+= sep+" "+demeNames[i];
		}
		return str;
	}
	
	public abstract boolean isParameter(String paramName);
	public abstract String getParameterValue(String paramName);
	
	public void printModel() {
		System.out.println("Population Model:");
	}
	
	
}