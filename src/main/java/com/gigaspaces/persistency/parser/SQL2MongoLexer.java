// Generated from SQL2Mongo.g4 by ANTLR 4.0
 package com.gigaspaces.persistency.parser; 
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SQL2MongoLexer extends Lexer {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__13=1, T__12=2, T__11=3, T__10=4, T__9=5, T__8=6, T__7=7, T__6=8, T__5=9, 
		T__4=10, T__3=11, T__2=12, T__1=13, T__0=14, INT=15, FLOAT=16, BOOL=17, 
		STRING=18, NULL=19, PRAM=20, ID=21, NAME=22, WS=23;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"'is'", "'>'", "'like'", "')'", "'and'", "'or'", "'('", "'rlike'", "'<'", 
		"'='", "'>='", "'!='", "'NOT'", "'<='", "INT", "FLOAT", "BOOL", "STRING", 
		"NULL", "'?'", "ID", "NAME", "WS"
	};
	public static final String[] ruleNames = {
		"T__13", "T__12", "T__11", "T__10", "T__9", "T__8", "T__7", "T__6", "T__5", 
		"T__4", "T__3", "T__2", "T__1", "T__0", "INT", "FLOAT", "BOOL", "STRING", 
		"NULL", "PRAM", "ID", "NAME", "WS"
	};


	public SQL2MongoLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "SQL2Mongo.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 22: WS_action((RuleContext)_localctx, actionIndex); break;
		}
	}
	private void WS_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0: skip(); break;
		}
	}

	public static final String _serializedATN =
		"\2\4\31\u009f\b\1\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b"+
		"\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20"+
		"\t\20\4\21\t\21\4\22\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27"+
		"\t\27\4\30\t\30\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\6\3"+
		"\6\3\6\3\6\3\7\3\7\3\7\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\13\3"+
		"\13\3\f\3\f\3\f\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\20\6"+
		"\20_\n\20\r\20\16\20`\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22"+
		"\3\22\3\22\3\22\5\22p\n\22\3\23\3\23\7\23t\n\23\f\23\16\23w\13\23\3\23"+
		"\3\23\3\24\3\24\3\24\5\24~\n\24\3\24\6\24\u0081\n\24\r\24\16\24\u0082"+
		"\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\26\3\26\3\26\7\26\u008f\n\26\f\26"+
		"\16\26\u0092\13\26\3\27\6\27\u0095\n\27\r\27\16\27\u0096\3\30\6\30\u009a"+
		"\n\30\r\30\16\30\u009b\3\30\3\30\3u\31\3\3\1\5\4\1\7\5\1\t\6\1\13\7\1"+
		"\r\b\1\17\t\1\21\n\1\23\13\1\25\f\1\27\r\1\31\16\1\33\17\1\35\20\1\37"+
		"\21\1!\22\1#\23\1%\24\1\'\25\1)\26\1+\27\1-\30\1/\31\2\3\2\4\5\62;C\\"+
		"c|\5\13\f\17\17\"\"\u00a6\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2"+
		"\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2"+
		"\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3"+
		"\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2"+
		"\2\2\2-\3\2\2\2\2/\3\2\2\2\3\61\3\2\2\2\5\64\3\2\2\2\7\66\3\2\2\2\t;\3"+
		"\2\2\2\13=\3\2\2\2\rA\3\2\2\2\17D\3\2\2\2\21F\3\2\2\2\23L\3\2\2\2\25N"+
		"\3\2\2\2\27P\3\2\2\2\31S\3\2\2\2\33V\3\2\2\2\35Z\3\2\2\2\37^\3\2\2\2!"+
		"b\3\2\2\2#o\3\2\2\2%q\3\2\2\2\'}\3\2\2\2)\u0089\3\2\2\2+\u008b\3\2\2\2"+
		"-\u0094\3\2\2\2/\u0099\3\2\2\2\61\62\7k\2\2\62\63\7u\2\2\63\4\3\2\2\2"+
		"\64\65\7@\2\2\65\6\3\2\2\2\66\67\7n\2\2\678\7k\2\289\7m\2\29:\7g\2\2:"+
		"\b\3\2\2\2;<\7+\2\2<\n\3\2\2\2=>\7c\2\2>?\7p\2\2?@\7f\2\2@\f\3\2\2\2A"+
		"B\7q\2\2BC\7t\2\2C\16\3\2\2\2DE\7*\2\2E\20\3\2\2\2FG\7t\2\2GH\7n\2\2H"+
		"I\7k\2\2IJ\7m\2\2JK\7g\2\2K\22\3\2\2\2LM\7>\2\2M\24\3\2\2\2NO\7?\2\2O"+
		"\26\3\2\2\2PQ\7@\2\2QR\7?\2\2R\30\3\2\2\2ST\7#\2\2TU\7?\2\2U\32\3\2\2"+
		"\2VW\7P\2\2WX\7Q\2\2XY\7V\2\2Y\34\3\2\2\2Z[\7>\2\2[\\\7?\2\2\\\36\3\2"+
		"\2\2]_\4\62;\2^]\3\2\2\2_`\3\2\2\2`^\3\2\2\2`a\3\2\2\2a \3\2\2\2bc\5\37"+
		"\20\2cd\7\60\2\2de\5\37\20\2e\"\3\2\2\2fg\7v\2\2gh\7t\2\2hi\7w\2\2ip\7"+
		"g\2\2jk\7h\2\2kl\7c\2\2lm\7n\2\2mn\7u\2\2np\7g\2\2of\3\2\2\2oj\3\2\2\2"+
		"p$\3\2\2\2qu\7)\2\2rt\13\2\2\2sr\3\2\2\2tw\3\2\2\2uv\3\2\2\2us\3\2\2\2"+
		"vx\3\2\2\2wu\3\2\2\2xy\7)\2\2y&\3\2\2\2z{\7P\2\2{|\7Q\2\2|~\7V\2\2}z\3"+
		"\2\2\2}~\3\2\2\2~\u0080\3\2\2\2\177\u0081\7\"\2\2\u0080\177\3\2\2\2\u0081"+
		"\u0082\3\2\2\2\u0082\u0080\3\2\2\2\u0082\u0083\3\2\2\2\u0083\u0084\3\2"+
		"\2\2\u0084\u0085\7p\2\2\u0085\u0086\7w\2\2\u0086\u0087\7n\2\2\u0087\u0088"+
		"\7n\2\2\u0088(\3\2\2\2\u0089\u008a\7A\2\2\u008a*\3\2\2\2\u008b\u0090\5"+
		"-\27\2\u008c\u008d\7\60\2\2\u008d\u008f\5-\27\2\u008e\u008c\3\2\2\2\u008f"+
		"\u0092\3\2\2\2\u0090\u008e\3\2\2\2\u0090\u0091\3\2\2\2\u0091,\3\2\2\2"+
		"\u0092\u0090\3\2\2\2\u0093\u0095\t\2\2\2\u0094\u0093\3\2\2\2\u0095\u0096"+
		"\3\2\2\2\u0096\u0094\3\2\2\2\u0096\u0097\3\2\2\2\u0097.\3\2\2\2\u0098"+
		"\u009a\t\3\2\2\u0099\u0098\3\2\2\2\u009a\u009b\3\2\2\2\u009b\u0099\3\2"+
		"\2\2\u009b\u009c\3\2\2\2\u009c\u009d\3\2\2\2\u009d\u009e\b\30\2\2\u009e"+
		"\60\3\2\2\2\13\2`ou}\u0082\u0090\u0096\u009b";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
	}
}