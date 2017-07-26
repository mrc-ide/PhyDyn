package phydyn.model;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.LexerNoViableAltException;

import phydyn.model.parser.PopModelLexer;

public class PopModelBailLexer extends PopModelLexer {
	public PopModelBailLexer(CharStream input) {
		super(input);
	}
	
	@Override
	public void recover(LexerNoViableAltException e) {
		throw new RuntimeException(e);
	}


	
}
