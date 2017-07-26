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
		T__0=1, T__1=2, T__2=3, EQ=4, ASSIGN=5, AND=6, OR=7, NOT=8, LEQ=9, GT=10, 
		LT=11, GEQ=12, ADD=13, SUB=14, MUL=15, DIV=16, POW=17, EXP=18, LOG=19, 
		SQRT=20, SIN=21, COS=22, MAX=23, MIN=24, MOD=25, ABS=26, FLOOR=27, CEIL=28, 
		IF=29, THEN=30, ELSE=31, INT=32, FLOAT=33, IDENT=34, LINE_COMMENT=35, 
		MULTILINE_COMENT=36, WS=37;
	public static final int
		RULE_stm = 0, RULE_equation = 1, RULE_expr = 2;
	public static final String[] ruleNames = {
		"stm", "equation", "expr"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'('", "')'", "','", "'=='", "'='", "'and'", "'or'", "'not'", "'!>'", 
		"'>'", "'!>='", "'>='", "'+'", "'-'", "'*'", "'/'", "'^'", "'exp'", "'log'", 
		"'sqrt'", "'sin'", "'cos'", "'max'", "'min'", "'mod'", "'abs'", "'floor'", 
		"'ceil'", "'if'", "'then'", "'else'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, "EQ", "ASSIGN", "AND", "OR", "NOT", "LEQ", "GT", 
		"LT", "GEQ", "ADD", "SUB", "MUL", "DIV", "POW", "EXP", "LOG", "SQRT", 
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
			setState(43);
			switch (_input.LA(1)) {
			case SUB:
				{
				_localctx = new MinusExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(13);
				match(SUB);
				setState(14);
				expr(13);
				}
				break;
			case NOT:
				{
				_localctx = new NotExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(15);
				match(NOT);
				setState(16);
				expr(12);
				}
				break;
			case T__0:
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
			case IF:
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
			case EXP:
			case LOG:
			case SQRT:
			case SIN:
			case COS:
			case ABS:
			case FLOOR:
			case CEIL:
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
			case MAX:
			case MIN:
			case MOD:
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
			case IDENT:
				{
				_localctx = new IdentExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(40);
				match(IDENT);
				}
				break;
			case INT:
				{
				_localctx = new IntExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(41);
				((IntExprContext)_localctx).val = match(INT);
				}
				break;
			case FLOAT:
				{
				_localctx = new FloatExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(42);
				((FloatExprContext)_localctx).val = match(FLOAT);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(62);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(60);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
					case 1:
						{
						_localctx = new PowerExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(45);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(46);
						match(POW);
						setState(47);
						expr(10);
						}
						break;
					case 2:
						{
						_localctx = new ProdExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(48);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(49);
						((ProdExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==MUL || _la==DIV) ) {
							((ProdExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(50);
						expr(10);
						}
						break;
					case 3:
						{
						_localctx = new SumExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(51);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(52);
						((SumExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==ADD || _la==SUB) ) {
							((SumExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(53);
						expr(9);
						}
						break;
					case 4:
						{
						_localctx = new CmpExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(54);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(55);
						((CmpExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EQ) | (1L << LEQ) | (1L << GT) | (1L << LT) | (1L << GEQ))) != 0)) ) {
							((CmpExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(56);
						expr(8);
						}
						break;
					case 5:
						{
						_localctx = new BoolExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(57);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(58);
						((BoolExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==AND || _la==OR) ) {
							((BoolExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(59);
						expr(7);
						}
						break;
					}
					} 
				}
				setState(64);
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
			return precpred(_ctx, 10);
		case 1:
			return precpred(_ctx, 9);
		case 2:
			return precpred(_ctx, 8);
		case 3:
			return precpred(_ctx, 7);
		case 4:
			return precpred(_ctx, 6);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\'D\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\3\2\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\5\4.\n\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\7\4?\n\4\f\4\16\4B\13\4\3\4\2\3\6\5\2\4\6\2\b\4\2\24"+
		"\30\34\36\3\2\31\33\3\2\21\22\3\2\17\20\4\2\6\6\13\16\3\2\b\tM\2\b\3\2"+
		"\2\2\4\f\3\2\2\2\6-\3\2\2\2\b\t\7$\2\2\t\n\7\7\2\2\n\13\5\6\4\2\13\3\3"+
		"\2\2\2\f\r\5\6\4\2\r\5\3\2\2\2\16\17\b\4\1\2\17\20\7\20\2\2\20.\5\6\4"+
		"\17\21\22\7\n\2\2\22.\5\6\4\16\23\24\7\3\2\2\24\25\5\6\4\2\25\26\7\4\2"+
		"\2\26.\3\2\2\2\27\30\7\37\2\2\30\31\5\6\4\2\31\32\7 \2\2\32\33\5\6\4\2"+
		"\33\34\7!\2\2\34\35\5\6\4\2\35.\3\2\2\2\36\37\t\2\2\2\37 \7\3\2\2 !\5"+
		"\6\4\2!\"\7\4\2\2\".\3\2\2\2#$\t\3\2\2$%\7\3\2\2%&\5\6\4\2&\'\7\5\2\2"+
		"\'(\5\6\4\2()\7\4\2\2).\3\2\2\2*.\7$\2\2+.\7\"\2\2,.\7#\2\2-\16\3\2\2"+
		"\2-\21\3\2\2\2-\23\3\2\2\2-\27\3\2\2\2-\36\3\2\2\2-#\3\2\2\2-*\3\2\2\2"+
		"-+\3\2\2\2-,\3\2\2\2.@\3\2\2\2/\60\f\f\2\2\60\61\7\23\2\2\61?\5\6\4\f"+
		"\62\63\f\13\2\2\63\64\t\4\2\2\64?\5\6\4\f\65\66\f\n\2\2\66\67\t\5\2\2"+
		"\67?\5\6\4\1389\f\t\2\29:\t\6\2\2:?\5\6\4\n;<\f\b\2\2<=\t\7\2\2=?\5\6"+
		"\4\t>/\3\2\2\2>\62\3\2\2\2>\65\3\2\2\2>8\3\2\2\2>;\3\2\2\2?B\3\2\2\2@"+
		">\3\2\2\2@A\3\2\2\2A\7\3\2\2\2B@\3\2\2\2\5->@";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}