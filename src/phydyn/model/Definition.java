package phydyn.model;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import beast.core.BEASTObject;
import beast.core.Input;
import phydyn.model.parser.PopModelLexer;
import phydyn.model.parser.PopModelParser;
import phydyn.model.parser.PopModelParser.StmContext;

public class Definition extends BEASTObject {
	
	public Input<String> definitionStringInput = new Input<>(
			"value","Definition expresed as assignment statement");
	
	protected StmContext tree;
	public String name;
	public PMMachineCode code;
	
	@Override
	public void initAndValidate() {
		/* parse equation string */
		ANTLRInputStream input = new ANTLRInputStream(definitionStringInput.get());
		PopModelLexer lexer = new PopModelLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PopModelParser parser = new PopModelParser(tokens);
		tree = parser.stm();
		name = ((StmContext)tree).IDENT().getText();
	}
	
	public int compile(PopModelCompiler compiler) {
		code = compiler.compile(tree);
		return code.maxStackSize;
	}

}
