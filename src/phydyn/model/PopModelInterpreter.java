package phydyn.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.tree.ParseTree;

import phydyn.model.parser.PopModelBaseVisitor;
import phydyn.model.parser.PopModelParser;
import phydyn.model.parser.PopModelParser.BoolExprContext;
import phydyn.model.parser.PopModelParser.CallBinaryExprContext;
import phydyn.model.parser.PopModelParser.CallSpecialExprContext;
import phydyn.model.parser.PopModelParser.CmpExprContext;
import phydyn.model.parser.PopModelParser.CondExprContext;
import phydyn.model.parser.PopModelParser.EquationContext;
import phydyn.model.parser.PopModelParser.FloatExprContext;
import phydyn.model.parser.PopModelParser.IdentExprContext;
import phydyn.model.parser.PopModelParser.IntExprContext;
import phydyn.model.parser.PopModelParser.MinusExprContext;
import phydyn.model.parser.PopModelParser.NotExprContext;
import phydyn.model.parser.PopModelParser.ParenthExprContext;
import phydyn.model.parser.PopModelParser.PowerExprContext;
import phydyn.model.parser.PopModelParser.ProdExprContext;
import phydyn.model.parser.PopModelParser.StmContext;
import phydyn.model.parser.PopModelParser.SumExprContext;

/**
 * Evaluates expressions found in ODEs and variable definitions used by the model
 * Basic Colgem Interpreter implemented as a tree visitor.
 * Expressions evaluate to a double 
 *
 * @author Igor Siveroni
 */

/* Basic Interpreter of PhyDin expressions implemented as a tree visitor */
public class PopModelInterpreter extends PopModelBaseVisitor<Double> {

	private final Map<String, Double> env;
	
	public PopModelInterpreter(SemanticChecker checker) {
		super();
		env = new HashMap<>();
		// Load initial environments with system constants
		for (Map.Entry<String, Double> entry : checker.usedConstants.entrySet())
        {
            env.put(entry.getKey(), entry.getValue());
        }
	}

	public void clear_env() {
		env.clear();
	}
	
	public void updateEnv(List<String> varNames, double[] values) {
		int i=0;
		for(String name: varNames) {
			env.put(name, values[i++]);
		}		
	}
	
	public void updateEnv(String[] varNames, double[] values) {
		int i=0;
		for(String name: varNames) {
			env.put(name, values[i++]);
		}		
	}
	
	public void updateEnv(String varName, double value) {
		env.put(varName, value);	
	}
	public void printEnv() {
		System.out.print("[");
		for (Map.Entry<String, Double> entry : env.entrySet()) {
		    String key = entry.getKey();
		    Double value = entry.getValue();
		    System.out.print("("+key+":"+value+")");
		}
		System.out.println("]");
	}
	
	public double evaluate(ParseTree exprTree) {
		double val = visit(exprTree);
		return val;
	}
	
	@Override
	public Double visitStm(StmContext ctx) {
		String id = ctx.IDENT().getText();
		double val = visit(ctx.expr());
		env.put(id,val);
		return val;
	}

	@Override
	public Double visitEquation(EquationContext ctx) {
		/* so far, an equation is just an expression */
		double val = visit(ctx.expr());
		return val;
		// return super.visitEquation(ctx);
	}
	
	@Override
	public Double visitMinusExpr(MinusExprContext ctx) {
		double val = visit(ctx.expr());
		return -val;
	}
	
	@Override 
	public Double visitNotExpr(NotExprContext ctx) {
		double val = visit(ctx.expr());
		return (val == 0.0) ? 1.0 : 0.0; 
	}

	@Override
	public Double visitIdentExpr(IdentExprContext ctx) {
		String id = ctx.IDENT().getText();
		if (env.containsKey(id)) return env.get(id);
		throw new IllegalArgumentException("Variable " + id
	            + " unkown");

	}

	@Override
	public Double visitFloatExpr(FloatExprContext ctx) {
		return Double.valueOf(ctx.FLOAT().getText());
	}

