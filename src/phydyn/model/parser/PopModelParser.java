// Generated from /home/igor/z/imperial/eclipsews/phydyndev/src/phydyn/model/parser/PopModel.g4 by ANTLR 4.5.2

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
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, EQ=6, ASSIGN=7, AND=8, OR=9, NOT=10, 
		LEQ=11, GT=12, LT=13, GEQ=14, ADD=15, SUB=16, MUL=17, DIV=18, POW=19, 
		EXP=20, LOG=21, SQRT=22, SIN=23, COS=24, MAX=25, MIN=26, MOD=27, ABS=28, 
		FLOOR=29, CEIL=30, IF=31, THEN=32, ELSE=33, INT=34, FLOAT=35, IDENT=36, 
		LINE_COMMENT=37, MULTILINE_COMENT=38, WS=39;
	public static final int
		RULE_stm = 0, RULE_equation = 1, RULE_expr = 2;
	public static final String[] ruleNames = {
		"stm", "equation", "expr"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'('", "')'", "','", "'['", "']'", "'=='", "'='", "'and'", "'or'", 
		"'not'", "'!>'", "'>'", "'!>='", "'>='", "'+'", "'-'", "'*'", "'/'", "'^'", 
		"'exp'", "'log'", "'sqrt'", "'sin'", "'cos'", "'max'", "'min'", "'mod'", 
		"'abs'", "'floor'", "'ceil'", "'if'", "'then'", "'else'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, "EQ", "ASSIGN", "AND", "OR", "NOT", 
		"LEQ", "GT", "LT", "GEQ", "ADD", "SUB", "MUL", "DIV", "POW", "EXP", "LOG", 
		"SQRT", "SIN", "COS", "MAX", "MIN", "MOD", "ABS", "FLOOR", "CEIL", "IF", 
		"THEN", "ELSE", "INT", "FLOAT", "IDENT", "LINE_COMMENT", "MULTILINE_COMENT", 
		"WS"
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
		enterRule(_localctx, 0, RULE_stm);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(6);
			match(IDENT);
			setState(7);
			match(ASSIGN);
			setState(8);
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
		enterRule(_localctx, 2, RULE_equation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(10);
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
		int _startState = 4;
		enterRecursionRule(_localctx, 4, RULE_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(48);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				_localctx = new MinusExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(13);
				match(SUB);
				setState(14);
				expr(14);
				}
				break;
			case 2:
				{
				_localctx = new NotExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(15);
				match(NOT);
				setState(16);
				expr(13);
				}
				break;
			case 3:
				{
				_localctx = new ParenthExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(17);
				match(T__0);
				setState(18);
				expr(0);
				setState(19);
				match(T__1);
				}
				break;
			case 4:
				{
				_localctx = new CondExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(21);
				match(IF);
				setState(22);
				expr(0);
				setState(23);
				match(THEN);
				setState(24);
				expr(0);
				setState(25);
				match(ELSE);
				setState(26);
				expr(0);
				}
				break;
			case 5:
				{
				_localctx = new CallSpecialExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(28);
				((CallSpecialExprContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EXP) | (1L << LOG) | (1L << SQRT) | (1L << SIN) | (1L << COS) | (1L << ABS) | (1L << FLOOR) | (1L << CEIL))) != 0)) ) {
					((CallSpecialExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(29);
				match(T__0);
				setState(30);
				expr(0);
				setState(31);
				match(T__1);
				}
				break;
			case 6:
				{
				_localctx = new CallBinaryExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(33);
				((CallBinaryExprContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MAX) | (1L << MIN) | (1L << MOD))) != 0)) ) {
					((CallBinaryExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(34);
				match(T__0);
				setState(35);
				expr(0);
				setState(36);
				match(T__2);
				setState(37);
				expr(0);
				setState(38);
				match(T__1);
				}
				break;
			case 7:
				{
				_localctx = new VectorExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(40);
				match(IDENT);
				setState(41);
				match(T__3);
				setState(42);
				expr(0);
				setState(43);
				match(T__4);
				}
				break;
			case 8:
				{
				_localctx = new IdentExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(45);
				match(IDENT);
				}
				break;
			case 9:
				{
				_localctx = new IntExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(46);
				((IntExprContext)_localctx).val = match(INT);
				}
				break;
			case 10:
				{
				_localctx = new FloatExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(47);
				((FloatExprContext)_localctx).val = match(FLOAT);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(67);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(65);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
					case 1:
						{
						_localctx = new PowerExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(50);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(51);
						match(POW);
						setState(52);
						expr(11);
						}
						break;
					case 2:
						{
						_localctx = new ProdExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(53);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(54);
						((ProdExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==MUL || _la==DIV) ) {
							((ProdExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(55);
						expr(11);
						}
						break;
					case 3:
						{
						_localctx = new SumExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(56);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(57);
						((SumExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==ADD || _la==SUB) ) {
							((SumExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(58);
						expr(10);
						}
						break;
					case 4:
						{
						_localctx = new CmpExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(59);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(60);
						((CmpExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EQ) | (1L << LEQ) | (1L << GT) | (1L << LT) | (1L << GEQ))) != 0)) ) {
							((CmpExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(61);
						expr(9);
						}
						break;
					case 5:
						{
						_localctx = new BoolExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(62);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(63);
						((BoolExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==AND || _la==OR) ) {
							((BoolExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(64);
						expr(8);
						}
						break;
					}
					} 
				}
				setState(69);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
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
		case 2:
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3)I\4\2\t\2\4\3\t\3"+
		"\4\4\t\4\3\2\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4\63\n\4\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\7\4D\n\4\f\4\16\4G\13\4\3\4\2\3"+
		"\6\5\2\4\6\2\b\4\2\26\32\36 \3\2\33\35\3\2\23\24\3\2\21\22\4\2\b\b\r\20"+
		"\3\2\n\13S\2\b\3\2\2\2\4\f\3\2\2\2\6\62\3\2\2\2\b\t\7&\2\2\t\n\7\t\2\2"+
		"\n\13\5\6\4\2\13\3\3\2\2\2\f\r\5\6\4\2\r\5\3\2\2\2\16\17\b\4\1\2\17\20"+
		"\7\22\2\2\20\63\5\6\4\20\21\22\7\f\2\2\22\63\5\6\4\17\23\24\7\3\2\2\24"+
		"\25\5\6\4\2\25\26\7\4\2\2\26\63\3\2\2\2\27\30\7!\2\2\30\31\5\6\4\2\31"+
		"\32\7\"\2\2\32\33\5\6\4\2\33\34\7#\2\2\34\35\5\6\4\2\35\63\3\2\2\2\36"+
		"\37\t\2\2\2\37 \7\3\2\2 !\5\6\4\2!\"\7\4\2\2\"\63\3\2\2\2#$\t\3\2\2$%"+
		"\7\3\2\2%&\5\6\4\2&\'\7\5\2\2\'(\5\6\4\2()\7\4\2\2)\63\3\2\2\2*+\7&\2"+
		"\2+,\7\6\2\2,-\5\6\4\2-.\7\7\2\2.\63\3\2\2\2/\63\7&\2\2\60\63\7$\2\2\61"+
		"\63\7%\2\2\62\16\3\2\2\2\62\21\3\2\2\2\62\23\3\2\2\2\62\27\3\2\2\2\62"+
		"\36\3\2\2\2\62#\3\2\2\2\62*\3\2\2\2\62/\3\2\2\2\62\60\3\2\2\2\62\61\3"+
		"\2\2\2\63E\3\2\2\2\64\65\f\r\2\2\65\66\7\25\2\2\66D\5\6\4\r\678\f\f\2"+
		"\289\t\4\2\29D\5\6\4\r:;\f\13\2\2;<\t\5\2\2<D\5\6\4\f=>\f\n\2\2>?\t\6"+
		"\2\2?D\5\6\4\13@A\f\t\2\2AB\t\7\2\2BD\5\6\4\nC\64\3\2\2\2C\67\3\2\2\2"+
		"C:\3\2\2\2C=\3\2\2\2C@\3\2\2\2DG\3\2\2\2EC\3\2\2\2EF\3\2\2\2F\7\3\2\2"+
		"\2GE\3\2\2\2\5\62CE";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}