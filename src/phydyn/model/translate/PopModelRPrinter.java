package phydyn.model.translate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.tree.ParseTree;

import phydyn.model.DefinitionObj;
import phydyn.model.PopModelODE;
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

enum RTarget { PHYDYNR, ODIN };

public class PopModelRPrinter extends PopModelBaseVisitor<String> {
	PopModelODE popModel;
	Map<String, String> reserved;
	String[] paramNames;
	List<String> defNames;
	List<DefinitionObj> definitions;
	RTarget target;
	
	static PopModelRPrinter create(PopModelODE model, RTarget target) {
		PopModelRPrinter printer = new PopModelRPrinter(model);
		// check valid target
		
		printer.target = target;
		HashMap<String,String> reserved = new HashMap<String,String>();
		// load reserved variables map
		reserved.put("PI", "pi");
		reserved.put("T", "t");
		if (target==RTarget.PHYDYNR) {
			reserved.put("T0", "parms$t0");
			reserved.put("T1", "parms$t1" );
		} else {  // ODIN
			reserved.put("T0", "T0");
			reserved.put("T1", "T1" );
		}
		printer.reserved = reserved;
		return printer;
	}
	
	private PopModelRPrinter(PopModelODE popModel) {
		super();
		this.popModel = popModel;
		defNames = popModel.defNames;
		definitions = popModel.definitions;
		paramNames = popModel.modelParams.paramNames;	
	}
	
 	
	@Override
	public String visitStm(StmContext ctx) {
		String id = ctx.IDENT().getText();
		String rhs = visit(ctx.expr());
		return id+" <- "+rhs ;
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
		
		if (target == RTarget.ODIN) {
			return id;
		} else if (target == RTarget.PHYDYNR) {
			if (General.indexOf(id,  paramNames)!=-1) {
				return "parms$"+id;
			} else if (defNames.contains(id)) {
				/* PopModelODE checks that definitions are not circular 
				 * definitions are inserted in the expression - like a macro */
				final int idx = defNames.indexOf(id);
				final DefinitionObj def = definitions.get(idx);
				final String rhs = this.visit(def.stm.expr());
				if (checkParenthesis(rhs))
					return rhs;
				else
					return "("+rhs+")";
			}	
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
		return "(if "+condexp+" ("+thenexp+") else ("+elseexp+"))";
	}
	
	@Override
	public String visitParenthExpr(ParenthExprContext ctx) {
		String s = visit(ctx.expr());
		if (checkParenthesis(s.trim()))
			return s;
		else
			return "("+s+")";
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

	// return true if s = (xxxx) where xxx has balanced parentheses
	static boolean checkParenthesis(String s) {
		if (s.charAt(0) != '(') return false;
		int i, open = 1;
		for(i=1; i < s.length(); i++) {
			final char c = s.charAt(i);
			if (c == '(') open++;
			if (c == ')') open--;
			if (open<1) break;
		}
		if ((open==0) && (i == s.length()-1))
			return true;
		return false;
	}


}
