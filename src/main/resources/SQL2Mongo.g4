
// Define a grammar called Hello
grammar SQL2Mongo;


@parser::header { package org.sql2mongo.parse; }
@lexer::header { package org.sql2mongo.parse; }

@members {
	StringBuilder sb= new StringBuilder();
	int open=0;
}

parse
  :  expression EOF!
  ;

expression:
	or;
	
or: and ('OR'^ and)*;

and: not ('AND'^ not)*;

not: 'NOT'^ atom
	| atom;

atom: ID (op^ value)*
	| '('! expression ')'!;

op: '>' | '>=' | '<' | '<=' | '=' | '<>';

orderBy: 'ORDER BY' ID+;
groupBy: 'GROUP BY' ID+;

value:(INT|BOOL|PRAM) ;


INT: ('0'..'9')+;
BOOL : ('true'|'false');
STRING: '\'.*\'';
PRAM:'?';

ID : NAME ('.' NAME)*;
NAME : ('a'..'z' | 'A'..'Z' | '0'..'9')+;
WS : (' ' | '\t' | '\r' | '\n')+ {skip();};
