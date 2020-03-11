package phydyn.model;

import java.util.ArrayList;
import java.util.List;

// import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;

import org.antlr.v4.runtime.CommonTokenStream;

import beast.core.BEASTObject;
import beast.core.Input;
import phydyn.model.parser.PopModelLexer;
import phydyn.model.parser.PopModelParser;
import phydyn.model.parser.PopModelParser.DefinitionsContext;
import phydyn.model.parser.PopModelParser.StmContext;

public class Definitions extends BEASTObject {
	
	public Input<String> definitionStringInput = new Input<>(
			"value","Definitions: simi-colon separated assignment statements");
	
	private DefinitionsContext defsCtx;

	public Definitions() { }
	
	public Definitions(String defsStr) {
		definitionStringInput.setValue(defsStr, this);
	}

	@Override
	public void initAndValidate() {		
		/* parse equation string */
		CodePointCharStream  input = CharStreams.fromString( definitionStringInput.get()  );
		//ANTLRInputStream input = new ANTLRInputStream(definitionStringInput.get());
		try {
			PopModelLexer lexer = new PopModelLexer(input);		
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			PopModelParser parser = new PopModelParser(tokens);
			defsCtx =  parser.definitions();
		} catch (Exception e) {
			System.out.println( "Error while parsing definitions: "+definitionStringInput.get());

			throw new IllegalArgumentException("Parsing error");
		}
	}
	
	public List<DefinitionObj> createDefinitions() {	
		List<StmContext> stmCtxs = defsCtx.stm();
		List<DefinitionObj> defs = new ArrayList<>();
		for (StmContext def: stmCtxs ) {
			defs.add(new DefinitionObj(def.IDENT().getText(),  def  ));
		}
		return defs;
	}
	
	
	// added to allow re-definition of Definitions as a String - remove the above soon.
	public static List<DefinitionObj> createDefinitions(String defsString) {	
		DefinitionsContext defsCtx;

		/* parse equation string */
		CodePointCharStream  input = CharStreams.fromString( defsString  );
		//ANTLRInputStream input = new ANTLRInputStream(definitionStringInput.get());
		try {
			PopModelLexer lexer = new PopModelLexer(input);		
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			PopModelParser parser = new PopModelParser(tokens);
			defsCtx =  parser.definitions();
		} catch (Exception e) {
			System.out.println( "Error while parsing definitions: "+defsString);
			throw new IllegalArgumentException("Parsing error");
		}		
				
		List<StmContext> stmCtxs = defsCtx.stm();
		List<DefinitionObj> defs = new ArrayList<>();
		for (StmContext def: stmCtxs ) {
			defs.add(new DefinitionObj(def.IDENT().getText(),  def  ));
		}
		return defs;
	}
	
	

}
