// Generated from SQL2Mongo.g4 by ANTLR 4.0
 package com.gigaspaces.persistency.parser; 
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SQL2MongoParser extends Parser {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__21=1, T__20=2, T__19=3, T__18=4, T__17=5, T__16=6, T__15=7, T__14=8, 
		T__13=9, T__12=10, T__11=11, T__10=12, T__9=13, T__8=14, T__7=15, T__6=16, 
		T__5=17, T__4=18, T__3=19, T__2=20, T__1=21, T__0=22, INT=23, FLOAT=24, 
		BOOL=25, STRING=26, NULL=27, PRAM=28, ID=29, NAME=30, WS=31;
	public static final String[] tokenNames = {
		"<INVALID>", "'is'", "'a'", "'like'", "')'", "'D'", "'('", "'A'", "'o'", 
		"'<'", "'d'", "'='", "'r'", "'!='", "'N'", "'<='", "'O'", "'>'", "'R'", 
		"'n'", "'rlike'", "'>='", "'NOT'", "INT", "FLOAT", "BOOL", "STRING", "NULL", 
		"'?'", "ID", "NAME", "WS"
	};
	public static final int
		RULE_parse = 0, RULE_expression = 1, RULE_or = 2, RULE_and = 3, RULE_not = 4, 
		RULE_atom = 5, RULE_op = 6, RULE_value = 7;
	public static final String[] ruleNames = {
		"parse", "expression", "or", "and", "not", "atom", "op", "value"
	};

	@Override
	public String getGrammarFileName() { return "SQL2Mongo.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public SQL2MongoParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ParseContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode EOF() { return getToken(SQL2MongoParser.EOF, 0); }
		public ParseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parse; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQL2MongoVisitor ) return ((SQL2MongoVisitor<? extends T>)visitor).visitParse(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParseContext parse() throws RecognitionException {
		ParseContext _localctx = new ParseContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_parse);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(16); expression();
			setState(17); match(EOF);
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

	public static class ExpressionContext extends ParserRuleContext {
		public OrContext or() {
			return getRuleContext(OrContext.class,0);
		}
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQL2MongoVisitor ) return ((SQL2MongoVisitor<? extends T>)visitor).visitExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(19); or();
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

	public static class OrContext extends ParserRuleContext {
		public AndContext and(int i) {
			return getRuleContext(AndContext.class,i);
		}
		public List<AndContext> and() {
			return getRuleContexts(AndContext.class);
		}
		public OrContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_or; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQL2MongoVisitor ) return ((SQL2MongoVisitor<? extends T>)visitor).visitOr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OrContext or() throws RecognitionException {
		OrContext _localctx = new OrContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_or);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(21); and();
			setState(28);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==8 || _la==16) {
				{
				{
				{
				setState(22);
				_la = _input.LA(1);
				if ( !(_la==8 || _la==16) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				setState(23);
				_la = _input.LA(1);
				if ( !(_la==12 || _la==18) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				}
				setState(25); and();
				}
				}
				setState(30);
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

	public static class AndContext extends ParserRuleContext {
		public List<NotContext> not() {
			return getRuleContexts(NotContext.class);
		}
		public NotContext not(int i) {
			return getRuleContext(NotContext.class,i);
		}
		public AndContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_and; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQL2MongoVisitor ) return ((SQL2MongoVisitor<? extends T>)visitor).visitAnd(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AndContext and() throws RecognitionException {
		AndContext _localctx = new AndContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_and);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(31); not();
			setState(39);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==2 || _la==7) {
				{
				{
				{
				setState(32);
				_la = _input.LA(1);
				if ( !(_la==2 || _la==7) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				setState(33);
				_la = _input.LA(1);
				if ( !(_la==14 || _la==19) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				setState(34);
				_la = _input.LA(1);
				if ( !(_la==5 || _la==10) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				}
				setState(36); not();
				}
				}
				setState(41);
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

	public static class NotContext extends ParserRuleContext {
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public NotContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_not; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQL2MongoVisitor ) return ((SQL2MongoVisitor<? extends T>)visitor).visitNot(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NotContext not() throws RecognitionException {
		NotContext _localctx = new NotContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_not);
		try {
			setState(45);
			switch (_input.LA(1)) {
			case 22:
				enterOuterAlt(_localctx, 1);
				{
				setState(42); match(22);
				setState(43); atom();
				}
				break;
			case 6:
			case ID:
				enterOuterAlt(_localctx, 2);
				{
				setState(44); atom();
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

	public static class AtomContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ValueContext value(int i) {
			return getRuleContext(ValueContext.class,i);
		}
		public List<OpContext> op() {
			return getRuleContexts(OpContext.class);
		}
		public List<ValueContext> value() {
			return getRuleContexts(ValueContext.class);
		}
		public TerminalNode ID() { return getToken(SQL2MongoParser.ID, 0); }
		public OpContext op(int i) {
			return getRuleContext(OpContext.class,i);
		}
		public AtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atom; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQL2MongoVisitor ) return ((SQL2MongoVisitor<? extends T>)visitor).visitAtom(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AtomContext atom() throws RecognitionException {
		AtomContext _localctx = new AtomContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_atom);
		int _la;
		try {
			setState(60);
			switch (_input.LA(1)) {
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(47); match(ID);
				setState(53);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 1) | (1L << 3) | (1L << 9) | (1L << 11) | (1L << 13) | (1L << 15) | (1L << 17) | (1L << 20) | (1L << 21))) != 0)) {
					{
					{
					setState(48); op();
					setState(49); value();
					}
					}
					setState(55);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 2);
				{
				setState(56); match(6);
				setState(57); expression();
				setState(58); match(4);
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

	public static class OpContext extends ParserRuleContext {
		public OpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_op; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQL2MongoVisitor ) return ((SQL2MongoVisitor<? extends T>)visitor).visitOp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OpContext op() throws RecognitionException {
		OpContext _localctx = new OpContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(62);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 1) | (1L << 3) | (1L << 9) | (1L << 11) | (1L << 13) | (1L << 15) | (1L << 17) | (1L << 20) | (1L << 21))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			consume();
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

	public static class ValueContext extends ParserRuleContext {
		public TerminalNode FLOAT() { return getToken(SQL2MongoParser.FLOAT, 0); }
		public TerminalNode INT() { return getToken(SQL2MongoParser.INT, 0); }
		public TerminalNode PRAM() { return getToken(SQL2MongoParser.PRAM, 0); }
		public TerminalNode NULL() { return getToken(SQL2MongoParser.NULL, 0); }
		public TerminalNode STRING() { return getToken(SQL2MongoParser.STRING, 0); }
		public TerminalNode BOOL() { return getToken(SQL2MongoParser.BOOL, 0); }
		public ValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQL2MongoVisitor ) return ((SQL2MongoVisitor<? extends T>)visitor).visitValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_value);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(64);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << FLOAT) | (1L << BOOL) | (1L << STRING) | (1L << NULL) | (1L << PRAM))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			consume();
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

	public static final String _serializedATN =
		"\2\3!E\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t"+
		"\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\4\3\4\3\4\7\4\35\n\4\f\4\16\4 \13\4\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\7\5(\n\5\f\5\16\5+\13\5\3\6\3\6\3\6\5\6\60\n\6\3"+
		"\7\3\7\3\7\3\7\7\7\66\n\7\f\7\16\79\13\7\3\7\3\7\3\7\3\7\5\7?\n\7\3\b"+
		"\3\b\3\t\3\t\3\t\2\n\2\4\6\b\n\f\16\20\2\t\4\n\n\22\22\4\16\16\24\24\4"+
		"\4\4\t\t\4\20\20\25\25\4\7\7\f\f\n\3\3\5\5\13\13\r\r\17\17\21\21\23\23"+
		"\26\27\3\31\36A\2\22\3\2\2\2\4\25\3\2\2\2\6\27\3\2\2\2\b!\3\2\2\2\n/\3"+
		"\2\2\2\f>\3\2\2\2\16@\3\2\2\2\20B\3\2\2\2\22\23\5\4\3\2\23\24\7\1\2\2"+
		"\24\3\3\2\2\2\25\26\5\6\4\2\26\5\3\2\2\2\27\36\5\b\5\2\30\31\t\2\2\2\31"+
		"\32\t\3\2\2\32\33\3\2\2\2\33\35\5\b\5\2\34\30\3\2\2\2\35 \3\2\2\2\36\34"+
		"\3\2\2\2\36\37\3\2\2\2\37\7\3\2\2\2 \36\3\2\2\2!)\5\n\6\2\"#\t\4\2\2#"+
		"$\t\5\2\2$%\t\6\2\2%&\3\2\2\2&(\5\n\6\2\'\"\3\2\2\2(+\3\2\2\2)\'\3\2\2"+
		"\2)*\3\2\2\2*\t\3\2\2\2+)\3\2\2\2,-\7\30\2\2-\60\5\f\7\2.\60\5\f\7\2/"+
		",\3\2\2\2/.\3\2\2\2\60\13\3\2\2\2\61\67\7\37\2\2\62\63\5\16\b\2\63\64"+
		"\5\20\t\2\64\66\3\2\2\2\65\62\3\2\2\2\669\3\2\2\2\67\65\3\2\2\2\678\3"+
		"\2\2\28?\3\2\2\29\67\3\2\2\2:;\7\b\2\2;<\5\4\3\2<=\7\6\2\2=?\3\2\2\2>"+
		"\61\3\2\2\2>:\3\2\2\2?\r\3\2\2\2@A\t\7\2\2A\17\3\2\2\2BC\t\b\2\2C\21\3"+
		"\2\2\2\7\36)/\67>";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
	}
}