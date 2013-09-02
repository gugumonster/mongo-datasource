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
		T__12=1, T__11=2, T__10=3, T__9=4, T__8=5, T__7=6, T__6=7, T__5=8, T__4=9, 
		T__3=10, T__2=11, T__1=12, T__0=13, INT=14, BOOL=15, STRING=16, PRAM=17, 
		ID=18, NAME=19, WS=20;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"'AND'", "')'", "'('", "'<>'", "'<'", "'='", "'<='", "'>'", "'OR'", "'GROUP BY'", 
		"'ORDER BY'", "'>='", "'NOT'", "INT", "BOOL", "''.*''", "'?'", "ID", "NAME", 
		"WS"
	};
	public static final String[] ruleNames = {
		"T__12", "T__11", "T__10", "T__9", "T__8", "T__7", "T__6", "T__5", "T__4", 
		"T__3", "T__2", "T__1", "T__0", "INT", "BOOL", "STRING", "PRAM", "ID", 
		"NAME", "WS"
	};


		StringBuilder sb= new StringBuilder();
		int open=0;


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
		case 19: WS_action((RuleContext)_localctx, actionIndex); break;
		}
	}
	private void WS_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0: skip(); break;
		}
	}

	public static final String _serializedATN =
		"\2\4\26\u0086\b\1\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b"+
		"\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20"+
		"\t\20\4\21\t\21\4\22\t\22\4\23\t\23\4\24\t\24\4\25\t\25\3\2\3\2\3\2\3"+
		"\2\3\3\3\3\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\b\3\t\3\t\3\n"+
		"\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f"+
		"\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\17\6\17]\n\17\r"+
		"\17\16\17^\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\5\20j\n\20\3\21"+
		"\3\21\3\21\3\21\3\21\3\22\3\22\3\23\3\23\3\23\7\23v\n\23\f\23\16\23y\13"+
		"\23\3\24\6\24|\n\24\r\24\16\24}\3\25\6\25\u0081\n\25\r\25\16\25\u0082"+
		"\3\25\3\25\2\26\3\3\1\5\4\1\7\5\1\t\6\1\13\7\1\r\b\1\17\t\1\21\n\1\23"+
		"\13\1\25\f\1\27\r\1\31\16\1\33\17\1\35\20\1\37\21\1!\22\1#\23\1%\24\1"+
		"\'\25\1)\26\2\3\2\4\5\62;C\\c|\5\13\f\17\17\"\"\u008a\2\3\3\2\2\2\2\5"+
		"\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2"+
		"\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33"+
		"\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2"+
		"\'\3\2\2\2\2)\3\2\2\2\3+\3\2\2\2\5/\3\2\2\2\7\61\3\2\2\2\t\63\3\2\2\2"+
		"\13\66\3\2\2\2\r8\3\2\2\2\17:\3\2\2\2\21=\3\2\2\2\23?\3\2\2\2\25B\3\2"+
		"\2\2\27K\3\2\2\2\31T\3\2\2\2\33W\3\2\2\2\35\\\3\2\2\2\37i\3\2\2\2!k\3"+
		"\2\2\2#p\3\2\2\2%r\3\2\2\2\'{\3\2\2\2)\u0080\3\2\2\2+,\7C\2\2,-\7P\2\2"+
		"-.\7F\2\2.\4\3\2\2\2/\60\7+\2\2\60\6\3\2\2\2\61\62\7*\2\2\62\b\3\2\2\2"+
		"\63\64\7>\2\2\64\65\7@\2\2\65\n\3\2\2\2\66\67\7>\2\2\67\f\3\2\2\289\7"+
		"?\2\29\16\3\2\2\2:;\7>\2\2;<\7?\2\2<\20\3\2\2\2=>\7@\2\2>\22\3\2\2\2?"+
		"@\7Q\2\2@A\7T\2\2A\24\3\2\2\2BC\7I\2\2CD\7T\2\2DE\7Q\2\2EF\7W\2\2FG\7"+
		"R\2\2GH\7\"\2\2HI\7D\2\2IJ\7[\2\2J\26\3\2\2\2KL\7Q\2\2LM\7T\2\2MN\7F\2"+
		"\2NO\7G\2\2OP\7T\2\2PQ\7\"\2\2QR\7D\2\2RS\7[\2\2S\30\3\2\2\2TU\7@\2\2"+
		"UV\7?\2\2V\32\3\2\2\2WX\7P\2\2XY\7Q\2\2YZ\7V\2\2Z\34\3\2\2\2[]\4\62;\2"+
		"\\[\3\2\2\2]^\3\2\2\2^\\\3\2\2\2^_\3\2\2\2_\36\3\2\2\2`a\7v\2\2ab\7t\2"+
		"\2bc\7w\2\2cj\7g\2\2de\7h\2\2ef\7c\2\2fg\7n\2\2gh\7u\2\2hj\7g\2\2i`\3"+
		"\2\2\2id\3\2\2\2j \3\2\2\2kl\7)\2\2lm\7\60\2\2mn\7,\2\2no\7)\2\2o\"\3"+
		"\2\2\2pq\7A\2\2q$\3\2\2\2rw\5\'\24\2st\7\60\2\2tv\5\'\24\2us\3\2\2\2v"+
		"y\3\2\2\2wu\3\2\2\2wx\3\2\2\2x&\3\2\2\2yw\3\2\2\2z|\t\2\2\2{z\3\2\2\2"+
		"|}\3\2\2\2}{\3\2\2\2}~\3\2\2\2~(\3\2\2\2\177\u0081\t\3\2\2\u0080\177\3"+
		"\2\2\2\u0081\u0082\3\2\2\2\u0082\u0080\3\2\2\2\u0082\u0083\3\2\2\2\u0083"+
		"\u0084\3\2\2\2\u0084\u0085\b\25\2\2\u0085*\3\2\2\2\b\2^iw}\u0082";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
	}
}