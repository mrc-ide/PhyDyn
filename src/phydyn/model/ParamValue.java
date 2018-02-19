package phydyn.model;

import java.util.ArrayList;
import java.util.List;

import beast.core.CalculationNode;
import beast.core.Input;
import beast.core.Input.Validate;
import beast.core.parameter.RealParameter;

public class ParamValue extends CalculationNode {
	
	public Input<List<String>> paramNamesInput = new Input<>(
			"names", "Name(s) of Model parameter(s)", new ArrayList<>());
	
	public Input<RealParameter> paramValuesInput = new Input<>(
			"values", "Values of Model parameter(s)", Validate.REQUIRED);
	
	public Input<Boolean> vectorInput = new Input<>(
			"vector", "Indicates if variable is a vector", new Boolean(false));
	
	public List<String> names;
	public RealParameter values;
	public boolean isVector;
	public int dimension;

	@Override
	public void initAndValidate() {
		names = paramNamesInput.get();
		values = paramValuesInput.get();
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

}
