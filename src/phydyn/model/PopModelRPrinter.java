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
import phydyn.model.parser.PopModelParser.VectorExprContext;
import phydyn.util.General;

public class PopModelRPrinter extends PopModelBaseVisitor<String> {
	PopModelODE popModel;
	Map<String, String> reserved;
	String[] paramNames;
	List<String> defNames;
	List<DefinitionObj> definitions;
	
	public PopModelRPrinter(PopModelODE popModel) {
		super();
		this.popModel = popModel;
		defNames = popModel.defNames;
		definitions = popModel.definitions;
		paramNames = popModel.modelParams.paramNames;
		// load reserved variables map
		reserved = new HashMap<String,String>();
		// this should be linked to 
		reserved.put("PI", "pi");
		// reserved.put("E", "exp(1)");
		reserved.put("T", "t");
		reserved.put("T0", "t0t1$t0");
		reserved.put("T1", "t0t1$t1" );
	}
	@Override
	public String visitStm(StmContext ctx) {
		String id = ctx.IDENT().getText();
		String rhs = visit(ctx.expr());
		return id+" = "+rhs+";";
	}

	@Override
	public String visitEquation(EquationContext ctx) {
		/* so far, an equation is just an expression */
		return visit(ctx.expr());
	}
	
	@Override
	public String visitMinusExpr(MinusExprContext ctx) {
		return "-"+ visit(ctx.expr());
	}
	
	@Override 
	public String visitNotExpr(NotExprContext ctx) {
		return "! "+visit(ctx.expr());
	}
	
	@Override
	public String visitVectorExpr(VectorExprContext ctx) {
		System.out.println("WARNING: vectors not tested");
		String id = ctx.IDENT().getText();
		String index = visit(ctx.expr());
		return id+"["+index+"]";
		
	}

	@Override
	public String visitIdentExpr(IdentExprContext ctx) {
		String id =  ctx.IDENT().getText();
		// reserved words
		if (reserved.containsKey(id)) {
			return reserved.get(id);
		}
		
		
		if (General.indexOf(id,  paramNames)!=-1) {
			return "parms$"+id;
		} else if (defNames.contains(id)) {
			/* PopModelODE checks that definitions are not circular */
			final int idx = defNames.indexOf(id);
			final DefinitionObj def = definitions.get(idx);
			final String rhs = this.visit(def.stm.expr());
			return "("+rhs+")";
		}	
		return id;
	}

	@Override
	public String visitFloatExpr(FloatExprContext ctx) {
		return ctx.FLOAT().getText();
	}

	@Override
	public String visitIntExpr(IntExprContext ctx) {
		return ctx.INT().getText();
	}

	@Override
	public String visitPowerExpr(PowerExprContext ctx) {
		String base,exponent;
		base = visit(ctx.expr(0));
		exponent = visit(ctx.expr(1));
		return base+"^"+exponent;
	}

	@Override
	public String visitSumExpr(SumExprContext ctx) {
		String left = visit(ctx.expr(0));
		String right = visit(ctx.expr(1));
		// ctx.op.getText() expecting ADD|SUB -> +|-
		return left+ctx.op.getText()+right;
	}

	@Override
	public String visitProdExpr(ProdExprContext ctx) {
		String left = visit(ctx.expr(0));
		String right = visit(ctx.expr(1));
		// ctx.op.getText() expecting MUL|DIV -> *|/
		return left+ctx.op.getText()+right;
	}
	
	@Override public String visitCmpExpr(CmpExprContext ctx) { 
		String left = visit(ctx.expr(0));
		String right = visit(ctx.expr(1));
		// ctx.op.getText() expecting GT|LT|GEQ|LEQ|EQ
		String cmpOp;
		switch(ctx.op.getType()) {
		case PopModelParser.GT:
			cmpOp = ">"; break;
		case PopModelParser.LT:
			cmpOp = "<"; break;
		case PopModelParser.GEQ:
			cmpOp = ">="; break;
		case PopModelParser.LEQ:
			cmpOp = "<="; break;		
		case PopModelParser.EQ:
			cmpOp = "=="; break;
		default:
			throw new IllegalArgumentException("Programming Error - comparison operator not implemented: "
												+ctx.op.getText());
		}
		return "("+left+cmpOp+right+")";
	}
	
	@Override public String visitBoolExpr(BoolExprContext ctx) { 
		String left = visit(ctx.expr(0));
		String right = visit(ctx.expr(1));
		String boolOp;
		switch(ctx.op.getType()) {
		case PopModelParser.AND:
			boolOp = " && "; break;
		case PopModelParser.OR:
			boolOp = " || "; break;	
		default:
			throw new IllegalArgumentException("Programming Error - boolean operator not implemented: "
												+ctx.op.getText());	
		}		
		return "("+left+boolOp+right+")";
	}

	@Override 
	public String visitCondExpr(CondExprContext ctx) { 
		String condexp = visit(ctx.expr(0));
		String thenexp = visit(ctx.expr(1));
		String elseexp = visit(ctx.expr(2));
		return "(if ("+condexp+")  ("+thenexp+") else ("+elseexp+"))";
	}
	
	@Override
	public String visitParenthExpr(ParenthExprContext ctx) {
		return "("+visit(ctx.expr())+")";
	}

	@Override
	public String visitCallSpecialExpr(CallSpecialExprContext ctx) {
		String fname;
		switch (ctx.op.getType()) {
		case PopModelParser.EXP:
			fname = "exp"; break;
		case PopModelParser.LOG:
			fname = "log"; break;
		case PopModelParser.SQRT:
			fname = "sqrt"; break;
		case PopModelParser.COS:
			fname = "cos"; break;
		case PopModelParser.SIN:
			fname = "sin"; break;
		case PopModelParser.ABS:
			fname = "abs"; break;
		case PopModelParser.FLOOR:
			fname = "floor"; break;
		case PopModelParser.CEIL:
			fname = "ceiling"; break;
		default:
			throw new IllegalArgumentException("Programming Error - special function not implemented: "
												+ctx.op.getText());	
		}
				
		return fname+"("+visit(ctx.expr())+")";
	}
	
	
	@Override
	public String visitCallBinaryExpr(CallBinaryExprContext ctx) {
		String val1 = visit(ctx.expr(0));
		String val2 = visit(ctx.expr(1));
		String fname;
		switch (ctx.op.getType()) {
		case PopModelParser.MIN:
			fname = "min"; break;
		case PopModelParser.MAX:
			fname = "max"; break;
		case PopModelParser.MOD:
			return "("+val1 + " %% "+val2+")";
		default:
			throw new IllegalArgumentException("Programming Error - special binary function not implemented: "
												+ctx.op.getText());	
		}
		
		return fname+"("+val1 + ","+val2+")";
	}
	

	@Override
	public String visit(ParseTree tree) {
		// TODO Auto-generated method stub
		return super.visit(tree);
	}



}
