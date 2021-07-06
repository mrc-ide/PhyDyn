package phydyn.model;

import java.util.HashMap;
import java.util.Map;

import phydyn.model.parser.PopModelBaseVisitor;
import phydyn.model.parser.PopModelParser.ExprContext;
import phydyn.model.parser.PopModelParser.IdentExprContext;
import phydyn.model.parser.PopModelParser.ProdExprContext;
import phydyn.model.parser.PopModelParser.StmContext;
import phydyn.model.parser.PopModelParser.SumExprContext;

/**
 * Class that performs basic syntactic checks on the equation arithmetic expressions:
 * All variables must be defined: 
*  population names, time variables, literals, rates and user-defined variables.
 *
 * @author Igor Siveroni
 *
 */
	

public class SemanticChecker extends PopModelBaseVisitor<Boolean> {
	public static final String PI="PI", T="t", T0="T0", T1="T1"; // removed E="E"
	public static final Map<String,Double> numericConstants;
	static {  // build map of literals
		numericConstants = new HashMap<String,Double>();
		numericConstants.put(PI,  Math.PI);
		//numericConstants.put(E, Math.E);
	}
	public static String[]  T0T1 = { T0, T1 };
	public static String[]  reservedIds = { T0, T1, T, PI }; // removed E
	//private static final Set<String> systemVariables = new HashSet<String>(Arrays.asList(
	//	     new String[] {T, T0,T1}));


	public Map<String, Integer> envTypes;
	public Map<String, Integer> envTypesUsed;
	public Map<String,Double> usedConstants;
	public boolean useT, useT0T1;
	boolean typeError, expOnly;
	
	public SemanticChecker() {
		super();
		envTypes = new HashMap<>();
		envTypesUsed = new HashMap<>();
		usedConstants=  new HashMap<String,Double>();
	}
	
	// Added to allow further semantic checks (Copy Constructor)
	public SemanticChecker(SemanticChecker checker) {
		//HashMap<String, Employee> shallowCopy = new HashMap<String, Employee>(originalMap);
		envTypes = new HashMap<String,Integer>(checker.envTypes);
		envTypesUsed = new HashMap<String,Integer>(checker.envTypes);
		usedConstants = new HashMap<String,Double>(checker.usedConstants);
		useT = checker.useT; 
		useT0T1 = checker.useT0T1;
	}
	
	// added to allow ad-hoc expressions e.g. new likelihood for data fitting
	public void addExternalVariable(String id) {
		envTypes.put(id, 5); // new type 5
	}
	
	public SemanticChecker(PopModelODE model) {
		super();
		envTypes = new HashMap<>();
		envTypesUsed = new HashMap<>();
		usedConstants=  new HashMap<String,Double>();
		for(DefinitionObj def: model.definitions) envTypes.put(def.name, 0);
		for(int i=0; i< model.numDemes; i++) envTypes.put(model.demeNames[i], 1);
		for(int i=0; i< model.numNonDemes; i++) envTypes.put(model.nonDemeNames[i], 2);
		for(int i=0; i< model.modelParams.numParams; i++) envTypes.put(model.modelParams.paramNames[i], 3);	
	}
	
	public boolean check(PopModelODE model) {
		int i;
		// load variables
		envTypes.clear(); 
		usedConstants.clear();
		useT0T1 = useT = false;
		//for(Definition def: model.definitions) envTypes.put(def.name, 0);
		for(i=0; i< model.numDemes; i++) envTypes.put(model.demeNames[i], 1);
		for(i=0; i< model.numNonDemes; i++) envTypes.put(model.nonDemeNames[i], 2);
		for(i=0; i< model.modelParams.numParams; i++) envTypes.put(model.modelParams.paramNames[i], 3);
		typeError = false;
		expOnly = false;
		// definitions
		// for(Definition def: model.definitions) envTypes.put(def.name, 0);
		for(DefinitionObj def: model.definitions) {
			envTypes.put(def.name, 0);
			visit(def.stm); 
		}
		if (typeError) {
			System.out.println("Error with definition specification.");
			System.out.println("Definitions are evaluated from top to bottom and must be");
			System.out.println("defined before usage.");
		}
		for(MatrixEquationObj eq: model.equations) {
			visit(eq.rhsExprCtx); 
		}
		return typeError;
		
	}
	
	// This may be used several times
	public boolean check(ExprContext exp) {		
		typeError = false;
		expOnly = true;
		visit(exp);
		return typeError;
	}
	
	@Override
	public Boolean visitStm(StmContext ctx) {
		String id = ctx.IDENT().getText();
		for(int i=0; i < reservedIds.length; i++) {
			if (id.equals(reservedIds[i])) {
				System.out.println("Illegal use of system constant/id: "+id);
				typeError = true;
				break;
			}				
		}
		return visit(ctx.expr());
	}
	
	@Override
	public Boolean visitIdentExpr(IdentExprContext ctx) {
		String id = ctx.IDENT().getText();	
		if (!envTypes.containsKey(id)) {
			if (numericConstants.containsKey(id)) {
				if (!usedConstants.containsKey(id))
					usedConstants.put(id, numericConstants.get(id));
			} else if (id.equals(T0) || id.equals(T1)) {
				useT0T1 = true;
			} else if (id.equals(T)) {
				useT = true;
			} else {
				System.out.println("Unknown identifier: "+id);
				typeError = true;
			}
		} else if (expOnly) {
			if (!envTypesUsed.containsKey(id)) {
				envTypesUsed.put(id, envTypes.get(id));  // record if id used (and type)
			}
		}
		// System.out.print(id);
		return true;
	}
	
	/*  testing parsing
	@Override
	public Boolean visitSumExpr(SumExprContext ctx) {
		System.out.print("(");
		visit(ctx.expr(0));
		System.out.print(" sum ");
		visit(ctx.expr(1));
		System.out.print(")");
		return true;
	}

	@Override
	public Boolean visitProdExpr(ProdExprContext ctx) {
		System.out.print("(");
		visit(ctx.expr(0));
		System.out.print(" prod ");
		visit(ctx.expr(1));
		System.out.print(")");
		return true;
	}
	*/
	
	
}
