// Generated from PopModel.g4 by ANTLR 4.7.1

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
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "EQ", "ASSIGN", 
		"AND", "OR", "NOT", "LEQ", "GT", "LT", "GEQ", "ADD", "SUB", "MUL", "DIV", 
		"POW", "EXP", "LOG", "SQRT", "SIN", "COS", "MAX", "MIN", "MOD", "ABS", 
		"FLOOR", "CEIL", "IF", "THEN", "ELSE", "INT", "FLOAT", "IDENT", "DIGIT", 
		"ID_LETTER", "LINE_COMMENT", "MULTILINE_COMENT", "WS"
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
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\64\u0152\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64"+
		"\t\64\4\65\t\65\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t"+
		"\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3\17\3\17\3\17\3"+
		"\20\3\20\3\21\3\21\3\22\3\22\3\22\3\23\3\23\3\24\3\24\3\24\3\24\3\25\3"+
		"\25\3\25\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\30\3\30\3\31\3\31\3\31\3"+
		"\31\3\32\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3"+
		" \3 \3 \3 \3!\3!\3!\3!\3\"\3\"\3\"\3\"\3\"\3#\3#\3#\3#\3$\3$\3$\3$\3%"+
		"\3%\3%\3%\3&\3&\3&\3&\3\'\3\'\3\'\3\'\3(\3(\3(\3(\3)\3)\3)\3)\3)\3)\3"+
		"*\3*\3*\3*\3*\3+\3+\3+\3,\3,\3,\3,\3,\3-\3-\3-\3-\3-\3.\6.\u0102\n.\r"+
		".\16.\u0103\3/\6/\u0107\n/\r/\16/\u0108\3/\3/\7/\u010d\n/\f/\16/\u0110"+
		"\13/\3/\3/\5/\u0114\n/\3/\6/\u0117\n/\r/\16/\u0118\5/\u011b\n/\3/\3/\6"+
		"/\u011f\n/\r/\16/\u0120\5/\u0123\n/\3\60\3\60\3\60\7\60\u0128\n\60\f\60"+
		"\16\60\u012b\13\60\3\61\3\61\3\62\3\62\3\63\3\63\3\63\3\63\7\63\u0135"+
		"\n\63\f\63\16\63\u0138\13\63\3\63\3\63\3\63\3\63\3\64\3\64\3\64\3\64\7"+
		"\64\u0142\n\64\f\64\16\64\u0145\13\64\3\64\3\64\3\64\3\64\3\64\3\65\6"+
		"\65\u014d\n\65\r\65\16\65\u014e\3\65\3\65\4\u0136\u0143\2\66\3\3\5\4\7"+
		"\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22"+
		"#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C"+
		"#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a\2c\2e\62g\63i\64\3\2\6\4\2GGgg\3\2"+
		"\62;\5\2C\\aac|\5\2\13\f\17\17\"\"\2\u015c\2\3\3\2\2\2\2\5\3\2\2\2\2\7"+
		"\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2"+
		"\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2"+
		"\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2"+
		"\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2"+
		"\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2"+
		"\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M"+
		"\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2"+
		"\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2"+
		"\3k\3\2\2\2\5p\3\2\2\2\7t\3\2\2\2\tz\3\2\2\2\13|\3\2\2\2\r~\3\2\2\2\17"+
		"\u0080\3\2\2\2\21\u0082\3\2\2\2\23\u008b\3\2\2\2\25\u008d\3\2\2\2\27\u008f"+
		"\3\2\2\2\31\u0091\3\2\2\2\33\u0093\3\2\2\2\35\u0095\3\2\2\2\37\u0099\3"+
		"\2\2\2!\u009b\3\2\2\2#\u009d\3\2\2\2%\u00a0\3\2\2\2\'\u00a2\3\2\2\2)\u00a6"+
		"\3\2\2\2+\u00a9\3\2\2\2-\u00ad\3\2\2\2/\u00b0\3\2\2\2\61\u00b2\3\2\2\2"+
		"\63\u00b6\3\2\2\2\65\u00b9\3\2\2\2\67\u00bb\3\2\2\29\u00bd\3\2\2\2;\u00bf"+
		"\3\2\2\2=\u00c1\3\2\2\2?\u00c3\3\2\2\2A\u00c7\3\2\2\2C\u00cb\3\2\2\2E"+
		"\u00d0\3\2\2\2G\u00d4\3\2\2\2I\u00d8\3\2\2\2K\u00dc\3\2\2\2M\u00e0\3\2"+
		"\2\2O\u00e4\3\2\2\2Q\u00e8\3\2\2\2S\u00ee\3\2\2\2U\u00f3\3\2\2\2W\u00f6"+
		"\3\2\2\2Y\u00fb\3\2\2\2[\u0101\3\2\2\2]\u0122\3\2\2\2_\u0124\3\2\2\2a"+
		"\u012c\3\2\2\2c\u012e\3\2\2\2e\u0130\3\2\2\2g\u013d\3\2\2\2i\u014c\3\2"+
		"\2\2kl\7/\2\2lm\7k\2\2mn\7p\2\2no\7h\2\2o\4\3\2\2\2pq\7k\2\2qr\7p\2\2"+
		"rs\7h\2\2s\6\3\2\2\2tu\7r\2\2uv\7t\2\2vw\7k\2\2wx\7q\2\2xy\7t\2\2y\b\3"+
		"\2\2\2z{\7*\2\2{\n\3\2\2\2|}\7.\2\2}\f\3\2\2\2~\177\7+\2\2\177\16\3\2"+
		"\2\2\u0080\u0081\7=\2\2\u0081\20\3\2\2\2\u0082\u0083\7q\2\2\u0083\u0084"+
		"\7r\2\2\u0084\u0085\7g\2\2\u0085\u0086\7t\2\2\u0086\u0087\7c\2\2\u0087"+
		"\u0088\7v\2\2\u0088\u0089\7q\2\2\u0089\u008a\7t\2\2\u008a\22\3\2\2\2\u008b"+
		"\u008c\7}\2\2\u008c\24\3\2\2\2\u008d\u008e\7\177\2\2\u008e\26\3\2\2\2"+
		"\u008f\u0090\7H\2\2\u0090\30\3\2\2\2\u0091\u0092\7I\2\2\u0092\32\3\2\2"+
		"\2\u0093\u0094\7F\2\2\u0094\34\3\2\2\2\u0095\u0096\7f\2\2\u0096\u0097"+
		"\7q\2\2\u0097\u0098\7v\2\2\u0098\36\3\2\2\2\u0099\u009a\7]\2\2\u009a "+
		"\3\2\2\2\u009b\u009c\7_\2\2\u009c\"\3\2\2\2\u009d\u009e\7?\2\2\u009e\u009f"+
		"\7?\2\2\u009f$\3\2\2\2\u00a0\u00a1\7?\2\2\u00a1&\3\2\2\2\u00a2\u00a3\7"+
		"c\2\2\u00a3\u00a4\7p\2\2\u00a4\u00a5\7f\2\2\u00a5(\3\2\2\2\u00a6\u00a7"+
		"\7q\2\2\u00a7\u00a8\7t\2\2\u00a8*\3\2\2\2\u00a9\u00aa\7p\2\2\u00aa\u00ab"+
		"\7q\2\2\u00ab\u00ac\7v\2\2\u00ac,\3\2\2\2\u00ad\u00ae\7#\2\2\u00ae\u00af"+
		"\7@\2\2\u00af.\3\2\2\2\u00b0\u00b1\7@\2\2\u00b1\60\3\2\2\2\u00b2\u00b3"+
		"\7#\2\2\u00b3\u00b4\7@\2\2\u00b4\u00b5\7?\2\2\u00b5\62\3\2\2\2\u00b6\u00b7"+
		"\7@\2\2\u00b7\u00b8\7?\2\2\u00b8\64\3\2\2\2\u00b9\u00ba\7-\2\2\u00ba\66"+
		"\3\2\2\2\u00bb\u00bc\7/\2\2\u00bc8\3\2\2\2\u00bd\u00be\7,\2\2\u00be:\3"+
		"\2\2\2\u00bf\u00c0\7\61\2\2\u00c0<\3\2\2\2\u00c1\u00c2\7`\2\2\u00c2>\3"+
		"\2\2\2\u00c3\u00c4\7g\2\2\u00c4\u00c5\7z\2\2\u00c5\u00c6\7r\2\2\u00c6"+
		"@\3\2\2\2\u00c7\u00c8\7n\2\2\u00c8\u00c9\7q\2\2\u00c9\u00ca\7i\2\2\u00ca"+
		"B\3\2\2\2\u00cb\u00cc\7u\2\2\u00cc\u00cd\7s\2\2\u00cd\u00ce\7t\2\2\u00ce"+
		"\u00cf\7v\2\2\u00cfD\3\2\2\2\u00d0\u00d1\7u\2\2\u00d1\u00d2\7k\2\2\u00d2"+
		"\u00d3\7p\2\2\u00d3F\3\2\2\2\u00d4\u00d5\7e\2\2\u00d5\u00d6\7q\2\2\u00d6"+
		"\u00d7\7u\2\2\u00d7H\3\2\2\2\u00d8\u00d9\7o\2\2\u00d9\u00da\7c\2\2\u00da"+
		"\u00db\7z\2\2\u00dbJ\3\2\2\2\u00dc\u00dd\7o\2\2\u00dd\u00de\7k\2\2\u00de"+
		"\u00df\7p\2\2\u00dfL\3\2\2\2\u00e0\u00e1\7o\2\2\u00e1\u00e2\7q\2\2\u00e2"+
		"\u00e3\7f\2\2\u00e3N\3\2\2\2\u00e4\u00e5\7c\2\2\u00e5\u00e6\7d\2\2\u00e6"+
		"\u00e7\7u\2\2\u00e7P\3\2\2\2\u00e8\u00e9\7h\2\2\u00e9\u00ea\7n\2\2\u00ea"+
		"\u00eb\7q\2\2\u00eb\u00ec\7q\2\2\u00ec\u00ed\7t\2\2\u00edR\3\2\2\2\u00ee"+
		"\u00ef\7e\2\2\u00ef\u00f0\7g\2\2\u00f0\u00f1\7k\2\2\u00f1\u00f2\7n\2\2"+
		"\u00f2T\3\2\2\2\u00f3\u00f4\7k\2\2\u00f4\u00f5\7h\2\2\u00f5V\3\2\2\2\u00f6"+
		"\u00f7\7v\2\2\u00f7\u00f8\7j\2\2\u00f8\u00f9\7g\2\2\u00f9\u00fa\7p\2\2"+
		"\u00faX\3\2\2\2\u00fb\u00fc\7g\2\2\u00fc\u00fd\7n\2\2\u00fd\u00fe\7u\2"+
		"\2\u00fe\u00ff\7g\2\2\u00ffZ\3\2\2\2\u0100\u0102\5a\61\2\u0101\u0100\3"+
		"\2\2\2\u0102\u0103\3\2\2\2\u0103\u0101\3\2\2\2\u0103\u0104\3\2\2\2\u0104"+
		"\\\3\2\2\2\u0105\u0107\5a\61\2\u0106\u0105\3\2\2\2\u0107\u0108\3\2\2\2"+
		"\u0108\u0106\3\2\2\2\u0108\u0109\3\2\2\2\u0109\u010a\3\2\2\2\u010a\u010e"+
		"\7\60\2\2\u010b\u010d\5a\61\2\u010c\u010b\3\2\2\2\u010d\u0110\3\2\2\2"+
		"\u010e\u010c\3\2\2\2\u010e\u010f\3\2\2\2\u010f\u011a\3\2\2\2\u0110\u010e"+
		"\3\2\2\2\u0111\u0113\t\2\2\2\u0112\u0114\7/\2\2\u0113\u0112\3\2\2\2\u0113"+
		"\u0114\3\2\2\2\u0114\u0116\3\2\2\2\u0115\u0117\5a\61\2\u0116\u0115\3\2"+
		"\2\2\u0117\u0118\3\2\2\2\u0118\u0116\3\2\2\2\u0118\u0119\3\2\2\2\u0119"+
		"\u011b\3\2\2\2\u011a\u0111\3\2\2\2\u011a\u011b\3\2\2\2\u011b\u0123\3\2"+
		"\2\2\u011c\u011e\7\60\2\2\u011d\u011f\5a\61\2\u011e\u011d\3\2\2\2\u011f"+
		"\u0120\3\2\2\2\u0120\u011e\3\2\2\2\u0120\u0121\3\2\2\2\u0121\u0123\3\2"+
		"\2\2\u0122\u0106\3\2\2\2\u0122\u011c\3\2\2\2\u0123^\3\2\2\2\u0124\u0129"+
		"\5c\62\2\u0125\u0128\5c\62\2\u0126\u0128\5a\61\2\u0127\u0125\3\2\2\2\u0127"+
		"\u0126\3\2\2\2\u0128\u012b\3\2\2\2\u0129\u0127\3\2\2\2\u0129\u012a\3\2"+
		"\2\2\u012a`\3\2\2\2\u012b\u0129\3\2\2\2\u012c\u012d\t\3\2\2\u012db\3\2"+
		"\2\2\u012e\u012f\t\4\2\2\u012fd\3\2\2\2\u0130\u0131\7\61\2\2\u0131\u0132"+
		"\7\61\2\2\u0132\u0136\3\2\2\2\u0133\u0135\13\2\2\2\u0134\u0133\3\2\2\2"+
		"\u0135\u0138\3\2\2\2\u0136\u0137\3\2\2\2\u0136\u0134\3\2\2\2\u0137\u0139"+
		"\3\2\2\2\u0138\u0136\3\2\2\2\u0139\u013a\7\f\2\2\u013a\u013b\3\2\2\2\u013b"+
		"\u013c\b\63\2\2\u013cf\3\2\2\2\u013d\u013e\7\61\2\2\u013e\u013f\7,\2\2"+
		"\u013f\u0143\3\2\2\2\u0140\u0142\13\2\2\2\u0141\u0140\3\2\2\2\u0142\u0145"+
		"\3\2\2\2\u0143\u0144\3\2\2\2\u0143\u0141\3\2\2\2\u0144\u0146\3\2\2\2\u0145"+
		"\u0143\3\2\2\2\u0146\u0147\7,\2\2\u0147\u0148\7\61\2\2\u0148\u0149\3\2"+
		"\2\2\u0149\u014a\b\64\2\2\u014ah\3\2\2\2\u014b\u014d\t\5\2\2\u014c\u014b"+
		"\3\2\2\2\u014d\u014e\3\2\2\2\u014e\u014c\3\2\2\2\u014e\u014f\3\2\2\2\u014f"+
		"\u0150\3\2\2\2\u0150\u0151\b\65\2\2\u0151j\3\2\2\2\20\2\u0103\u0108\u010e"+
		"\u0113\u0118\u011a\u0120\u0122\u0127\u0129\u0136\u0143\u014e\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}