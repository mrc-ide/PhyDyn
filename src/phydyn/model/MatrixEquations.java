package phydyn.model;


import java.util.ArrayList;
import java.util.List;

//import org.antlr.v4.runtime.ANTLRInputStream;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import beast.core.BEASTObject;
import beast.core.Input;
import beast.core.Input.Validate;
import phydyn.model.parser.PopModelLexer;
import phydyn.model.parser.PopModelParser;
import phydyn.model.parser.PopModelParser.BirthEquationContext;
import phydyn.model.parser.PopModelParser.DeathEquationContext;
import phydyn.model.parser.PopModelParser.ExprContext;
import phydyn.model.parser.PopModelParser.MatrixEquationContext;
import phydyn.model.parser.PopModelParser.MatrixEquationsContext;
import phydyn.model.parser.PopModelParser.MigrationEquationContext;
import phydyn.model.parser.PopModelParser.NondemeEquationContext;

/*
 * PhyDyn Matrix equations with XML input
 */
		

public class MatrixEquations extends BEASTObject {
	

	public Input<String> equationsStringInput = new Input<>(
            "value",
            "String description of matrix equation", Validate.REQUIRED);
	
	
	public int row,column;
	
	protected MatrixEquationsContext eqsCtx;
	
	// public ParseTree tree;
	public PMMachineCode code;
	
	public MatrixEquations() { }
	
	public MatrixEquations(String eqs) {
		equationsStringInput.setValue(eqs, this);
		initAndValidate();
	}

	@Override
	public void initAndValidate() {
		/* parse equation string */
		CodePointCharStream  input = CharStreams.fromString( equationsStringInput.get()  );
		//ANTLRInputStream input = new ANTLRInputStream(equationsStringInput.get());
		try {
			PopModelLexer lexer = new PopModelBailLexer(input); 
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			PopModelParser parser = new PopModelParser(tokens);
			parser.setErrorHandler(new PopModelParserErrorStrategy());
			eqsCtx = parser.matrixEquations();
		} catch (Exception e) {
			System.out.println( "Error while parsing equations: "+equationsStringInput.get());

			throw new IllegalArgumentException("Parsing error");
		}
		return;
	}
	
	public List<MatrixEquationObj> createMatrixEquations() {
		List<MatrixEquationContext> eqCtxs = eqsCtx.matrixEquation();
		List<MatrixEquationObj> eqs = new ArrayList<>();
		MatrixEquationObj eq;
		for (MatrixEquationContext eqCtx: eqCtxs ) {
			eq = null;
			if (eqCtx instanceof BirthEquationContext)
				eq = createMatrixEquation((BirthEquationContext) eqCtx );
			else if (eqCtx instanceof MigrationEquationContext)
				eq = createMatrixEquation((MigrationEquationContext) eqCtx );
			else if (eqCtx instanceof DeathEquationContext)
				eq = createMatrixEquation((DeathEquationContext) eqCtx );
			else if (eqCtx instanceof NondemeEquationContext)
				eq = createMatrixEquation((NondemeEquationContext) eqCtx );
			else 
				throw new IllegalArgumentException("Programmer error: Invalid matrix equation type - programming error");
			eqs.add(eq);
		}
		return eqs;
		

	}
	
	static public List<MatrixEquationObj> createMatrixEquations(String eqsString) {
		MatrixEquationsContext eqsCtx;
		/* parse equation string */
		CodePointCharStream  input = CharStreams.fromString( eqsString  );
		//ANTLRInputStream input = new ANTLRInputStream(equationsStringInput.get());
		try {
			PopModelLexer lexer = new PopModelBailLexer(input); 
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			PopModelParser parser = new PopModelParser(tokens);
			parser.setErrorHandler(new PopModelParserErrorStrategy());
			eqsCtx = parser.matrixEquations();
		} catch (Exception e) {
			System.out.println( "Error while parsing equations: "+eqsString);

			throw new IllegalArgumentException("Parsing error");
		}
				
		List<MatrixEquationContext> eqCtxs = eqsCtx.matrixEquation();
		List<MatrixEquationObj> eqs = new ArrayList<>();
		MatrixEquationObj eq;
		for (MatrixEquationContext eqCtx: eqCtxs ) {
			eq = null;
			if (eqCtx instanceof BirthEquationContext)
				eq = createMatrixEquation((BirthEquationContext) eqCtx );
			else if (eqCtx instanceof MigrationEquationContext)
				eq = createMatrixEquation((MigrationEquationContext) eqCtx );
			else if (eqCtx instanceof DeathEquationContext)
				eq = createMatrixEquation((DeathEquationContext) eqCtx );
			else if (eqCtx instanceof NondemeEquationContext)
				eq = createMatrixEquation((NondemeEquationContext) eqCtx );
			else 
				throw new IllegalArgumentException("Programmer error: Invalid matrix equation type - programming error");
			eqs.add(eq);
		}
		return eqs;
		
	}
	
	
	static MatrixEquationObj createMatrixEquation(BirthEquationContext e) {
		return new MatrixEquationObj(EquationType.BIRTH, e.IDENT(0).getText(), 
					e.IDENT(1).getText(), e.expr() );
	}
	
	static MatrixEquationObj createMatrixEquation(MigrationEquationContext e) {
		return new MatrixEquationObj(EquationType.MIGRATION, e.IDENT(0).getText(), 
				e.IDENT(1).getText(), e.expr() );
	}
	
	
	static MatrixEquationObj createMatrixEquation(DeathEquationContext e) {
		return new MatrixEquationObj(EquationType.DEATH, e.IDENT().getText(), e.expr() );
	}
	
	static MatrixEquationObj createMatrixEquation(NondemeEquationContext e) {
		return new MatrixEquationObj(EquationType.NONDEME, e.IDENT().getText(), e.expr() );
	}
	
	
	
	
}
