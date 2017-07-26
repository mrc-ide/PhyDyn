package phydyn.model;

import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jblas.DoubleMatrix;

import beast.core.BEASTObject;
import beast.core.Input;
import beast.core.Input.Validate;
import phydyn.model.parser.PopModelLexer;
import phydyn.model.parser.PopModelParser;

enum EquationType { BIRTH, DEATH, MIGRATION, NONDEME };

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
	public ParseTree tree;
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
		ANTLRInputStream input = new ANTLRInputStream(equationStringInput.get());
		try {
			PopModelLexer lexer = new PopModelBailLexer(input); 
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			PopModelParser parser = new PopModelParser(tokens);
			parser.setErrorHandler(new PopModelParserErrorStrategy());
			tree = parser.expr();
		} catch (Exception e) {
			throw new IllegalArgumentException("Error while parsing equation"+equationStringInput.get());
		}
		return;
	}

	public void completeValidation(PopModelODE model) {
		if (type==EquationType.NONDEME) {
			row = model.indexOf(model.nonDemeNames, originNameInput.get() );
		} else {
			row = model.indexOf( model.demeNames, originNameInput.get());
		}
		
		// rows (origin) must be correct since they name lists were collected from originName
		// if (row == -1) {
		//	throw new IllegalArgumentException("Unknown origin name in matrix eq: "+originNameInput.get());
		//}
		
		if (destinationNameInput.get() != null) {
			column = model.indexOf( model.demeNames, destinationNameInput.get());
			if (column==-1) {
				throw new IllegalArgumentException("Unknown destination name in matrix eq: "+destinationNameInput.get());
			}
		}
		if ((type==EquationType.BIRTH) && (row != column)) { 
			model.setDiagF(false);
		}
		// additional type checking would be ideal
		
	}
	
	public int compile(PopModelCompiler compiler) {
		code = compiler.compile(tree);
		return code.maxStackSize;
	}
	
	

	
}
