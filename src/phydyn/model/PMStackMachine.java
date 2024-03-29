package phydyn.model;

import beast.base.core.Description;

@Description("Class that models a stack-based machine used to execute the code generated by the PopModelCompiler.")
/*
 * Instructions:
 * POP, PUSH_CONST i, PUSH_VAR i, BINARY_OP, SPECIAL_CALL, WRITE_VAR
 */

public class PMStackMachine {

	double[] env;
	double[] stack;
	int stack_index;
	// Constants used to denote operation and special functions
	public final static int OP_ADD=1, OP_SUB=2, OP_MUL=3, OP_DIV=4, OP_POW=5, OP_NEG=6, OP_NOT=7;
	public final static int OP_AND=8, OP_OR=9, OP_GT=10, OP_GEQ=11, OP_LT=12, OP_LEQ=13, OP_EQ=14 ;
	public final static int FUN_EXP=1, FUN_LOG=2, FUN_SQRT=3, FUN_SIN=4, FUN_COS=5;
	public final static int FUN_ABS=6, FUN_FLOOR=7, FUN_CEIL=8, FUN_MIN=9, FUN_MAX=10, FUN_MOD=11;
	// Constants used to denote operation names
	public final static int IPUSH_CONST=-1, IPUSH_VAR=-2, IPOP=-3, IWRITE_VAR=-4, IARITHMETIC_OP=-5;
	public final static int ISPECIAL_CALL=-6, IUNARY_OP=-7, IJMP=-8, IJMP_IF_TRUE=-9, IJMP_IF_FALSE=-10;
	public final static int ICMP_OP=-11, IBOOL_OP=-12;
	public final static int BINARY_CALL=-13;
	public final static int IPUSH_VAR_OFFSET=-14; 
	
	public PMStackMachine(int envSize, int stackSize) {
		env = new double[envSize];
		stack = new double[stackSize];
		stack_index=-1; /* points to the top of the stack */
	}
	
	public void updateEnv(int[] addresses, double[] values) {
		for(int i=0; i<addresses.length; i++) 
			env[addresses[i]] = values[i];
	}
	
	// vectors
	public void updateEnv(int[] addresses, double[][] values) {
		int idx;
		double[] vector;
		for(int i=0; i<addresses.length; i++) {
			idx = addresses[i];
			vector = values[i];
			for(int j=0; j < vector.length; j++) {
				env[idx++] = vector[j];
			}			
		}
	}
	
	public void updateEnv(int address, double value) {
		env[address] = value;
	}		
	
	
	public double execute(PMMachineCode code) {
		int[] instructions = code.instructions;
		double[] constants = code.constants;
		double v1,v2;
		boolean b1,b2;
		int pc=0, pc_jmp;
		/* traverse code.instructions */
		while (pc < instructions.length) {
			switch(instructions[pc++]) {
			case IPUSH_CONST:
				stack[++stack_index] = constants[instructions[pc++]];
				break;
			case IPUSH_VAR:
				stack[++stack_index] = env[instructions[pc++]];
				break;
			case IPUSH_VAR_OFFSET:
				v1 = stack[stack_index]; // apply floow here
				stack[stack_index] = env[instructions[pc++] + (int)Math.floor(v1)];
				break;
			case IWRITE_VAR:
				env[instructions[pc++]] = stack[stack_index--];
				break;
			case IARITHMETIC_OP:
				v1 = stack[stack_index--];
				v2 = stack[stack_index];
				switch(instructions[pc++]) {
				case OP_ADD: v2 += v1; break;
				case OP_SUB: v2 -= v1; break;
				case OP_MUL: v2 *= v1; break;
				case OP_DIV: v2 /= v1; break;
				case OP_POW: v2 = Math.pow(v2, v1); break;
				default: v2 = 0; /* should never happen */
				}
				stack[stack_index] = v2;
				break;
			case ICMP_OP:
				v1 = stack[stack_index--];
				v2 = stack[stack_index];
				switch(instructions[pc++]) {
				case OP_GT: b1 = (v2>v1); break;
				case OP_GEQ: b1 = (v2>-v1); break;
				case OP_LT: b1 = (v2 < v1); break;
				case OP_LEQ: b1 = (v2 <= v1); break;
				case OP_EQ: b1 = (v1==v2);
				default: b1 = false; /* should never happen */
				}
				v2 = (b1) ? 1.0 : 0.0;
				stack[stack_index] = v2;
				break;
			case IBOOL_OP:
				v1 = stack[stack_index--];
				v2 = stack[stack_index];
				b1 = (v1==0.0) ? false : true;
				b2 = (v2==0.0) ? false : true;
				switch(instructions[pc++]) {
				case OP_AND: b1 = (b1 && b2); break;
				case OP_OR: b1 = (b1 || b2); break;
				default: b1 = false; /* should never happen */
				}
				v2 = (b1) ? 1.0 : 0.0;
				stack[stack_index] = v2;
				break;
			case IUNARY_OP:
				v1 = stack[stack_index];
				switch(instructions[pc++]) {
				case OP_NEG: v2 = -v1; break;
				case OP_NOT: v2 = (v1==0.0) ? 1.0 : 0.0; break;
				default: v2 = 0; /* should never happen */
				}
				stack[stack_index] = v2;
				break;
			case ISPECIAL_CALL:
				switch(instructions[pc++]) {
				case FUN_EXP:
					stack[stack_index] = Math.exp(stack[stack_index]); break;
				case FUN_LOG:
					stack[stack_index] = Math.log(stack[stack_index]); break;
				case FUN_SQRT:
					stack[stack_index] = Math.pow(stack[stack_index],0.5); break;
				case FUN_SIN:
					stack[stack_index] = Math.sin(stack[stack_index]); break;
				case FUN_COS:
					stack[stack_index] = Math.cos(stack[stack_index]); break;
				case FUN_ABS:
					stack[stack_index] = Math.abs(stack[stack_index]); break;
				case FUN_FLOOR:
					stack[stack_index] = Math.floor(stack[stack_index]); break;
				case FUN_CEIL:
					stack[stack_index] = Math.ceil(stack[stack_index]); break;
				default:
					stack[stack_index] = 0;
				}
				break;
			case BINARY_CALL:
				v1 = stack[stack_index--];
				v2 = stack[stack_index];
				switch(instructions[pc++]) {
				case FUN_MIN:
					v2 = (v1 <= v2) ? v1 : v2; break;
				case FUN_MAX:
					v2 = (v1 <= v2) ? v2 : v1; break;
				case FUN_MOD:
					v2 = (v2 % v1); break;
				default:
					v2 = 0;
				}
				stack[stack_index] = v2;
				break;	
			case IJMP:
				pc = instructions[pc++]; break;
			case IJMP_IF_TRUE:
				v1 = stack[stack_index--];
				pc_jmp = instructions[pc++];  
				if (v1 != 0.0) 
					pc = pc_jmp;
				break;
			case IJMP_IF_FALSE:
				v1 = stack[stack_index--];
				pc_jmp = instructions[pc++];
				if (v1 == 0.0) 
					pc = pc_jmp;
				break;
			} /* switch instructions */
			
		}
		// return element on top of the stack, and clear
		//System.out.println("index:"+stack_index);
		/* return top of the stack, if any e.g. expression evaluation */
		if (stack_index >= 0) {
			return stack[stack_index--];
		} else {
			return 0;
		}
	}

}
