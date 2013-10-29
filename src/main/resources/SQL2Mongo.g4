
// Define a grammar called Hello
grammar SQL2Mongo;


@parser::header { package org.sql2mongo.parse; }
@lexer::header { package org.sql2mongo.parse; }

parse
  :  expression EOF!
  ;

expression:
	or;
	
or: and ((('O'|'o')('R'|'r'))^ and)*;

and: not ((('A'|'a')('N'|'n')('D'|'d'))^ not)*;

not: 'NOT'^ atom
	| atom;

atom: ID (op^ value)*
	| '('! expression ')'!;

op: '>' | '>=' | '<' | '<=' | '=' | '!=' | 'like' | 'rlike' | 'is';

value: (NULL|INT|BOOL|FLOAT|STRING|PRAM);

INT: ('0'..'9')+;
FLOAT:INT'.'INT;
BOOL : ('true'|'false');
STRING: '\''.*?'\'';
NULL: 'NOT'?' '+'null';
PRAM:'?';

ID : NAME ('.' NAME)*;
NAME : ('a'..'z' | 'A'..'Z' | '0'..'9')+;
WS : (' ' | '\t' | '\r' | '\n')+ {skip();};
