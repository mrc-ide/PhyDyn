package phydyn.app.beauti;

import beast.app.draw.*;
import phydyn.model.*;

public class PhyDynPopModelODEInputEditor extends BEASTObjectInputEditor {

    private static final long serialVersionUID = 1L;

	public PhyDynPopModelODEInputEditor(BeautiDoc doc) {
		super(doc);
	}

    @Override
    public Class<?> type() {
        return phydyn.model.PopModelODE.class;
    }

    @Override
    public void init(Input<?> input, BEASTInterface beastObject, int itemNr,
    		ExpandOption isExpandOption, boolean addButtons) {
    		// TODO: set up GUI here
    		// keep track of 
    		// o which parameters there are, 
    		// o initial value of each parameter, 
    		// o whether estimated or not, 
    		// o scale vs location (for type of operator)
	}

    public static boolean customConnector(BeautiDoc doc) {
	    try {
	    	// TODO: find PopModelODE object, possible cached in PhyDynPopModelODEInputEditor, otherwise from doc object
	    	// TODO: remove all PhyDyn Parameters from the State, the tracelog, + remove associated operators
    		// TODO: create parameters (if not already there)
    		// make sure ID is unique, e.g. "PhyDynParameter_XXX" where XXX the actual name you want it to have say "gamma"
    		// TODO: if estimated, connect to state, trace logger, 
    		// create Prior on parameter and connect to the "prior" CompounDistribution
    		// create Scale or RandomWalk operator referring to the new Parameter and connect operators to MCMC object
    		return true;
    	} catch (Exception e) {
    	
    	}
    	return false;
	}

}