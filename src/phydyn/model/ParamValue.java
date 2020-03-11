package phydyn.model;

import java.util.Arrays;
import java.util.List;

import beast.core.CalculationNode;
import beast.core.Input;
import beast.core.Input.Validate;
import beast.core.parameter.RealParameter;

/*
 * Changes: We no longer allow multiple names.
 * We allow multiple values in RealParameter. I multiple values are entered then
 * the PhyDyn parameter becomes a vector, and the isVector flag must be set to true.
 * We have introduced (pname,pvalue) - they will be replacing (names,values) 
 */

public class ParamValue extends CalculationNode {
	
	// changed List of String to string
	public Input<String> pNameInput = new Input<>(
				"pname", "Name of Model parameter");
		
	// changed List<String> to String - parsed at initialisation
	// changing this to pname (to be deprecated)
	public Input<String> paramNamesInput = new Input<>(
			"names", "Name(s) of Model parameter(s)", Validate.XOR, pNameInput);
		
	public Input<RealParameter> pValueInput = new Input<>(
			"pvalue", "Values of Model parameter(s)");
	
	public Input<RealParameter> paramValuesInput = new Input<>(
			"values", "Values of Model parameter(s)", Validate.XOR, pValueInput);
	
	// changed from vector to isVector
	public Input<Boolean> vectorInput = new Input<>(
			"isVector", "Indicates if variable is a vector", new Boolean(false));
	
	public List<String> names;
	public RealParameter values;
	public boolean isVector;
	public int dimension;

	@Override
	public void initAndValidate() {				
		String[] pnames;
		if (((pNameInput.get()!=null) && (pValueInput.get()==null)) ||
			((pNameInput.get()==null) && (pValueInput.get()!=null))) {
			throw new IllegalArgumentException("(ParamValue) pname and pvalue must be used together");
		}
		if (pNameInput.get()!=null) {
			pnames = pNameInput.get().split("\\s+");
			if (pnames.length > 1) {
				throw new IllegalArgumentException("(ParamValue) Only one parameter name allowed. Split: "+ pNameInput.get());

			}
		} else { 
			// look for names in paramNamesInput -- to be deprecated
			pnames = paramNamesInput.get().split("\\s+");
			if (pnames.length > 1) {
				throw new IllegalArgumentException("(ParamValue) Only one parameter name allowed. Split: "+ paramNamesInput.get());

			}
		}
		
		names = Arrays.asList(pnames);
		if (paramValuesInput.get()!=null) {
			values = paramValuesInput.get();
		} else {
			values = pValueInput.get();
		}
		isVector = vectorInput.get();
		dimension = values.getDimension();
		if (isVector) {
			if (names.size() != 1) {
				throw new IllegalArgumentException("Error in Model ParamValue: If using vector, ParamValue should only contain"
						+ " one variable name");
			}
			if (dimension==1) {
				System.out.println("Warning: ParamValue "+names+" set as vector but dimension is 1");
			}
		} else {
			if (names.size()!=values.getDimension()) {
				throw new IllegalArgumentException("Error in Model ParamValue "+names+" Length of names and values disagree.");
			}
		}
	}
	
	// use toString() instead of print

}
