package phydyn.model;

import beast.base.core.Description;
import org.antlr.v4.runtime.tree.ParseTree;
import phydyn.model.parser.PopModelBaseVisitor;
import phydyn.model.parser.PopModelParser;
import phydyn.model.parser.PopModelParser.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Description("Compiles a PopModel expression parse tree into a sequence of equivalent PMStackMachine instructions."
		+ "Performs basic type checking and calculates the addresses (indexes/offsets) of all declated variables")
public class PopModelCompiler extends PopModelBaseVisitor<PMMachineCode> {
	// Keeps track of indices in environment array
	private final Map<String, Integer> envIndex;
	private int nextIndex;
	private PMMachineCode code;
	
	PopModelCompiler() {
		super();
		envIndex = new HashMap<>();
		nextIndex = 0;
	}
	
	/* Enters new variables to the environment - 
	 * Copies their corresponding indices into the second argument */
	void updateEnv(List<String> names, int[] indices) {
		int i=0;
		for(String name: names) {
			if (envIndex.containsKey(name)) {
				indices[i] = (int) envIndex.get(name);
			} else {			
				envIndex.put(name, nextIndex);
				indices[i] = nextIndex;
				nextIndex++;
			}
			i++;
		}
	}
	
	void updateEnv(String[] names, int[] indices) {
		int i=0;
		for(String name: names) {
			if (envIndex.containsKey(name)) {
				indices[i] = (int) envIndex.get(name);
			} else {
				envIndex.put(name, nextIndex);
				indices[i] = nextIndex;
				nextIndex++;
			}
			i++;
		}
	}
	
	/* Enters new for vector variables to the environment
	* Copies their corresponding indices into the second argument */
	void updateEnv(String[] names, double[][] vectors, int[] indices) {
		int i=0;
		for(String name: names) {
			if (envIndex.containsKey(name)) {
				indices[i] = (int) envIndex.get(name);
			} else {				
				envIndex.put(name, nextIndex);
				indices[i] = nextIndex;
				nextIndex += vectors[i].length;
			}
			i++;
		}
	}
	
	
	int updateEnv(String name) {
		int idx;
		if (envIndex.containsKey(name)) {
			idx = (int) envIndex.get(name);
		} else {
			idx = nextIndex;
			envIndex.put(name, idx);
			nextIndex++;
		}
		return idx;
	}
	
	
	public int getAddress(String name) {
		return (int) envIndex.get(name);
	}
	
	public int getEnvSize() {
		// return envIndex.size(); // no longer true
		return nextIndex;
	}
	
	public PMMachineCode compile(ParseTree ast) {
		code = new PMMachineCode();
		visit(ast);
		code.endCodeGeneration();
		return code;
	}
	
	/* Compiler is implemented as  a visitor */
	/* PopModelBaseVisitor interface */
	@Override
	public PMMachineCode visitStm(StmContext ctx) {
		/* id = expr */	
		String id = ctx.IDENT().getText();
		// System.out.println("Compiling definition:"+id);
		visit(ctx.expr());  // rhs is in top of the stack
		code.generateWriteVar(envIndex.get(id));
		return code;
	}
	
	@Override
	public PMMachineCode visitEquation(EquationContext ctx) {
		/* so far, an equation is just an expression */
		visit(ctx.expr()); // generate code that evaluates expression
		return code;
	}
	
	@Override
	public PMMachineCode visitMinusExpr(MinusExprContext ctx) {
		visit(ctx.expr());
		code.generateUnaryOp(PMStackMachine.OP_NEG);
		return code;
	}

	@Override
	public PMMachineCode visitNotExpr(NotExprContext ctx) {
		visit(ctx.expr());
		code.generateUnaryOp(PMStackMachine.OP_NOT);
		return code;
	}
	
	@Override
	public PMMachineCode visitIdentExpr(IdentExprContext ctx) {
		String id = ctx.IDENT().getText();
		if (envIndex.containsKey(id)) {
			code.generatePushVar(envIndex.get(id));
		} else { // Shouldn't be necessary - check Semantic checker
			throw new IllegalArgumentException("Compiler: Variable " + id
	            + " unkown");
		}
		return code;
	}
	
	@Override
	public PMMachineCode visitVectorExpr(VectorExprContext ctx) {
		String id = ctx.IDENT().getText();
		visit(ctx.expr()); // calculate ofsset
		if (envIndex.containsKey(id)) {
			code.generatePushVarOffset(envIndex.get(id));
		} else { // Shouldn't be necessary - check Semantic checker
			throw new IllegalArgumentException("Compiler: Vector " + id
	            + " unkown");
		}
		return code;		
	}
	
	
	@Override
	public PMMachineCode visitFloatExpr(FloatExprContext ctx) {
		code.generatePushConstant(Double.valueOf(ctx.FLOAT().getText()));
		return code;
	}
	
	@Override
	public PMMachineCode visitIntExpr(IntExprContext ctx) {
		code.generatePushConstant(Double.valueOf(ctx.INT().getText())); /* convert to double */
		return code;
	}
	
	@Override
	public PMMachineCode visitPowerExpr(PowerExprContext ctx) {
		visit(ctx.expr(0)); // generate code for base
		visit(ctx.expr(1)); // generate code for exponent
		code.generateBinaryInstruction(PMStackMachine.IARITHMETIC_OP, PMStackMachine.OP_POW);
		return code;
	}
	
