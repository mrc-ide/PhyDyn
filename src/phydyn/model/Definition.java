package phydyn.model;


import org.antlr.v4.runtime.CommonTokenStream;

//import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;


import beast.core.BEASTObject;
import beast.core.Input;
import phydyn.model.parser.PopModelLexer;
import phydyn.model.parser.PopModelParser;
import phydyn.model.parser.PopModelParser.StmContext;

public class Definition extends BEASTObject {
	
	public Input<String> definitionStringInput = new Input<>(
			"value","Definition expresed as assignment statement");
	
	protected StmContext stm;

	
	@Override
	public void initAndValidate() {
		/* parse equation string */
		CodePointCharStream  input = CharStreams.fromString( definitionStringInput.get()  );
		// ANTLRInputStream input = new ANTLRInputStream(definitionStringInput.get());
		try {
			PopModelLexer lexer = new PopModelLexer(input);		
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			PopModelParser parser = new PopModelParser(tokens);
			stm = (StmContext) parser.stm();
		} catch (Exception e) {
			System.out.println("Error while parsing definition: "+definitionStringInput.get());

			throw new IllegalArgumentException("Parsing error");
		}
	}
	
	public DefinitionObj createDefinition() {
		// new DefinitionO( ((StmContext)stm).IDENT().getText(), stm  );
		return new DefinitionObj( stm.IDENT().getText(), stm  );
	}
	
	

}
