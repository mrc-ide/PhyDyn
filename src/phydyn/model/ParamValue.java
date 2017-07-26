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
	
	public List<String> names;
	public RealParameter values;


	@Override
	public void initAndValidate() {
		names = paramNamesInput.get();
		values = paramValuesInput.get();
		if (names.size()!=values.getDimension()) {
			throw new IllegalArgumentException("Error in Model Value Input: lenght of names and values disagree");
		}
	}

}
