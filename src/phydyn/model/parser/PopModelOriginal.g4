grammar PopModel;

// antlr -visitor -no-listener

@header {
package phydyn.model.parser;
}

// @lexer::header { blah}
// @members { more blah }

// Extended syntax: definition and matrix equation lists
definitions : (stm ';')+ ;
matrixEquation: 
     'F' '(' IDENT ',' IDENT ')' ASSIGN expr     # birthEquation
   | 'G' '(' IDENT ',' IDENT ')' ASSIGN expr     # migrationEquation
   | 'D' '(' IDENT ')' ASSIGN expr       # deathEquation
   | 'dot' '(' IDENT ')' ASSIGN expr     # nondemeEquation
   ;
matrixEquations : ( matrixEquation ';' )+ ; 

// Parsing rules - definitions and rhs of equations
stm : IDENT ASSIGN expr ;
equation : expr ;

expr:
    '(' expr ')'			# parenthExpr
  |  SUB expr               # minusExpr
  |  NOT expr               # notExpr
  |  IF expr THEN expr ELSE expr  # condExpr
  |  <assoc=right> expr '^' expr	# powerExpr
  |  expr op=(MUL|DIV) expr		# prodExpr
  |  expr op=(ADD|SUB) expr		# sumExpr
  |  expr op=(GT|LT|GEQ|LEQ|EQ) expr		# cmpExpr
  |  expr op=(AND|OR) expr		# boolExpr  
  |  op=(EXP|LOG|SQRT|SIN|COS|ABS|FLOOR|CEIL) '(' expr ')'     # callSpecialExpr
  |  op=(MAX|MIN|MOD) '(' expr ',' expr ')'  #callBinaryExpr
  |  IDENT '[' expr ']'     # vectorExpr
  |  IDENT	       	   		# identExpr
  |  val=INT				# intExpr
  |  val=FLOAT				# floatExpr
  ;

// Java Constants
// Operators
EQ: '==';
ASSIGN : '=';
AND : 'and';
OR  : 'or';
NOT : 'not';
LEQ : '!>';
GT  : '>';
LT  : '!>=';
GEQ : '>=';
ADD : '+' ;
SUB : '-' ;
MUL : '*' ;
DIV : '/' ;
POW : '^' ;

// Functions
EXP : 'exp' ;
LOG : 'log' ;
SQRT : 'sqrt' ;
SIN   : 'sin';
COS   : 'cos';
MAX   : 'max';
MIN   : 'min';
MOD   : 'mod';
ABS   : 'abs';
FLOOR : 'floor';
CEIL  : 'ceil';

// Other
IF : 'if' ;
THEN : 'then' ;
ELSE : 'else' ;


// Lexer rules
INT : DIGIT+;
FLOAT : DIGIT+ '.' DIGIT* ([eE] '-'? DIGIT+)?
      | '.' DIGIT+ ;
IDENT : ID_LETTER (ID_LETTER | DIGIT)* ;

fragment DIGIT : [0-9] ;
fragment ID_LETTER : [a-zA-Z_] ;

LINE_COMMENT	 : '//' .*? '\n' -> skip ;
MULTILINE_COMENT : '/*' .*? '*/' -> skip ;
WS 		 : [ \t\r\n]+ -> skip ;		// ignore whitespace