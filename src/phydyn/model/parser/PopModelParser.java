// Generated from PopModel.g4 by ANTLR 4.7.1

package phydyn.model.parser;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class PopModelParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, EQ=17, 
		ASSIGN=18, AND=19, OR=20, NOT=21, LEQ=22, GT=23, LT=24, GEQ=25, ADD=26, 
		SUB=27, MUL=28, DIV=29, POW=30, EXP=31, LOG=32, SQRT=33, SIN=34, COS=35, 
		MAX=36, MIN=37, MOD=38, ABS=39, FLOOR=40, CEIL=41, IF=42, THEN=43, ELSE=44, 
		INT=45, FLOAT=46, IDENT=47, LINE_COMMENT=48, MULTILINE_COMENT=49, WS=50;
	public static final int
		RULE_bound = 0, RULE_arg = 1, RULE_priorDecl = 2, RULE_operatorDecl = 3, 
		RULE_adeclBody = 4, RULE_analysisDecl = 5, RULE_analysisSpec = 6, RULE_definitions = 7, 
		RULE_matrixEquation = 8, RULE_matrixEquations = 9, RULE_stm = 10, RULE_equation = 11, 
		RULE_expr = 12;
	public static final String[] ruleNames = {
		"bound", "arg", "priorDecl", "operatorDecl", "adeclBody", "analysisDecl", 
		"analysisSpec", "definitions", "matrixEquation", "matrixEquations", "stm", 
		"equation", "expr"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'-inf'", "'inf'", "'prior'", "'('", "','", "')'", "';'", "'operator'", 
		"'{'", "'}'", "'F'", "'G'", "'D'", "'dot'", "'['", "']'", "'=='", "'='", 
		"'and'", "'or'", "'not'", "'!>'", "'>'", "'!>='", "'>='", "'+'", "'-'", 
		"'*'", "'/'", "'^'", "'exp'", "'log'", "'sqrt'", "'sin'", "'cos'", "'max'", 
		"'min'", "'mod'", "'abs'", "'floor'", "'ceil'", "'if'", "'then'", "'else'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, "EQ", "ASSIGN", "AND", "OR", "NOT", "LEQ", 
		"GT", "LT", "GEQ", "ADD", "SUB", "MUL", "DIV", "POW", "EXP", "LOG", "SQRT", 
		"SIN", "COS", "MAX", "MIN", "MOD", "ABS", "FLOOR", "CEIL", "IF", "THEN", 
		"ELSE", "INT", "FLOAT", "IDENT", "LINE_COMMENT", "MULTILINE_COMENT", "WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "PopModel.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public PopModelParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class BoundContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(PopModelParser.INT, 0); }
		public TerminalNode FLOAT() { return getToken(PopModelParser.FLOAT, 0); }
		public BoundContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bound; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PopModelVisitor ) return ((PopModelVisitor<? extends T>)visitor).visitBound(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BoundContext bound() throws RecognitionException {
		BoundContext _localctx = new BoundContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_bound);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(26);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << INT) | (1L << FLOAT))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArgContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(PopModelParser.INT, 0); }
		public TerminalNode FLOAT() { return getToken(PopModelParser.FLOAT, 0); }
		public TerminalNode IDENT() { return getToken(PopModelParser.IDENT, 0); }
		public ArgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arg; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PopModelVisitor ) return ((PopModelVisitor<? extends T>)visitor).visitArg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArgContext arg() throws RecognitionException {
		ArgContext _localctx = new ArgContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_arg);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(28);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << FLOAT) | (1L << IDENT))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PriorDeclContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(PopModelParser.IDENT, 0); }
		public List<ArgContext> arg() {
			return getRuleContexts(ArgContext.class);
		}
		public ArgContext arg(int i) {
			return getRuleContext(ArgContext.class,i);
		}
		public PriorDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_priorDecl; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PopModelVisitor ) return ((PopModelVisitor<? extends T>)visitor).visitPriorDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PriorDeclContext priorDecl() throws RecognitionException {
		PriorDeclContext _localctx = new PriorDeclContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_priorDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(30);
			match(T__2);
			setState(31);
			match(ASSIGN);
			setState(32);
			match(IDENT);
			setState(33);
			match(T__3);
			setState(34);
			arg();
			setState(39);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(35);
				match(T__4);
				setState(36);
				arg();
				}
				}
				setState(41);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(42);
			match(T__5);
			setState(43);
			match(T__6);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OperatorDeclContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(PopModelParser.IDENT, 0); }
		public TerminalNode INT() { return getToken(PopModelParser.INT, 0); }
		public List<ArgContext> arg() {
			return getRuleContexts(ArgContext.class);
		}
		public ArgContext arg(int i) {
			return getRuleContext(ArgContext.class,i);
		}
		public OperatorDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operatorDecl; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PopModelVisitor ) return ((PopModelVisitor<? extends T>)visitor).visitOperatorDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OperatorDeclContext operatorDecl() throws RecognitionException {
		OperatorDeclContext _localctx = new OperatorDeclContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_operatorDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(45);
			match(T__7);
			setState(49);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__3) {
				{
				setState(46);
				match(T__3);
				setState(47);
				match(INT);
				setState(48);
				match(T__5);
				}
			}

			setState(51);
			match(ASSIGN);
			setState(52);
			match(IDENT);
			setState(53);
			match(T__3);
			setState(62);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << FLOAT) | (1L << IDENT))) != 0)) {
				{
				setState(54);
				arg();
				setState(59);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__4) {
					{
					{
					setState(55);
					match(T__4);
					setState(56);
					arg();
					}
					}
					setState(61);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(64);
			match(T__5);
			setState(65);
			match(T__6);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AdeclBodyContext extends ParserRuleContext {
		public List<PriorDeclContext> priorDecl() {
			return getRuleContexts(PriorDeclContext.class);
		}
		public PriorDeclContext priorDecl(int i) {
			return getRuleContext(PriorDeclContext.class,i);
		}
		public List<OperatorDeclContext> operatorDecl() {
			return getRuleContexts(OperatorDeclContext.class);
		}
		public OperatorDeclContext operatorDecl(int i) {
			return getRuleContext(OperatorDeclContext.class,i);
		}
		public AdeclBodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_adeclBody; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PopModelVisitor ) return ((PopModelVisitor<? extends T>)visitor).visitAdeclBody(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AdeclBodyContext adeclBody() throws RecognitionException {
		AdeclBodyContext _localctx = new AdeclBodyContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_adeclBody);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(67);
			match(T__8);
			setState(71);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(68);
				priorDecl();
				}
				}
				setState(73);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(77);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__7) {
				{
				{
				setState(74);
				operatorDecl();
				}
				}
				setState(79);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(80);
			match(T__9);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AnalysisDeclContext extends ParserRuleContext {
		public List<TerminalNode> IDENT() { return getTokens(PopModelParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(PopModelParser.IDENT, i);
		}
		public AdeclBodyContext adeclBody() {
			return getRuleContext(AdeclBodyContext.class,0);
		}
		public List<BoundContext> bound() {
			return getRuleContexts(BoundContext.class);
		}
		public BoundContext bound(int i) {
			return getRuleContext(BoundContext.class,i);
		}
		public AnalysisDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_analysisDecl; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PopModelVisitor ) return ((PopModelVisitor<? extends T>)visitor).visitAnalysisDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AnalysisDeclContext analysisDecl() throws RecognitionException {
		AnalysisDeclContext _localctx = new AnalysisDeclContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_analysisDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(82);
			match(IDENT);
			setState(83);
			match(ASSIGN);
			setState(84);
			match(IDENT);
			setState(91);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__3) {
				{
				setState(85);
				match(T__3);
				setState(86);
				bound();
				setState(87);
				match(T__4);
				setState(88);
				bound();
				setState(89);
				match(T__5);
				}
			}

			setState(93);
			adeclBody();
			setState(94);
			match(T__6);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AnalysisSpecContext extends ParserRuleContext {
		public List<AnalysisDeclContext> analysisDecl() {
			return getRuleContexts(AnalysisDeclContext.class);
		}
		public AnalysisDeclContext analysisDecl(int i) {
			return getRuleContext(AnalysisDeclContext.class,i);
		}
		public AnalysisSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_analysisSpec; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PopModelVisitor ) return ((PopModelVisitor<? extends T>)visitor).visitAnalysisSpec(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AnalysisSpecContext analysisSpec() throws RecognitionException {
		AnalysisSpecContext _localctx = new AnalysisSpecContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_analysisSpec);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(99);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==IDENT) {
				{
				{
				setState(96);
				analysisDecl();
				}
				}
				setState(101);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DefinitionsContext extends ParserRuleContext {
		public List<StmContext> stm() {
			return getRuleContexts(StmContext.class);
		}
		public StmContext stm(int i) {
			return getRuleContext(StmContext.class,i);
		}
		public DefinitionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_definitions; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PopModelVisitor ) return ((PopModelVisitor<? extends T>)visitor).visitDefinitions(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DefinitionsContext definitions() throws RecognitionException {
		DefinitionsContext _localctx = new DefinitionsContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_definitions);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(105); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(102);
				stm();
				setState(103);
				match(T__6);
				}
				}
				setState(107); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==IDENT );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MatrixEquationContext extends ParserRuleContext {
		public MatrixEquationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_matrixEquation; }
	 
		public MatrixEquationContext() { }
		public void copyFrom(MatrixEquationContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class MigrationEquationContext extends MatrixEquationContext {
		public List<TerminalNode> IDENT() { return getTokens(PopModelParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(PopModelParser.IDENT, i);
		}
		public TerminalNode ASSIGN() { return getToken(PopModelParser.ASSIGN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public MigrationEquationContext(MatrixEquationContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PopModelVisitor ) return ((PopModelVisitor<? extends T>)visitor).visitMigrationEquation(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DeathEquationContext extends MatrixEquationContext {
		public TerminalNode IDENT() { return getToken(PopModelParser.IDENT, 0); }
		public TerminalNode ASSIGN() { return getToken(PopModelParser.ASSIGN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public DeathEquationContext(MatrixEquationContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PopModelVisitor ) return ((PopModelVisitor<? extends T>)visitor).visitDeathEquation(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NondemeEquationContext extends MatrixEquationContext {
		public TerminalNode IDENT() { return getToken(PopModelParser.IDENT, 0); }
		public TerminalNode ASSIGN() { return getToken(PopModelParser.ASSIGN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public NondemeEquationContext(MatrixEquationContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PopModelVisitor ) return ((PopModelVisitor<? extends T>)visitor).visitNondemeEquation(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BirthEquationContext extends MatrixEquationContext {
		public List<TerminalNode> IDENT() { return getTokens(PopModelParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(PopModelParser.IDENT, i);
		}
		public TerminalNode ASSIGN() { return getToken(PopModelParser.ASSIGN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public BirthEquationContext(MatrixEquationContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PopModelVisitor ) return ((PopModelVisitor<? extends T>)visitor).visitBirthEquation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MatrixEquationContext matrixEquation() throws RecognitionException {
		MatrixEquationContext _localctx = new MatrixEquationContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_matrixEquation);
		try {
			setState(137);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__10:
				_localctx = new BirthEquationContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(109);
				match(T__10);
				setState(110);
				match(T__3);
				setState(111);
				match(IDENT);
				setState(112);
				match(T__4);
				setState(113);
				match(IDENT);
				setState(114);
				match(T__5);
				setState(115);
				match(ASSIGN);
				setState(116);
				expr(0);
				}
				break;
			case T__11:
				_localctx = new MigrationEquationContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(117);
				match(T__11);
				setState(118);
				match(T__3);
				setState(119);
				match(IDENT);
				setState(120);
				match(T__4);
				setState(121);
				match(IDENT);
				setState(122);
				match(T__5);
				setState(123);
				match(ASSIGN);
				setState(124);
				expr(0);
				}
				break;
			case T__12:
				_localctx = new DeathEquationContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(125);
				match(T__12);
				setState(126);
				match(T__3);
				setState(127);
				match(IDENT);
				setState(128);
				match(T__5);
				setState(129);
				match(ASSIGN);
				setState(130);
				expr(0);
				}
				break;
			case T__13:
				_localctx = new NondemeEquationContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(131);
				match(T__13);
				setState(132);
				match(T__3);
				setState(133);
				match(IDENT);
				setState(134);
				match(T__5);
				setState(135);
				match(ASSIGN);
				setState(136);
				expr(0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MatrixEquationsContext extends ParserRuleContext {
		public List<MatrixEquationContext> matrixEquation() {
			return getRuleContexts(MatrixEquationContext.class);
		}
		public MatrixEquationContext matrixEquation(int i) {
			return getRuleContext(MatrixEquationContext.class,i);
		}
		public MatrixEquationsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_matrixEquations; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PopModelVisitor ) return ((PopModelVisitor<? extends T>)visitor).visitMatrixEquations(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MatrixEquationsContext matrixEquations() throws RecognitionException {
		MatrixEquationsContext _localctx = new MatrixEquationsContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_matrixEquations);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(142); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(139);
				matrixEquation();
				setState(140);
				match(T__6);
				}
				}
				setState(144); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__10) | (1L << T__11) | (1L << T__12) | (1L << T__13))) != 0) );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StmContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(PopModelParser.IDENT, 0); }
		public TerminalNode ASSIGN() { return getToken(PopModelParser.ASSIGN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public StmContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stm; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PopModelVisitor ) return ((PopModelVisitor<? extends T>)visitor).visitStm(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StmContext stm() throws RecognitionException {
		StmContext _localctx = new StmContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_stm);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(146);
			match(IDENT);
			setState(147);
			match(ASSIGN);
			setState(148);
			expr(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EquationContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public EquationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_equation; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PopModelVisitor ) return ((PopModelVisitor<? extends T>)visitor).visitEquation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EquationContext equation() throws RecognitionException {
		EquationContext _localctx = new EquationContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_equation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(150);
			expr(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprContext extends ParserRuleContext {
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
	 
		public ExprContext() { }
		public void copyFrom(ExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class IntExprContext extends ExprContext {
		public Token val;
		public TerminalNode INT() { return getToken(PopModelParser.INT, 0); }
		public IntExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PopModelVisitor ) return ((PopModelVisitor<? extends T>)visitor).visitIntExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class PowerExprContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public PowerExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PopModelVisitor ) return ((PopModelVisitor<? extends T>)visitor).visitPowerExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CondExprContext extends ExprContext {
		public TerminalNode IF() { return getToken(PopModelParser.IF, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode THEN() { return getToken(PopModelParser.THEN, 0); }
		public TerminalNode ELSE() { return getToken(PopModelParser.ELSE, 0); }
		public CondExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PopModelVisitor ) return ((PopModelVisitor<? extends T>)visitor).visitCondExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class VectorExprContext extends ExprContext {
		public TerminalNode IDENT() { return getToken(PopModelParser.IDENT, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public VectorExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PopModelVisitor ) return ((PopModelVisitor<? extends T>)visitor).visitVectorExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class MinusExprContext extends ExprContext {
		public TerminalNode SUB() { return getToken(PopModelParser.SUB, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public MinusExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PopModelVisitor ) return ((PopModelVisitor<? extends T>)visitor).visitMinusExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CallSpecialExprContext extends ExprContext {
		public Token op;
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode EXP() { return getToken(PopModelParser.EXP, 0); }
		public TerminalNode LOG() { return getToken(PopModelParser.LOG, 0); }
		public TerminalNode SQRT() { return getToken(PopModelParser.SQRT, 0); }
		public TerminalNode SIN() { return getToken(PopModelParser.SIN, 0); }
		public TerminalNode COS() { return getToken(PopModelParser.COS, 0); }
		public TerminalNode ABS() { return getToken(PopModelParser.ABS, 0); }
		public TerminalNode FLOOR() { return getToken(PopModelParser.FLOOR, 0); }
		public TerminalNode CEIL() { return getToken(PopModelParser.CEIL, 0); }
		public CallSpecialExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PopModelVisitor ) return ((PopModelVisitor<? extends T>)visitor).visitCallSpecialExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CmpExprContext extends ExprContext {
		public Token op;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode GT() { return getToken(PopModelParser.GT, 0); }
		public TerminalNode LT() { return getToken(PopModelParser.LT, 0); }
		public TerminalNode GEQ() { return getToken(PopModelParser.GEQ, 0); }
		public TerminalNode LEQ() { return getToken(PopModelParser.LEQ, 0); }
		public TerminalNode EQ() { return getToken(PopModelParser.EQ, 0); }
		public CmpExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PopModelVisitor ) return ((PopModelVisitor<? extends T>)visitor).visitCmpExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class IdentExprContext extends ExprContext {
		public TerminalNode IDENT() { return getToken(PopModelParser.IDENT, 0); }
		public IdentExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PopModelVisitor ) return ((PopModelVisitor<? extends T>)visitor).visitIdentExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FloatExprContext extends ExprContext {
		public Token val;
		public TerminalNode FLOAT() { return getToken(PopModelParser.FLOAT, 0); }
		public FloatExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PopModelVisitor ) return ((PopModelVisitor<? extends T>)visitor).visitFloatExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NotExprContext extends ExprContext {
		public TerminalNode NOT() { return getToken(PopModelParser.NOT, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public NotExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PopModelVisitor ) return ((PopModelVisitor<? extends T>)visitor).visitNotExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SumExprContext extends ExprContext {
		public Token op;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode ADD() { return getToken(PopModelParser.ADD, 0); }
		public TerminalNode SUB() { return getToken(PopModelParser.SUB, 0); }
		public SumExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PopModelVisitor ) return ((PopModelVisitor<? extends T>)visitor).visitSumExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ProdExprContext extends ExprContext {
		public Token op;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode MUL() { return getToken(PopModelParser.MUL, 0); }
		public TerminalNode DIV() { return getToken(PopModelParser.DIV, 0); }
		public ProdExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PopModelVisitor ) return ((PopModelVisitor<? extends T>)visitor).visitProdExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CallBinaryExprContext extends ExprContext {
		public Token op;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode MAX() { return getToken(PopModelParser.MAX, 0); }
		public TerminalNode MIN() { return getToken(PopModelParser.MIN, 0); }
		public TerminalNode MOD() { return getToken(PopModelParser.MOD, 0); }
		public CallBinaryExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PopModelVisitor ) return ((PopModelVisitor<? extends T>)visitor).visitCallBinaryExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BoolExprContext extends ExprContext {
		public Token op;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode AND() { return getToken(PopModelParser.AND, 0); }
		public TerminalNode OR() { return getToken(PopModelParser.OR, 0); }
		public BoolExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PopModelVisitor ) return ((PopModelVisitor<? extends T>)visitor).visitBoolExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ParenthExprContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ParenthExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PopModelVisitor ) return ((PopModelVisitor<? extends T>)visitor).visitParenthExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		return expr(0);
	}

	private ExprContext expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext _localctx = new ExprContext(_ctx, _parentState);
		ExprContext _prevctx = _localctx;
		int _startState = 24;
		enterRecursionRule(_localctx, 24, RULE_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(188);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				{
				_localctx = new ParenthExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(153);
				match(T__3);
				setState(154);
				expr(0);
				setState(155);
				match(T__5);
				}
				break;
			case 2:
				{
				_localctx = new MinusExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(157);
				match(SUB);
				setState(158);
				expr(14);
				}
				break;
			case 3:
				{
				_localctx = new NotExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(159);
				match(NOT);
				setState(160);
				expr(13);
				}
				break;
			case 4:
				{
				_localctx = new CondExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(161);
				match(IF);
				setState(162);
				expr(0);
				setState(163);
				match(THEN);
				setState(164);
				expr(0);
				setState(165);
				match(ELSE);
				setState(166);
				expr(12);
				}
				break;
			case 5:
				{
				_localctx = new CallSpecialExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(168);
				((CallSpecialExprContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EXP) | (1L << LOG) | (1L << SQRT) | (1L << SIN) | (1L << COS) | (1L << ABS) | (1L << FLOOR) | (1L << CEIL))) != 0)) ) {
					((CallSpecialExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(169);
				match(T__3);
				setState(170);
				expr(0);
				setState(171);
				match(T__5);
				}
				break;
			case 6:
				{
				_localctx = new CallBinaryExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(173);
				((CallBinaryExprContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MAX) | (1L << MIN) | (1L << MOD))) != 0)) ) {
					((CallBinaryExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(174);
				match(T__3);
				setState(175);
				expr(0);
				setState(176);
				match(T__4);
				setState(177);
				expr(0);
				setState(178);
				match(T__5);
				}
				break;
			case 7:
				{
				_localctx = new VectorExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(180);
				match(IDENT);
				setState(181);
				match(T__14);
				setState(182);
				expr(0);
				setState(183);
				match(T__15);
				}
				break;
			case 8:
				{
				_localctx = new IdentExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(185);
				match(IDENT);
				}
				break;
			case 9:
				{
				_localctx = new IntExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(186);
				((IntExprContext)_localctx).val = match(INT);
				}
				break;
			case 10:
				{
				_localctx = new FloatExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(187);
				((FloatExprContext)_localctx).val = match(FLOAT);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(207);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(205);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
					case 1:
						{
						_localctx = new PowerExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(190);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(191);
						match(POW);
						setState(192);
						expr(11);
						}
						break;
					case 2:
						{
						_localctx = new ProdExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(193);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(194);
						((ProdExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==MUL || _la==DIV) ) {
							((ProdExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(195);
						expr(11);
						}
						break;
					case 3:
						{
						_localctx = new SumExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(196);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(197);
						((SumExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==ADD || _la==SUB) ) {
							((SumExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(198);
						expr(10);
						}
						break;
					case 4:
						{
						_localctx = new CmpExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(199);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(200);
						((CmpExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EQ) | (1L << LEQ) | (1L << GT) | (1L << LT) | (1L << GEQ))) != 0)) ) {
							((CmpExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(201);
						expr(9);
						}
						break;
					case 5:
						{
						_localctx = new BoolExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(202);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(203);
						((BoolExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==AND || _la==OR) ) {
							((BoolExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(204);
						expr(8);
						}
						break;
					}
					} 
				}
				setState(209);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 12:
			return expr_sempred((ExprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 11);
		case 1:
			return precpred(_ctx, 10);
		case 2:
			return precpred(_ctx, 9);
		case 3:
			return precpred(_ctx, 8);
		case 4:
			return precpred(_ctx, 7);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\64\u00d5\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\3\2\3\2\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\7\4(\n\4\f\4\16\4+\13\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\5\5\64\n\5"+
		"\3\5\3\5\3\5\3\5\3\5\3\5\7\5<\n\5\f\5\16\5?\13\5\5\5A\n\5\3\5\3\5\3\5"+
		"\3\6\3\6\7\6H\n\6\f\6\16\6K\13\6\3\6\7\6N\n\6\f\6\16\6Q\13\6\3\6\3\6\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7^\n\7\3\7\3\7\3\7\3\b\7\bd\n\b\f"+
		"\b\16\bg\13\b\3\t\3\t\3\t\6\tl\n\t\r\t\16\tm\3\n\3\n\3\n\3\n\3\n\3\n\3"+
		"\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\3\n\3\n\3\n\5\n\u008c\n\n\3\13\3\13\3\13\6\13\u0091\n\13\r\13\16"+
		"\13\u0092\3\f\3\f\3\f\3\f\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3"+
		"\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3"+
		"\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3"+
		"\16\5\16\u00bf\n\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\7\16\u00d0\n\16\f\16\16\16\u00d3\13\16\3\16"+
		"\2\3\32\17\2\4\6\b\n\f\16\20\22\24\26\30\32\2\n\4\2\3\4/\60\3\2/\61\4"+
		"\2!%)+\3\2&(\3\2\36\37\3\2\34\35\4\2\23\23\30\33\3\2\25\26\2\u00e2\2\34"+
		"\3\2\2\2\4\36\3\2\2\2\6 \3\2\2\2\b/\3\2\2\2\nE\3\2\2\2\fT\3\2\2\2\16e"+
		"\3\2\2\2\20k\3\2\2\2\22\u008b\3\2\2\2\24\u0090\3\2\2\2\26\u0094\3\2\2"+
		"\2\30\u0098\3\2\2\2\32\u00be\3\2\2\2\34\35\t\2\2\2\35\3\3\2\2\2\36\37"+
		"\t\3\2\2\37\5\3\2\2\2 !\7\5\2\2!\"\7\24\2\2\"#\7\61\2\2#$\7\6\2\2$)\5"+
		"\4\3\2%&\7\7\2\2&(\5\4\3\2\'%\3\2\2\2(+\3\2\2\2)\'\3\2\2\2)*\3\2\2\2*"+
		",\3\2\2\2+)\3\2\2\2,-\7\b\2\2-.\7\t\2\2.\7\3\2\2\2/\63\7\n\2\2\60\61\7"+
		"\6\2\2\61\62\7/\2\2\62\64\7\b\2\2\63\60\3\2\2\2\63\64\3\2\2\2\64\65\3"+
		"\2\2\2\65\66\7\24\2\2\66\67\7\61\2\2\67@\7\6\2\28=\5\4\3\29:\7\7\2\2:"+
		"<\5\4\3\2;9\3\2\2\2<?\3\2\2\2=;\3\2\2\2=>\3\2\2\2>A\3\2\2\2?=\3\2\2\2"+
		"@8\3\2\2\2@A\3\2\2\2AB\3\2\2\2BC\7\b\2\2CD\7\t\2\2D\t\3\2\2\2EI\7\13\2"+
		"\2FH\5\6\4\2GF\3\2\2\2HK\3\2\2\2IG\3\2\2\2IJ\3\2\2\2JO\3\2\2\2KI\3\2\2"+
		"\2LN\5\b\5\2ML\3\2\2\2NQ\3\2\2\2OM\3\2\2\2OP\3\2\2\2PR\3\2\2\2QO\3\2\2"+
		"\2RS\7\f\2\2S\13\3\2\2\2TU\7\61\2\2UV\7\24\2\2V]\7\61\2\2WX\7\6\2\2XY"+
		"\5\2\2\2YZ\7\7\2\2Z[\5\2\2\2[\\\7\b\2\2\\^\3\2\2\2]W\3\2\2\2]^\3\2\2\2"+
		"^_\3\2\2\2_`\5\n\6\2`a\7\t\2\2a\r\3\2\2\2bd\5\f\7\2cb\3\2\2\2dg\3\2\2"+
		"\2ec\3\2\2\2ef\3\2\2\2f\17\3\2\2\2ge\3\2\2\2hi\5\26\f\2ij\7\t\2\2jl\3"+
		"\2\2\2kh\3\2\2\2lm\3\2\2\2mk\3\2\2\2mn\3\2\2\2n\21\3\2\2\2op\7\r\2\2p"+
		"q\7\6\2\2qr\7\61\2\2rs\7\7\2\2st\7\61\2\2tu\7\b\2\2uv\7\24\2\2v\u008c"+
		"\5\32\16\2wx\7\16\2\2xy\7\6\2\2yz\7\61\2\2z{\7\7\2\2{|\7\61\2\2|}\7\b"+
		"\2\2}~\7\24\2\2~\u008c\5\32\16\2\177\u0080\7\17\2\2\u0080\u0081\7\6\2"+
		"\2\u0081\u0082\7\61\2\2\u0082\u0083\7\b\2\2\u0083\u0084\7\24\2\2\u0084"+
		"\u008c\5\32\16\2\u0085\u0086\7\20\2\2\u0086\u0087\7\6\2\2\u0087\u0088"+
		"\7\61\2\2\u0088\u0089\7\b\2\2\u0089\u008a\7\24\2\2\u008a\u008c\5\32\16"+
		"\2\u008bo\3\2\2\2\u008bw\3\2\2\2\u008b\177\3\2\2\2\u008b\u0085\3\2\2\2"+
		"\u008c\23\3\2\2\2\u008d\u008e\5\22\n\2\u008e\u008f\7\t\2\2\u008f\u0091"+
		"\3\2\2\2\u0090\u008d\3\2\2\2\u0091\u0092\3\2\2\2\u0092\u0090\3\2\2\2\u0092"+
		"\u0093\3\2\2\2\u0093\25\3\2\2\2\u0094\u0095\7\61\2\2\u0095\u0096\7\24"+
		"\2\2\u0096\u0097\5\32\16\2\u0097\27\3\2\2\2\u0098\u0099\5\32\16\2\u0099"+
		"\31\3\2\2\2\u009a\u009b\b\16\1\2\u009b\u009c\7\6\2\2\u009c\u009d\5\32"+
		"\16\2\u009d\u009e\7\b\2\2\u009e\u00bf\3\2\2\2\u009f\u00a0\7\35\2\2\u00a0"+
		"\u00bf\5\32\16\20\u00a1\u00a2\7\27\2\2\u00a2\u00bf\5\32\16\17\u00a3\u00a4"+
		"\7,\2\2\u00a4\u00a5\5\32\16\2\u00a5\u00a6\7-\2\2\u00a6\u00a7\5\32\16\2"+
		"\u00a7\u00a8\7.\2\2\u00a8\u00a9\5\32\16\16\u00a9\u00bf\3\2\2\2\u00aa\u00ab"+
		"\t\4\2\2\u00ab\u00ac\7\6\2\2\u00ac\u00ad\5\32\16\2\u00ad\u00ae\7\b\2\2"+
		"\u00ae\u00bf\3\2\2\2\u00af\u00b0\t\5\2\2\u00b0\u00b1\7\6\2\2\u00b1\u00b2"+
		"\5\32\16\2\u00b2\u00b3\7\7\2\2\u00b3\u00b4\5\32\16\2\u00b4\u00b5\7\b\2"+
		"\2\u00b5\u00bf\3\2\2\2\u00b6\u00b7\7\61\2\2\u00b7\u00b8\7\21\2\2\u00b8"+
		"\u00b9\5\32\16\2\u00b9\u00ba\7\22\2\2\u00ba\u00bf\3\2\2\2\u00bb\u00bf"+
		"\7\61\2\2\u00bc\u00bf\7/\2\2\u00bd\u00bf\7\60\2\2\u00be\u009a\3\2\2\2"+
		"\u00be\u009f\3\2\2\2\u00be\u00a1\3\2\2\2\u00be\u00a3\3\2\2\2\u00be\u00aa"+
		"\3\2\2\2\u00be\u00af\3\2\2\2\u00be\u00b6\3\2\2\2\u00be\u00bb\3\2\2\2\u00be"+
		"\u00bc\3\2\2\2\u00be\u00bd\3\2\2\2\u00bf\u00d1\3\2\2\2\u00c0\u00c1\f\r"+
		"\2\2\u00c1\u00c2\7 \2\2\u00c2\u00d0\5\32\16\r\u00c3\u00c4\f\f\2\2\u00c4"+
		"\u00c5\t\6\2\2\u00c5\u00d0\5\32\16\r\u00c6\u00c7\f\13\2\2\u00c7\u00c8"+
		"\t\7\2\2\u00c8\u00d0\5\32\16\f\u00c9\u00ca\f\n\2\2\u00ca\u00cb\t\b\2\2"+
		"\u00cb\u00d0\5\32\16\13\u00cc\u00cd\f\t\2\2\u00cd\u00ce\t\t\2\2\u00ce"+
		"\u00d0\5\32\16\n\u00cf\u00c0\3\2\2\2\u00cf\u00c3\3\2\2\2\u00cf\u00c6\3"+
		"\2\2\2\u00cf\u00c9\3\2\2\2\u00cf\u00cc\3\2\2\2\u00d0\u00d3\3\2\2\2\u00d1"+
		"\u00cf\3\2\2\2\u00d1\u00d2\3\2\2\2\u00d2\33\3\2\2\2\u00d3\u00d1\3\2\2"+
		"\2\20)\63=@IO]em\u008b\u0092\u00be\u00cf\u00d1";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}