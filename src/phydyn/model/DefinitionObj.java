package phydyn.model;

import phydyn.model.parser.PopModelParser.StmContext;

// non-xml version of model Definitions

public class DefinitionObj {
	
	public StmContext stm;  // contains v = exp - maybe we want the rhs exp separate
	public String name;
	public PMMachineCode code;
	
	
	public DefinitionObj(String lhs, StmContext rhs) {
		name = lhs;
		stm = rhs;
	}
	

	public int compile(PopModelCompiler compiler) {
		code = compiler.compile(stm);
		return code.maxStackSize;
	}

}
