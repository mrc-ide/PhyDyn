package phydyn.model;

import org.antlr.v4.runtime.tree.ParseTree;

import phydyn.model.parser.PopModelBaseVisitor;
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

/**
 * Evaluates expressions found in ODEs and variable definitions used by the model
 * Basic Colgem Interpreter implemented as a tree visitor.
 * Expressions evaluate to a double 
 *
 * @author Igor Siveroni
 */

/* Basic Interpreter of PhyDyn expressions implemented as a tree visitor */
public class PopModelODEPrinter extends PopModelBaseVisitor<String> {
	
	public PopModelODEPrinter() {
		super();
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
		return "not "+visit(ctx.expr());
	}
	
	@Override
	public String visitVectorExpr(VectorExprContext ctx) {
		String id = ctx.IDENT().getText();
		String index = visit(ctx.expr());
		return id+"["+index+"]";
		
	}

	@Override
	public String visitIdentExpr(IdentExprContext ctx) {
		return ctx.IDENT().getText();
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
		return left+ctx.op.getText()+right;
	}

	@Override
	public String visitProdExpr(ProdExprContext ctx) {
		String left = visit(ctx.expr(0));
		String right = visit(ctx.expr(1));
		return left+ctx.op.getText()+right;
	}
	
	@Override public String visitCmpExpr(CmpExprContext ctx) { 
		String left = visit(ctx.expr(0));
		String right = visit(ctx.expr(1));
		return "("+left+ctx.op.getText()+right+")";
	}
	
	@Override public String visitBoolExpr(BoolExprContext ctx) { 
		String left = visit(ctx.expr(0));
		String right = visit(ctx.expr(1));
		return "("+left+ctx.op.getText()+right+")";
	}

	@Override 
	public String visitCondExpr(CondExprContext ctx) { 
		String condexp = visit(ctx.expr(0));
		String thenexp = visit(ctx.expr(1));
		String elseexp = visit(ctx.expr(2));
		return "(if "+condexp+" then ("+thenexp+") else ("+elseexp+"))";
	}
	
	@Override
	public String visitParenthExpr(ParenthExprContext ctx) {
		return "("+visit(ctx.expr())+")";
	}

	@Override
	public String visitCallSpecialExpr(CallSpecialExprContext ctx) {
		return ctx.op.getText()+"("+visit(ctx.expr())+")";
	}
	
	
	@Override
	public String visitCallBinaryExpr(CallBinaryExprContext ctx) {
		String val1 = visit(ctx.expr(0));
		String val2 = visit(ctx.expr(1));
		return val1 + ctx.op.getText()+val2;
	}
	

	@Override
	public String visit(ParseTree tree) {
		// TODO Auto-generated method stub
		return super.visit(tree);
	}


	
	

}
