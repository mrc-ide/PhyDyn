// Generated from PopModel.g4 by ANTLR 4.5.2

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
	static { RuntimeMetaData.checkVersion("4.5.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, EQ=11, ASSIGN=12, AND=13, OR=14, NOT=15, LEQ=16, GT=17, LT=18, 
		GEQ=19, ADD=20, SUB=21, MUL=22, DIV=23, POW=24, EXP=25, LOG=26, SQRT=27, 
		SIN=28, COS=29, MAX=30, MIN=31, MOD=32, ABS=33, FLOOR=34, CEIL=35, IF=36, 
		THEN=37, ELSE=38, INT=39, FLOAT=40, IDENT=41, LINE_COMMENT=42, MULTILINE_COMENT=43, 
		WS=44;
	public static final int
		RULE_definitions = 0, RULE_matrixEquation = 1, RULE_matrixEquations = 2, 
		RULE_stm = 3, RULE_equation = 4, RULE_expr = 5;
	public static final String[] ruleNames = {
		"definitions", "matrixEquation", "matrixEquations", "stm", "equation", 
		"expr"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "';'", "'F'", "'('", "','", "')'", "'G'", "'D'", "'dot'", "'['", 
		"']'", "'=='", "'='", "'and'", "'or'", "'not'", "'!>'", "'>'", "'!>='", 
		"'>='", "'+'", "'-'", "'*'", "'/'", "'^'", "'exp'", "'log'", "'sqrt'", 
		"'sin'", "'cos'", "'max'", "'min'", "'mod'", "'abs'", "'floor'", "'ceil'", 
		"'if'", "'then'", "'else'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, "EQ", 
		"ASSIGN", "AND", "OR", "NOT", "LEQ", "GT", "LT", "GEQ", "ADD", "SUB", 
		"MUL", "DIV", "POW", "EXP", "LOG", "SQRT", "SIN", "COS", "MAX", "MIN", 
		"MOD", "ABS", "FLOOR", "CEIL", "IF", "THEN", "ELSE", "INT", "FLOAT", "IDENT", 
		"LINE_COMMENT", "MULTILINE_COMENT", "WS"
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
		enterRule(_localctx, 0, RULE_definitions);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(15); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(12);
				stm();
				setState(13);
				match(T__0);
				}
				}
				setState(17); 
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
		enterRule(_localctx, 2, RULE_matrixEquation);
		try {
			setState(47);
			switch (_input.LA(1)) {
			case T__1:
				_localctx = new BirthEquationContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(19);
				match(T__1);
				setState(20);
				match(T__2);
				setState(21);
				match(IDENT);
				setState(22);
				match(T__3);
				setState(23);
				match(IDENT);
				setState(24);
				match(T__4);
				setState(25);
				match(ASSIGN);
				setState(26);
				expr(0);
				}
				break;
			case T__5:
				_localctx = new MigrationEquationContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(27);
				match(T__5);
				setState(28);
				match(T__2);
				setState(29);
				match(IDENT);
				setState(30);
				match(T__3);
				setState(31);
				match(IDENT);
				setState(32);
				match(T__4);
				setState(33);
				match(ASSIGN);
				setState(34);
				expr(0);
				}
				break;
			case T__6:
				_localctx = new DeathEquationContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(35);
				match(T__6);
				setState(36);
				match(T__2);
				setState(37);
				match(IDENT);
				setState(38);
				match(T__4);
				setState(39);
				match(ASSIGN);
				setState(40);
				expr(0);
				}
				break;
			case T__7:
				_localctx = new NondemeEquationContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(41);
				match(T__7);
				setState(42);
				match(T__2);
				setState(43);
				match(IDENT);
				setState(44);
				match(T__4);
				setState(45);
				match(ASSIGN);
				setState(46);
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
		enterRule(_localctx, 4, RULE_matrixEquations);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(52); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(49);
				matrixEquation();
				setState(50);
				match(T__0);
				}
				}
				setState(54); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__1) | (1L << T__5) | (1L << T__6) | (1L << T__7))) != 0) );
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
		enterRule(_localctx, 6, RULE_stm);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(56);
			match(IDENT);
			setState(57);
			match(ASSIGN);
			setState(58);
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
		enterRule(_localctx, 8, RULE_equation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(60);
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
		int _startState = 10;
		enterRecursionRule(_localctx, 10, RULE_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(98);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				{
				_localctx = new MinusExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(63);
				match(SUB);
				setState(64);
				expr(14);
				}
				break;
			case 2:
				{
				_localctx = new NotExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(65);
				match(NOT);
				setState(66);
				expr(13);
				}
				break;
			case 3:
				{
				_localctx = new ParenthExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(67);
				match(T__2);
				setState(68);
				expr(0);
				setState(69);
				match(T__4);
				}
				break;
			case 4:
				{
				_localctx = new CondExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(71);
				match(IF);
				setState(72);
				expr(0);
				setState(73);
				match(THEN);
				setState(74);
				expr(0);
				setState(75);
				match(ELSE);
				setState(76);
				expr(0);
				}
				break;
			case 5:
				{
				_localctx = new CallSpecialExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(78);
				((CallSpecialExprContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EXP) | (1L << LOG) | (1L << SQRT) | (1L << SIN) | (1L << COS) | (1L << ABS) | (1L << FLOOR) | (1L << CEIL))) != 0)) ) {
					((CallSpecialExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(79);
				match(T__2);
				setState(80);
				expr(0);
				setState(81);
				match(T__4);
				}
				break;
			case 6:
				{
				_localctx = new CallBinaryExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(83);
				((CallBinaryExprContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MAX) | (1L << MIN) | (1L << MOD))) != 0)) ) {
					((CallBinaryExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(84);
				match(T__2);
				setState(85);
				expr(0);
				setState(86);
				match(T__3);
				setState(87);
				expr(0);
				setState(88);
				match(T__4);
				}
				break;
			case 7:
				{
				_localctx = new VectorExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(90);
				match(IDENT);
				setState(91);
				match(T__8);
				setState(92);
				expr(0);
				setState(93);
				match(T__9);
				}
				break;
			case 8:
				{
				_localctx = new IdentExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(95);
				match(IDENT);
				}
				break;
			case 9:
				{
				_localctx = new IntExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(96);
				((IntExprContext)_localctx).val = match(INT);
				}
				break;
			case 10:
				{
				_localctx = new FloatExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(97);
				((FloatExprContext)_localctx).val = match(FLOAT);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(117);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(115);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
					case 1:
						{
						_localctx = new PowerExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(100);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(101);
						match(POW);
						setState(102);
						expr(11);
						}
						break;
					case 2:
						{
						_localctx = new ProdExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(103);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(104);
						((ProdExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==MUL || _la==DIV) ) {
							((ProdExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(105);
						expr(11);
						}
						break;
					case 3:
						{
						_localctx = new SumExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(106);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(107);
						((SumExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==ADD || _la==SUB) ) {
							((SumExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(108);
						expr(10);
						}
						break;
					case 4:
						{
						_localctx = new CmpExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(109);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(110);
						((CmpExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EQ) | (1L << LEQ) | (1L << GT) | (1L << LT) | (1L << GEQ))) != 0)) ) {
							((CmpExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(111);
						expr(9);
						}
						break;
					case 5:
						{
						_localctx = new BoolExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(112);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(113);
						((BoolExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==AND || _la==OR) ) {
							((BoolExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(114);
						expr(8);
						}
						break;
					}
					} 
				}
				setState(119);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
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
		case 5:
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3.{\4\2\t\2\4\3\t\3"+
		"\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\3\2\3\2\3\2\6\2\22\n\2\r\2\16\2\23\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3\62\n\3\3\4\3\4\3\4\6\4\67"+
		"\n\4\r\4\16\48\3\5\3\5\3\5\3\5\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7e\n\7\3\7\3\7\3\7\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\7\7v\n\7\f\7\16\7y\13\7\3\7"+
		"\2\3\f\b\2\4\6\b\n\f\2\b\4\2\33\37#%\3\2 \"\3\2\30\31\3\2\26\27\4\2\r"+
		"\r\22\25\3\2\17\20\u0087\2\21\3\2\2\2\4\61\3\2\2\2\6\66\3\2\2\2\b:\3\2"+
		"\2\2\n>\3\2\2\2\fd\3\2\2\2\16\17\5\b\5\2\17\20\7\3\2\2\20\22\3\2\2\2\21"+
		"\16\3\2\2\2\22\23\3\2\2\2\23\21\3\2\2\2\23\24\3\2\2\2\24\3\3\2\2\2\25"+
		"\26\7\4\2\2\26\27\7\5\2\2\27\30\7+\2\2\30\31\7\6\2\2\31\32\7+\2\2\32\33"+
		"\7\7\2\2\33\34\7\16\2\2\34\62\5\f\7\2\35\36\7\b\2\2\36\37\7\5\2\2\37 "+
		"\7+\2\2 !\7\6\2\2!\"\7+\2\2\"#\7\7\2\2#$\7\16\2\2$\62\5\f\7\2%&\7\t\2"+
		"\2&\'\7\5\2\2\'(\7+\2\2()\7\7\2\2)*\7\16\2\2*\62\5\f\7\2+,\7\n\2\2,-\7"+
		"\5\2\2-.\7+\2\2./\7\7\2\2/\60\7\16\2\2\60\62\5\f\7\2\61\25\3\2\2\2\61"+
		"\35\3\2\2\2\61%\3\2\2\2\61+\3\2\2\2\62\5\3\2\2\2\63\64\5\4\3\2\64\65\7"+
		"\3\2\2\65\67\3\2\2\2\66\63\3\2\2\2\678\3\2\2\28\66\3\2\2\289\3\2\2\29"+
		"\7\3\2\2\2:;\7+\2\2;<\7\16\2\2<=\5\f\7\2=\t\3\2\2\2>?\5\f\7\2?\13\3\2"+
		"\2\2@A\b\7\1\2AB\7\27\2\2Be\5\f\7\20CD\7\21\2\2De\5\f\7\17EF\7\5\2\2F"+
		"G\5\f\7\2GH\7\7\2\2He\3\2\2\2IJ\7&\2\2JK\5\f\7\2KL\7\'\2\2LM\5\f\7\2M"+
		"N\7(\2\2NO\5\f\7\2Oe\3\2\2\2PQ\t\2\2\2QR\7\5\2\2RS\5\f\7\2ST\7\7\2\2T"+
		"e\3\2\2\2UV\t\3\2\2VW\7\5\2\2WX\5\f\7\2XY\7\6\2\2YZ\5\f\7\2Z[\7\7\2\2"+
		"[e\3\2\2\2\\]\7+\2\2]^\7\13\2\2^_\5\f\7\2_`\7\f\2\2`e\3\2\2\2ae\7+\2\2"+
		"be\7)\2\2ce\7*\2\2d@\3\2\2\2dC\3\2\2\2dE\3\2\2\2dI\3\2\2\2dP\3\2\2\2d"+
		"U\3\2\2\2d\\\3\2\2\2da\3\2\2\2db\3\2\2\2dc\3\2\2\2ew\3\2\2\2fg\f\r\2\2"+
		"gh\7\32\2\2hv\5\f\7\rij\f\f\2\2jk\t\4\2\2kv\5\f\7\rlm\f\13\2\2mn\t\5\2"+
		"\2nv\5\f\7\fop\f\n\2\2pq\t\6\2\2qv\5\f\7\13rs\f\t\2\2st\t\7\2\2tv\5\f"+
		"\7\nuf\3\2\2\2ui\3\2\2\2ul\3\2\2\2uo\3\2\2\2ur\3\2\2\2vy\3\2\2\2wu\3\2"+
		"\2\2wx\3\2\2\2x\r\3\2\2\2yw\3\2\2\2\b\23\618duw";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}