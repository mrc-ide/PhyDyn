package phydyn.model;
import beast.core.BEASTObject;
import beast.core.Input;
import beast.core.Input.Validate;
import beast.core.parameter.RealParameter;

public class VariableValue extends BEASTObject {

	public Input<String> varNameInput = new Input<>(
            "varName",
            "Name of population/deme.",Validate.REQUIRED);

    public Input<RealParameter> varValueInput = new Input<>(
            "varValue",
            "Value of model variable",Validate.REQUIRED);
    
   

	@Override
	public void initAndValidate()  {
		// TODO Auto-generated method stub

	}

}