	@Override
	public PMMachineCode visitSumExpr(SumExprContext ctx) {
		visit(ctx.expr(0));  // lhs expression
		visit(ctx.expr(1));  // rhs expression (top of stack)
		int op;
		if (ctx.op.getType() == PopModelParser.ADD) {
			op = PMStackMachine.OP_ADD;
		} else {
			op = PMStackMachine.OP_SUB;
		}
		code.generateBinaryInstruction(PMStackMachine.IARITHMETIC_OP, op);
		return code;
	}
	
	@Override
	public PMMachineCode visitProdExpr(ProdExprContext ctx) {
		visit(ctx.expr(0)); // lhs
		visit(ctx.expr(1)); // rhs
		int op;
		if (ctx.op.getType() == PopModelParser.MUL) {
			op = PMStackMachine.OP_MUL;
		} else {
			op = PMStackMachine.OP_DIV;
		}
		code.generateBinaryInstruction(PMStackMachine.IARITHMETIC_OP, op);
		return code;
	}
	
	
	@Override
	public PMMachineCode visitCmpExpr(CmpExprContext ctx) {
		visit(ctx.expr(0)); // lhs
		visit(ctx.expr(1)); // rhs
		int op;
		switch(ctx.op.getType()) {
		case PopModelParser.GT:
			op = PMStackMachine.OP_GT; break;
		case PopModelParser.GEQ:
			op = PMStackMachine.OP_GEQ; break;
		case PopModelParser.LT:
			op = PMStackMachine.OP_LT; break;
		case PopModelParser.LEQ:
			op = PMStackMachine.OP_LEQ; break;
		case PopModelParser.EQ:
			op = PMStackMachine.OP_EQ; break;
		default: 
			throw new RuntimeException("Bug: Invalid Cmp operator");
		}
		code.generateBinaryInstruction(PMStackMachine.ICMP_OP,op);
		return code;
	}
	
	@Override
	public PMMachineCode visitBoolExpr(BoolExprContext ctx) {
		visit(ctx.expr(0)); // lhs
		visit(ctx.expr(1)); // rhs
		int op;
		if (ctx.op.getType() == PopModelParser.AND) {
			op = PMStackMachine.OP_AND;
		} else {
			op = PMStackMachine.OP_OR;
		}
		code.generateBinaryInstruction(PMStackMachine.IBOOL_OP, op);
		return code;
	}
	
	
	@Override
	public PMMachineCode visitCondExpr(CondExprContext ctx) {
		visit(ctx.expr(0)); // cond
		int pcJmp1 = code.getNextPC(); 
		code.generateJumpConditional(0, false);
		visit(ctx.expr(1)); //true-exp
		int pcJmp2  = code.getNextPC();
		code.generateJump(0);
		int pcFalse = code.getNextPC();
		visit(ctx.expr(2)); //false-exp
		// update jump targets (second location after Jump instruction)
		code.updateInstruction(pcJmp1+1,pcFalse);
		code.updateInstruction(pcJmp2+1, code.getNextPC());
		return code;
	}
	
	@Override
	public PMMachineCode visitParenthExpr(ParenthExprContext ctx) {
		visit(ctx.expr());
		return code;
	}
	
	@Override
	public PMMachineCode visitCallSpecialExpr(CallSpecialExprContext ctx) {
		int fun;
		visit(ctx.expr());  // generate code for argument
		switch (ctx.op.getType()) {
		case PopModelParser.EXP:
			fun = PMStackMachine.FUN_EXP; break;
		case PopModelParser.LOG:
			fun = PMStackMachine.FUN_LOG; break;
		case PopModelParser.SQRT:
			fun = PMStackMachine.FUN_SQRT; break;
		case PopModelParser.COS:
			fun = PMStackMachine.FUN_COS; break;
		case PopModelParser.SIN:
			fun = PMStackMachine.FUN_SIN; break;
		case PopModelParser.ABS:
			fun = PMStackMachine.FUN_ABS; break;
		case PopModelParser.FLOOR:
			fun = PMStackMachine.FUN_FLOOR; break;
		case PopModelParser.CEIL:
			fun = PMStackMachine.FUN_CEIL; break;
		default:
			/* This is an implementation error */
			throw new IllegalArgumentException("Function " + ctx.op.getText()
		            + " unkown");
		}
		code.generateSpecialCall(fun);
		return code;
	}
	
	
	@Override
	public PMMachineCode visitCallBinaryExpr(CallBinaryExprContext ctx) {
		int fun;
		visit(ctx.expr(0)); // first argument to stack
		visit(ctx.expr(1)); // second argument to stack
		switch (ctx.op.getType()) {
		case PopModelParser.MIN:
			fun = PMStackMachine.FUN_MIN; break;
		case PopModelParser.MAX:
			fun = PMStackMachine.FUN_MAX; break;
		case PopModelParser.MOD:
			fun = PMStackMachine.FUN_MOD; break;
		
		default:
			/* This is an implementation error */
			throw new IllegalArgumentException("Function " + ctx.op.getText()
		            + " unkown");
		}
		code.generateBinaryCall(fun);
		return code;
	}
	
	
	@Override
	public PMMachineCode visit(ParseTree tree) {
		super.visit(tree);
		return code;
	}
	

}
