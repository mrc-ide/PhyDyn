package phydyn.model;

import beast.base.core.Description;

import java.util.ArrayList;
import java.util.List;

@Description("Class used to store a list of PMstack-machine instructions")

public class PMMachineCode {

	List<Integer> instrList;
	List<Double>  constantList;
	public int[] instructions;
	public double[] constants;  // need to store float numerals separately 
	int stackSize, maxStackSize;
	
	public PMMachineCode() {
		instrList = new ArrayList<Integer>();
		constantList = new ArrayList<Double>();
		instructions = null;
		stackSize = maxStackSize = 0;
	}
	
	// Useful for code generation - returns next available program counter
	public int getNextPC() {
		return instrList.size();
	}
	
	// During code generation, values such as jump pointers are not
	// known at the time an instruction is generated eg if/the/else
	public void updateInstruction(int pc, int v) {
		instrList.set(pc,v);
	}
	
	public void endCodeGeneration() {
		instructions = new int[instrList.size()];
		for(int i=0; i < instructions.length; i++) 
			instructions[i] = instrList.get(i);
		instrList.clear();
		constants = new double[constantList.size()];
		for(int i=0; i < constants.length; i++) 
			constants[i] = constantList.get(i);
		constantList.clear();
	}
	
	/* Code generation */
	public void generatePushConstant(double c) {
		instrList.add(PMStackMachine.IPUSH_CONST);
		instrList.add(constantList.size());
		constantList.add(c);
		updateStackSize(1);
	}
	
	public void generatePushVar(int address) {
		instrList.add(PMStackMachine.IPUSH_VAR);
		instrList.add(address);
		updateStackSize(1);
	}
	
	public void generatePushVarOffset(int address) {
		instrList.add(PMStackMachine.IPUSH_VAR_OFFSET);
		instrList.add(address);
		// no change in stack size - input top=offset
	}
	
	
	public void generateWriteVar(int address) {
		instrList.add(PMStackMachine.IWRITE_VAR);
		instrList.add(address);
		updateStackSize(-1);
	}
	
	public void generateJump(int pc) {
		instrList.add(PMStackMachine.IJMP);
		instrList.add(pc);
		// nothing taken from stack
	}
	
	public void generateJumpConditional(int pc,boolean cond) {
		if (cond)
			instrList.add(PMStackMachine.IJMP_IF_TRUE);
		else
			instrList.add(PMStackMachine.IJMP_IF_FALSE);
		instrList.add(pc);
		updateStackSize(-1); // condition taken from stack
	}
	
	public void generateBinaryInstruction(int instruction, int op) {
		instrList.add(instruction);
		instrList.add(op);
		updateStackSize(-1);
	}
	
	public void generateUnaryOp(int op) {
		instrList.add(PMStackMachine.IUNARY_OP);
		instrList.add(op);
		//updateStackSize(-1);
	}
	
	public void generateSpecialCall(int fun) {
		instrList.add(PMStackMachine.ISPECIAL_CALL);
		instrList.add(fun);
		//updateStackSize(-1);
	}
	
	public void generateBinaryCall(int fun) {
		instrList.add(PMStackMachine.BINARY_CALL);
		instrList.add(fun);
		//updateStackSize(-1);
	}
	
	public void updateStackSize(int i) {
		stackSize += i;
		if (stackSize > maxStackSize) maxStackSize = stackSize;
	}

}