	@Override
	public Double visitIntExpr(IntExprContext ctx) {
		return Double.valueOf(ctx.INT().getText()); /* convert to double */
	}

	@Override
	public Double visitPowerExpr(PowerExprContext ctx) {
		Double base,exponent;
		base = visit(ctx.expr(0));
		exponent = visit(ctx.expr(1));
		return Math.pow(base, exponent);
	}

	@Override
	public Double visitSumExpr(SumExprContext ctx) {
		double left = visit(ctx.expr(0));
		double right = visit(ctx.expr(1));
		if (ctx.op.getType() == PopModelParser.ADD) return left+right;
		return left-right;
	}

	@Override
	public Double visitProdExpr(ProdExprContext ctx) {
		double left = visit(ctx.expr(0));
		double right = visit(ctx.expr(1));
		if (ctx.op.getType() == PopModelParser.MUL) return left*right;
		return left/right;
	}
	
	@Override public Double visitCmpExpr(CmpExprContext ctx) { 
		double left = visit(ctx.expr(0));
		double right = visit(ctx.expr(1));
		boolean result = true;
		switch(ctx.op.getType()) {
		case PopModelParser.GT:
			result = (left>right); break;
		case PopModelParser.LT:
			result = (left<right); break;
		case PopModelParser.GEQ:
			result = (left>=right); break;
		case PopModelParser.LEQ:
			result = (left<=right); break;		
		case PopModelParser.EQ:
			result = (left==right); break;
		}
		return (result) ? 1.0 : 0.0;
	}
	
	@Override public Double visitBoolExpr(BoolExprContext ctx) { 
		double left = visit(ctx.expr(0));
		double right = visit(ctx.expr(1));
		boolean leftb = (left==0) ? false : true;
		boolean rightb = (right==0) ? false : true;
		boolean result = true;
		switch(ctx.op.getType()) {
		case PopModelParser.AND:
			result = (leftb && rightb); break;
		case PopModelParser.OR:
			result = (leftb && rightb); break;				
		}
		return (result) ? 1.0 : 0.0;
	}

	@Override 
	public Double visitCondExpr(CondExprContext ctx) { 
		double condExpr = visit(ctx.expr(0));
		double result;
		if (condExpr == 0)
			result = visit(ctx.expr(2));
		else
			result = visit(ctx.expr(1));
		return result; 
		
	}
	
	@Override
	public Double visitParenthExpr(ParenthExprContext ctx) {
		return visit(ctx.expr());
	}

	@Override
	public Double visitCallSpecialExpr(CallSpecialExprContext ctx) {
		double val = visit(ctx.expr());
		switch (ctx.op.getType()) {
		case PopModelParser.EXP:
			return Math.exp(val);
		case PopModelParser.LOG:
			return Math.log(val);
		case PopModelParser.SQRT:
			return Math.pow(val,0.5);
		case PopModelParser.COS:
			return Math.cos(val);
		case PopModelParser.SIN:
			return Math.sin(val);
		case PopModelParser.ABS:
			return Math.abs(val);
		case PopModelParser.FLOOR:
			return Math.floor(val);
		case PopModelParser.CEIL:
			return Math.ceil(val);
		}
		/* This is an implementation error */
		throw new IllegalArgumentException("Function " + ctx.op.getText()
	            + " unkown");
	}
	
	
	@Override
	public Double visitCallBinaryExpr(CallBinaryExprContext ctx) {
		double val1 = visit(ctx.expr(0));
		double val2 = visit(ctx.expr(1));
		switch (ctx.op.getType()) {
		case PopModelParser.MIN:
			return (val1 <= val2)? val1 : val2;
		case PopModelParser.MAX:
			return (val1 <= val2)? val2 : val1;
		case PopModelParser.MOD:
			return val1 % val2;
		
		}
		/* This is an implementation error */
		throw new IllegalArgumentException("Function " + ctx.op.getText()
	            + " unkown");
	}
	

	@Override
	public Double visit(ParseTree tree) {
		// TODO Auto-generated method stub
		return super.visit(tree);
	}


	
	

}
