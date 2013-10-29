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
		T__21=1, T__20=2, T__19=3, T__18=4, T__17=5, T__16=6, T__15=7, T__14=8, 
		T__13=9, T__12=10, T__11=11, T__10=12, T__9=13, T__8=14, T__7=15, T__6=16, 
		T__5=17, T__4=18, T__3=19, T__2=20, T__1=21, T__0=22, INT=23, FLOAT=24, 
		BOOL=25, STRING=26, NULL=27, PRAM=28, ID=29, NAME=30, WS=31;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"'is'", "'a'", "'like'", "')'", "'D'", "'('", "'A'", "'o'", "'<'", "'d'", 
		"'='", "'r'", "'!='", "'N'", "'<='", "'O'", "'>'", "'R'", "'n'", "'rlike'", 
		"'>='", "'NOT'", "INT", "FLOAT", "BOOL", "STRING", "NULL", "'?'", "ID", 
		"NAME", "WS"
	};
	public static final String[] ruleNames = {
		"T__21", "T__20", "T__19", "T__18", "T__17", "T__16", "T__15", "T__14", 
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
		case 30: WS_action((RuleContext)_localctx, actionIndex); break;
		}
	}
	private void WS_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0: skip(); break;
		}
	}

	public static final String _serializedATN =
		"\2\4!\u00bc\b\1\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t"+
		"\b\4\t\t\t\4\n\t\n\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20"+
		"\t\20\4\21\t\21\4\22\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27"+
		"\t\27\4\30\t\30\4\31\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36"+
		"\t\36\4\37\t\37\4 \t \3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\5\3\5"+
		"\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16"+
		"\3\16\3\16\3\17\3\17\3\20\3\20\3\20\3\21\3\21\3\22\3\22\3\23\3\23\3\24"+
		"\3\24\3\25\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\27\3\27\3\27\3\27"+
		"\3\30\6\30|\n\30\r\30\16\30}\3\31\3\31\3\31\3\31\3\32\3\32\3\32\3\32\3"+
		"\32\3\32\3\32\3\32\3\32\5\32\u008d\n\32\3\33\3\33\7\33\u0091\n\33\f\33"+
		"\16\33\u0094\13\33\3\33\3\33\3\34\3\34\3\34\5\34\u009b\n\34\3\34\6\34"+
		"\u009e\n\34\r\34\16\34\u009f\3\34\3\34\3\34\3\34\3\34\3\35\3\35\3\36\3"+
		"\36\3\36\7\36\u00ac\n\36\f\36\16\36\u00af\13\36\3\37\6\37\u00b2\n\37\r"+
		"\37\16\37\u00b3\3 \6 \u00b7\n \r \16 \u00b8\3 \3 \3\u0092!\3\3\1\5\4\1"+
		"\7\5\1\t\6\1\13\7\1\r\b\1\17\t\1\21\n\1\23\13\1\25\f\1\27\r\1\31\16\1"+
		"\33\17\1\35\20\1\37\21\1!\22\1#\23\1%\24\1\'\25\1)\26\1+\27\1-\30\1/\31"+
		"\1\61\32\1\63\33\1\65\34\1\67\35\19\36\1;\37\1= \1?!\2\3\2\4\5\62;C\\"+
		"c|\5\13\f\17\17\"\"\u00c3\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2"+
		"\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2"+
		"\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3"+
		"\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2"+
		"\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67"+
		"\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\3A\3\2\2\2\5D\3\2"+
		"\2\2\7F\3\2\2\2\tK\3\2\2\2\13M\3\2\2\2\rO\3\2\2\2\17Q\3\2\2\2\21S\3\2"+
		"\2\2\23U\3\2\2\2\25W\3\2\2\2\27Y\3\2\2\2\31[\3\2\2\2\33]\3\2\2\2\35`\3"+
		"\2\2\2\37b\3\2\2\2!e\3\2\2\2#g\3\2\2\2%i\3\2\2\2\'k\3\2\2\2)m\3\2\2\2"+
		"+s\3\2\2\2-v\3\2\2\2/{\3\2\2\2\61\177\3\2\2\2\63\u008c\3\2\2\2\65\u008e"+
		"\3\2\2\2\67\u009a\3\2\2\29\u00a6\3\2\2\2;\u00a8\3\2\2\2=\u00b1\3\2\2\2"+
		"?\u00b6\3\2\2\2AB\7k\2\2BC\7u\2\2C\4\3\2\2\2DE\7c\2\2E\6\3\2\2\2FG\7n"+
		"\2\2GH\7k\2\2HI\7m\2\2IJ\7g\2\2J\b\3\2\2\2KL\7+\2\2L\n\3\2\2\2MN\7F\2"+
		"\2N\f\3\2\2\2OP\7*\2\2P\16\3\2\2\2QR\7C\2\2R\20\3\2\2\2ST\7q\2\2T\22\3"+
		"\2\2\2UV\7>\2\2V\24\3\2\2\2WX\7f\2\2X\26\3\2\2\2YZ\7?\2\2Z\30\3\2\2\2"+
		"[\\\7t\2\2\\\32\3\2\2\2]^\7#\2\2^_\7?\2\2_\34\3\2\2\2`a\7P\2\2a\36\3\2"+
		"\2\2bc\7>\2\2cd\7?\2\2d \3\2\2\2ef\7Q\2\2f\"\3\2\2\2gh\7@\2\2h$\3\2\2"+
		"\2ij\7T\2\2j&\3\2\2\2kl\7p\2\2l(\3\2\2\2mn\7t\2\2no\7n\2\2op\7k\2\2pq"+
		"\7m\2\2qr\7g\2\2r*\3\2\2\2st\7@\2\2tu\7?\2\2u,\3\2\2\2vw\7P\2\2wx\7Q\2"+
		"\2xy\7V\2\2y.\3\2\2\2z|\4\62;\2{z\3\2\2\2|}\3\2\2\2}{\3\2\2\2}~\3\2\2"+
		"\2~\60\3\2\2\2\177\u0080\5/\30\2\u0080\u0081\7\60\2\2\u0081\u0082\5/\30"+
		"\2\u0082\62\3\2\2\2\u0083\u0084\7v\2\2\u0084\u0085\7t\2\2\u0085\u0086"+
		"\7w\2\2\u0086\u008d\7g\2\2\u0087\u0088\7h\2\2\u0088\u0089\7c\2\2\u0089"+
		"\u008a\7n\2\2\u008a\u008b\7u\2\2\u008b\u008d\7g\2\2\u008c\u0083\3\2\2"+
		"\2\u008c\u0087\3\2\2\2\u008d\64\3\2\2\2\u008e\u0092\7)\2\2\u008f\u0091"+
		"\13\2\2\2\u0090\u008f\3\2\2\2\u0091\u0094\3\2\2\2\u0092\u0093\3\2\2\2"+
		"\u0092\u0090\3\2\2\2\u0093\u0095\3\2\2\2\u0094\u0092\3\2\2\2\u0095\u0096"+
		"\7)\2\2\u0096\66\3\2\2\2\u0097\u0098\7P\2\2\u0098\u0099\7Q\2\2\u0099\u009b"+
		"\7V\2\2\u009a\u0097\3\2\2\2\u009a\u009b\3\2\2\2\u009b\u009d\3\2\2\2\u009c"+
		"\u009e\7\"\2\2\u009d\u009c\3\2\2\2\u009e\u009f\3\2\2\2\u009f\u009d\3\2"+
		"\2\2\u009f\u00a0\3\2\2\2\u00a0\u00a1\3\2\2\2\u00a1\u00a2\7p\2\2\u00a2"+
		"\u00a3\7w\2\2\u00a3\u00a4\7n\2\2\u00a4\u00a5\7n\2\2\u00a58\3\2\2\2\u00a6"+
		"\u00a7\7A\2\2\u00a7:\3\2\2\2\u00a8\u00ad\5=\37\2\u00a9\u00aa\7\60\2\2"+
		"\u00aa\u00ac\5=\37\2\u00ab\u00a9\3\2\2\2\u00ac\u00af\3\2\2\2\u00ad\u00ab"+
		"\3\2\2\2\u00ad\u00ae\3\2\2\2\u00ae<\3\2\2\2\u00af\u00ad\3\2\2\2\u00b0"+
		"\u00b2\t\2\2\2\u00b1\u00b0\3\2\2\2\u00b2\u00b3\3\2\2\2\u00b3\u00b1\3\2"+
		"\2\2\u00b3\u00b4\3\2\2\2\u00b4>\3\2\2\2\u00b5\u00b7\t\3\2\2\u00b6\u00b5"+
		"\3\2\2\2\u00b7\u00b8\3\2\2\2\u00b8\u00b6\3\2\2\2\u00b8\u00b9\3\2\2\2\u00b9"+
		"\u00ba\3\2\2\2\u00ba\u00bb\b \2\2\u00bb@\3\2\2\2\13\2}\u008c\u0092\u009a"+
		"\u009f\u00ad\u00b3\u00b8";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
	}
}