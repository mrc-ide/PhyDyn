package phydyn.model;

import org.antlr.v4.runtime.tree.ParseTree;

import phydyn.model.parser.PopModelParser.ExprContext;


/* 
 * PhyDyn matrix equation - no XML inputs
 * This is NOT a BEAST object
 */



public class MatrixEquationObj {
	public enum EquationType { BIRTH, DEATH, MIGRATION, NONDEME };
	public EquationType type;
	public int row,column;
	public ExprContext rhsExprCtx;
	public String originName, destinationName; 
	
	public PMMachineCode code;

	public MatrixEquationObj(EquationType eqType, String originName, String destinationName, ExprContext tree) {
		this.type = eqType;
		this.originName = originName;
		this.destinationName = destinationName;
		this.rhsExprCtx = tree;
	}
	
	public MatrixEquationObj(EquationType eqType, String originName, ExprContext tree) {
		this.type = eqType;
		this.originName = originName;
		this.destinationName = null;
		this.rhsExprCtx = tree;
	}

	
	public String getLHS() {
		String s="";
		switch(type) {
		case BIRTH:
			s = "F("+this.originName+","+this.destinationName+")"; break;
		case MIGRATION:
			s = "G("+this.originName+","+this.destinationName+")"; break;
		case DEATH:
			s = "D("+this.originName+")"; break;
		default: // non-deme
			s = "dot(" + this.originName +")";
		}
		return s;
	}

	public void completeValidation(PopModelODE model) {
		if (type==EquationType.NONDEME) {
			row = model.indexOf(model.nonDemeNames, originName );
		} else {
			row = model.indexOf( model.demeNames, originName);
		}
		
		// rows (origin) must be correct since they name lists were collected from originName
		// if (row == -1) {
		//	throw new IllegalArgumentException("Unknown origin name in matrix eq: "+originNameInput.get());
		//}
		
		if (destinationName != null) {
			column = model.indexOf( model.demeNames, destinationName);
			if (column==-1) {
				throw new IllegalArgumentException("Unknown destination name in matrix eq: "+destinationName);
			}
		}
		if ((type==EquationType.BIRTH) && (row != column)) { 
			model.setDiagF(false);
		}
		// additional type checking would be ideal
		
	}
	
	public int compile(PopModelCompiler compiler) {
		code = compiler.compile(rhsExprCtx);
		return code.maxStackSize;
	}
	
	

	
}
