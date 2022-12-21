package phydyn.model;


// import org.antlr.v4.runtime.ANTLRInputStream;

import beast.base.core.BEASTObject;
import beast.base.core.Input;
import beast.base.core.Input.Validate;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import phydyn.model.MatrixEquationObj.EquationType;
import phydyn.model.parser.PopModelLexer;
import phydyn.model.parser.PopModelParser;
import phydyn.model.parser.PopModelParser.ExprContext;

/*
 * PhyDyn Matrix equations with XML input
 */
		

public class MatrixEquation extends BEASTObject {
	
	public Input<String> equationTypeInput = new Input<>(
            "type",
            "Equation type (birth/death/migration/nondeme",Validate.REQUIRED);
	
	public Input<String> originNameInput = new Input<>(
			"origin",
			"State var name correspondig to row entry in matrix (vector)", Validate.REQUIRED);
	
	public Input<String> destinationNameInput = new Input<>(
			"destination",
			"State var name correspondig to column entry in matrix");
	
	public Input<String> equationStringInput = new Input<>(
            "value",
            "String description of matrix equation", Validate.REQUIRED);
	
	public EquationType type;
	public int row,column;
	public ExprContext rhsExprCtx;
	public PMMachineCode code;

	@Override
	public void initAndValidate() {
		// validate equation type
		String strType = equationTypeInput.get();
		if (strType.equals("birth")) {
			type = EquationType.BIRTH;
		} else if (strType.equals("death")) {
			type = EquationType.DEATH;
		} else if (strType.equals("migration")) {
			type = EquationType.MIGRATION;
		} else if (strType.equals("nondeme")) {
			type = EquationType.NONDEME;
		} else {
			throw new IllegalArgumentException("Unknown equation type (birth/death/migration/nondeme");
		}
		// validate row/column
		if (destinationNameInput.get()==null) {
			if ((type==EquationType.BIRTH)||(type==EquationType.MIGRATION)) {
				throw new IllegalArgumentException("Must specify column name for equation: "+equationStringInput.get());
			}
		} else if ((type==EquationType.DEATH)||(type==EquationType.NONDEME)) {
			throw new IllegalArgumentException("Should not specify column name for equation: "+equationStringInput.get());
		}
		/* parse equation string */
		CodePointCharStream  input = CharStreams.fromString( equationStringInput.get()  );
		//ANTLRInputStream input = new ANTLRInputStream(equationStringInput.get());
		try {
			PopModelLexer lexer = new PopModelBailLexer(input); 
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			PopModelParser parser = new PopModelParser(tokens);
			parser.setErrorHandler(new PopModelParserErrorStrategy());
			rhsExprCtx = parser.expr();
		} catch (Exception e) {
			System.out.println("Error while parsing equation:"+ equationStringInput.get());
			throw new IllegalArgumentException("Parsing error");
		}
		return;
	}
	
	public MatrixEquationObj createMatrixEquation() { 
		MatrixEquationObj eq=null;
		switch(type) {
		case BIRTH:
			eq = new MatrixEquationObj(type,originNameInput.get(),destinationNameInput.get(), rhsExprCtx);
			break;
		case MIGRATION:
			eq = new MatrixEquationObj(type,originNameInput.get(),destinationNameInput.get(), rhsExprCtx);
			break;
		case DEATH:
			eq = new MatrixEquationObj(type,originNameInput.get(), rhsExprCtx);
			break;
		case NONDEME:
			eq = new MatrixEquationObj(type,originNameInput.get(), rhsExprCtx);
			break;
		default:
			throw new IllegalArgumentException("Missing Matrix type: "+type);

		}
		return eq;
	}
	
	public String getLHS() {
		String s="";
		switch(type) {
		case BIRTH:
			s = "F("+this.originNameInput.get()+","+this.destinationNameInput.get()+")"; break;
		case MIGRATION:
			s = "G("+this.originNameInput.get()+","+this.destinationNameInput.get()+")"; break;
		case DEATH:
			s = "D("+this.originNameInput.get()+")"; break;
		default: // non-deme
			s = this.originNameInput.get();
		}
		return s;
	}

	
}
