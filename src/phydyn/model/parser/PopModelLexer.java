// Generated from PopModel.g4 by ANTLR 4.5.2

package phydyn.model.parser;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class PopModelLexer extends Lexer {
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
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"T__9", "EQ", "ASSIGN", "AND", "OR", "NOT", "LEQ", "GT", "LT", "GEQ", 
		"ADD", "SUB", "MUL", "DIV", "POW", "EXP", "LOG", "SQRT", "SIN", "COS", 
		"MAX", "MIN", "MOD", "ABS", "FLOOR", "CEIL", "IF", "THEN", "ELSE", "INT", 
		"FLOAT", "IDENT", "DIGIT", "ID_LETTER", "LINE_COMMENT", "MULTILINE_COMENT", 
		"WS"
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


	public PopModelLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "PopModel.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2.\u012a\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7"+
		"\3\b\3\b\3\t\3\t\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\f\3\r\3\r\3\16\3"+
		"\16\3\16\3\16\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\22\3"+
		"\22\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3"+
		"\30\3\30\3\31\3\31\3\32\3\32\3\32\3\32\3\33\3\33\3\33\3\33\3\34\3\34\3"+
		"\34\3\34\3\34\3\35\3\35\3\35\3\35\3\36\3\36\3\36\3\36\3\37\3\37\3\37\3"+
		"\37\3 \3 \3 \3 \3!\3!\3!\3!\3\"\3\"\3\"\3\"\3#\3#\3#\3#\3#\3#\3$\3$\3"+
		"$\3$\3$\3%\3%\3%\3&\3&\3&\3&\3&\3\'\3\'\3\'\3\'\3\'\3(\6(\u00da\n(\r("+
		"\16(\u00db\3)\6)\u00df\n)\r)\16)\u00e0\3)\3)\7)\u00e5\n)\f)\16)\u00e8"+
		"\13)\3)\3)\5)\u00ec\n)\3)\6)\u00ef\n)\r)\16)\u00f0\5)\u00f3\n)\3)\3)\6"+
		")\u00f7\n)\r)\16)\u00f8\5)\u00fb\n)\3*\3*\3*\7*\u0100\n*\f*\16*\u0103"+
		"\13*\3+\3+\3,\3,\3-\3-\3-\3-\7-\u010d\n-\f-\16-\u0110\13-\3-\3-\3-\3-"+
		"\3.\3.\3.\3.\7.\u011a\n.\f.\16.\u011d\13.\3.\3.\3.\3.\3.\3/\6/\u0125\n"+
		"/\r/\16/\u0126\3/\3/\4\u010e\u011b\2\60\3\3\5\4\7\5\t\6\13\7\r\b\17\t"+
		"\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27"+
		"-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U\2"+
		"W\2Y,[-].\3\2\6\4\2GGgg\3\2\62;\5\2C\\aac|\5\2\13\f\17\17\"\"\u0134\2"+
		"\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2"+
		"\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2"+
		"\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2"+
		"\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2"+
		"\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2"+
		"\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2"+
		"\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2Y"+
		"\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\3_\3\2\2\2\5a\3\2\2\2\7c\3\2\2\2\te\3\2"+
		"\2\2\13g\3\2\2\2\ri\3\2\2\2\17k\3\2\2\2\21m\3\2\2\2\23q\3\2\2\2\25s\3"+
		"\2\2\2\27u\3\2\2\2\31x\3\2\2\2\33z\3\2\2\2\35~\3\2\2\2\37\u0081\3\2\2"+
		"\2!\u0085\3\2\2\2#\u0088\3\2\2\2%\u008a\3\2\2\2\'\u008e\3\2\2\2)\u0091"+
		"\3\2\2\2+\u0093\3\2\2\2-\u0095\3\2\2\2/\u0097\3\2\2\2\61\u0099\3\2\2\2"+
		"\63\u009b\3\2\2\2\65\u009f\3\2\2\2\67\u00a3\3\2\2\29\u00a8\3\2\2\2;\u00ac"+
		"\3\2\2\2=\u00b0\3\2\2\2?\u00b4\3\2\2\2A\u00b8\3\2\2\2C\u00bc\3\2\2\2E"+
		"\u00c0\3\2\2\2G\u00c6\3\2\2\2I\u00cb\3\2\2\2K\u00ce\3\2\2\2M\u00d3\3\2"+
		"\2\2O\u00d9\3\2\2\2Q\u00fa\3\2\2\2S\u00fc\3\2\2\2U\u0104\3\2\2\2W\u0106"+
		"\3\2\2\2Y\u0108\3\2\2\2[\u0115\3\2\2\2]\u0124\3\2\2\2_`\7=\2\2`\4\3\2"+
		"\2\2ab\7H\2\2b\6\3\2\2\2cd\7*\2\2d\b\3\2\2\2ef\7.\2\2f\n\3\2\2\2gh\7+"+
		"\2\2h\f\3\2\2\2ij\7I\2\2j\16\3\2\2\2kl\7F\2\2l\20\3\2\2\2mn\7f\2\2no\7"+
		"q\2\2op\7v\2\2p\22\3\2\2\2qr\7]\2\2r\24\3\2\2\2st\7_\2\2t\26\3\2\2\2u"+
		"v\7?\2\2vw\7?\2\2w\30\3\2\2\2xy\7?\2\2y\32\3\2\2\2z{\7c\2\2{|\7p\2\2|"+
		"}\7f\2\2}\34\3\2\2\2~\177\7q\2\2\177\u0080\7t\2\2\u0080\36\3\2\2\2\u0081"+
		"\u0082\7p\2\2\u0082\u0083\7q\2\2\u0083\u0084\7v\2\2\u0084 \3\2\2\2\u0085"+
		"\u0086\7#\2\2\u0086\u0087\7@\2\2\u0087\"\3\2\2\2\u0088\u0089\7@\2\2\u0089"+
		"$\3\2\2\2\u008a\u008b\7#\2\2\u008b\u008c\7@\2\2\u008c\u008d\7?\2\2\u008d"+
		"&\3\2\2\2\u008e\u008f\7@\2\2\u008f\u0090\7?\2\2\u0090(\3\2\2\2\u0091\u0092"+
		"\7-\2\2\u0092*\3\2\2\2\u0093\u0094\7/\2\2\u0094,\3\2\2\2\u0095\u0096\7"+
		",\2\2\u0096.\3\2\2\2\u0097\u0098\7\61\2\2\u0098\60\3\2\2\2\u0099\u009a"+
		"\7`\2\2\u009a\62\3\2\2\2\u009b\u009c\7g\2\2\u009c\u009d\7z\2\2\u009d\u009e"+
		"\7r\2\2\u009e\64\3\2\2\2\u009f\u00a0\7n\2\2\u00a0\u00a1\7q\2\2\u00a1\u00a2"+
		"\7i\2\2\u00a2\66\3\2\2\2\u00a3\u00a4\7u\2\2\u00a4\u00a5\7s\2\2\u00a5\u00a6"+
		"\7t\2\2\u00a6\u00a7\7v\2\2\u00a78\3\2\2\2\u00a8\u00a9\7u\2\2\u00a9\u00aa"+
		"\7k\2\2\u00aa\u00ab\7p\2\2\u00ab:\3\2\2\2\u00ac\u00ad\7e\2\2\u00ad\u00ae"+
		"\7q\2\2\u00ae\u00af\7u\2\2\u00af<\3\2\2\2\u00b0\u00b1\7o\2\2\u00b1\u00b2"+
		"\7c\2\2\u00b2\u00b3\7z\2\2\u00b3>\3\2\2\2\u00b4\u00b5\7o\2\2\u00b5\u00b6"+
		"\7k\2\2\u00b6\u00b7\7p\2\2\u00b7@\3\2\2\2\u00b8\u00b9\7o\2\2\u00b9\u00ba"+
		"\7q\2\2\u00ba\u00bb\7f\2\2\u00bbB\3\2\2\2\u00bc\u00bd\7c\2\2\u00bd\u00be"+
		"\7d\2\2\u00be\u00bf\7u\2\2\u00bfD\3\2\2\2\u00c0\u00c1\7h\2\2\u00c1\u00c2"+
		"\7n\2\2\u00c2\u00c3\7q\2\2\u00c3\u00c4\7q\2\2\u00c4\u00c5\7t\2\2\u00c5"+
		"F\3\2\2\2\u00c6\u00c7\7e\2\2\u00c7\u00c8\7g\2\2\u00c8\u00c9\7k\2\2\u00c9"+
		"\u00ca\7n\2\2\u00caH\3\2\2\2\u00cb\u00cc\7k\2\2\u00cc\u00cd\7h\2\2\u00cd"+
		"J\3\2\2\2\u00ce\u00cf\7v\2\2\u00cf\u00d0\7j\2\2\u00d0\u00d1\7g\2\2\u00d1"+
		"\u00d2\7p\2\2\u00d2L\3\2\2\2\u00d3\u00d4\7g\2\2\u00d4\u00d5\7n\2\2\u00d5"+
		"\u00d6\7u\2\2\u00d6\u00d7\7g\2\2\u00d7N\3\2\2\2\u00d8\u00da\5U+\2\u00d9"+
		"\u00d8\3\2\2\2\u00da\u00db\3\2\2\2\u00db\u00d9\3\2\2\2\u00db\u00dc\3\2"+
		"\2\2\u00dcP\3\2\2\2\u00dd\u00df\5U+\2\u00de\u00dd\3\2\2\2\u00df\u00e0"+
		"\3\2\2\2\u00e0\u00de\3\2\2\2\u00e0\u00e1\3\2\2\2\u00e1\u00e2\3\2\2\2\u00e2"+
		"\u00e6\7\60\2\2\u00e3\u00e5\5U+\2\u00e4\u00e3\3\2\2\2\u00e5\u00e8\3\2"+
		"\2\2\u00e6\u00e4\3\2\2\2\u00e6\u00e7\3\2\2\2\u00e7\u00f2\3\2\2\2\u00e8"+
		"\u00e6\3\2\2\2\u00e9\u00eb\t\2\2\2\u00ea\u00ec\7/\2\2\u00eb\u00ea\3\2"+
		"\2\2\u00eb\u00ec\3\2\2\2\u00ec\u00ee\3\2\2\2\u00ed\u00ef\5U+\2\u00ee\u00ed"+
		"\3\2\2\2\u00ef\u00f0\3\2\2\2\u00f0\u00ee\3\2\2\2\u00f0\u00f1\3\2\2\2\u00f1"+
		"\u00f3\3\2\2\2\u00f2\u00e9\3\2\2\2\u00f2\u00f3\3\2\2\2\u00f3\u00fb\3\2"+
		"\2\2\u00f4\u00f6\7\60\2\2\u00f5\u00f7\5U+\2\u00f6\u00f5\3\2\2\2\u00f7"+
		"\u00f8\3\2\2\2\u00f8\u00f6\3\2\2\2\u00f8\u00f9\3\2\2\2\u00f9\u00fb\3\2"+
		"\2\2\u00fa\u00de\3\2\2\2\u00fa\u00f4\3\2\2\2\u00fbR\3\2\2\2\u00fc\u0101"+
		"\5W,\2\u00fd\u0100\5W,\2\u00fe\u0100\5U+\2\u00ff\u00fd\3\2\2\2\u00ff\u00fe"+
		"\3\2\2\2\u0100\u0103\3\2\2\2\u0101\u00ff\3\2\2\2\u0101\u0102\3\2\2\2\u0102"+
		"T\3\2\2\2\u0103\u0101\3\2\2\2\u0104\u0105\t\3\2\2\u0105V\3\2\2\2\u0106"+
		"\u0107\t\4\2\2\u0107X\3\2\2\2\u0108\u0109\7\61\2\2\u0109\u010a\7\61\2"+
		"\2\u010a\u010e\3\2\2\2\u010b\u010d\13\2\2\2\u010c\u010b\3\2\2\2\u010d"+
		"\u0110\3\2\2\2\u010e\u010f\3\2\2\2\u010e\u010c\3\2\2\2\u010f\u0111\3\2"+
		"\2\2\u0110\u010e\3\2\2\2\u0111\u0112\7\f\2\2\u0112\u0113\3\2\2\2\u0113"+
		"\u0114\b-\2\2\u0114Z\3\2\2\2\u0115\u0116\7\61\2\2\u0116\u0117\7,\2\2\u0117"+
		"\u011b\3\2\2\2\u0118\u011a\13\2\2\2\u0119\u0118\3\2\2\2\u011a\u011d\3"+
		"\2\2\2\u011b\u011c\3\2\2\2\u011b\u0119\3\2\2\2\u011c\u011e\3\2\2\2\u011d"+
		"\u011b\3\2\2\2\u011e\u011f\7,\2\2\u011f\u0120\7\61\2\2\u0120\u0121\3\2"+
		"\2\2\u0121\u0122\b.\2\2\u0122\\\3\2\2\2\u0123\u0125\t\5\2\2\u0124\u0123"+
		"\3\2\2\2\u0125\u0126\3\2\2\2\u0126\u0124\3\2\2\2\u0126\u0127\3\2\2\2\u0127"+
		"\u0128\3\2\2\2\u0128\u0129\b/\2\2\u0129^\3\2\2\2\20\2\u00db\u00e0\u00e6"+
		"\u00eb\u00f0\u00f2\u00f8\u00fa\u00ff\u0101\u010e\u011b\u0126\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}