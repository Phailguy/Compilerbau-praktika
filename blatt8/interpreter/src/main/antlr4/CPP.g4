grammar CPP;

// ============================================================================
// parser rules
// ============================================================================

// program consists of class and function declarations
program
    : (classDecl | functionDecl)* EOF
    ;

// class declaration
classDecl
    : CLASS IDENTIFIER (COLON PUBLIC IDENTIFIER)? LBRACE PUBLIC COLON classMember* RBRACE SEMICOLON
    ;

classMember
    : fieldDecl
    | methodDecl
    | constructorDecl
    ;

// field declaration in class
fieldDecl
    : type IDENTIFIER SEMICOLON
    ;

// method declaration
methodDecl
    : VIRTUAL? type IDENTIFIER LPAREN parameterList? RPAREN block
    ;

// constructor
constructorDecl
    : IDENTIFIER LPAREN parameterList? RPAREN block
    ;

// function declaration
functionDecl
    : type IDENTIFIER LPAREN parameterList? RPAREN block
    ;

// parameter list
parameterList
    : parameter (COMMA parameter)*
    ;

parameter
    : type AMPERSAND? IDENTIFIER
    ;

// type (with optional reference marking)
type
    : BOOL
    | INT
    | CHAR
    | STRING
    | VOID
    | IDENTIFIER  // class type
    ;

// statements
statement
    : block
    | varDecl
    | ifStmt
    | whileStmt
    | returnStmt
    | exprStmt
    ;

// block
block
    : LBRACE statement* RBRACE
    ;

// variable declaration
varDecl
    : type AMPERSAND? IDENTIFIER (ASSIGN expression)? SEMICOLON
    ;

// if statement
ifStmt
    : IF LPAREN expression RPAREN statement (ELSE statement)?
    ;

// while statement
whileStmt
    : WHILE LPAREN expression RPAREN statement
    ;

// return statement
returnStmt
    : RETURN expression? SEMICOLON
    ;

// expression statement
exprStmt
    : expression SEMICOLON
    ;

// expressions with precedence (from low to high)
expression
    : assignmentExpr
    ;

// assignment (right-associative)
assignmentExpr
    : logicalOrExpr (ASSIGN assignmentExpr)?
    ;

// logical or
logicalOrExpr
    : logicalAndExpr (OR logicalAndExpr)*
    ;

// logical and
logicalAndExpr
    : equalityExpr (AND equalityExpr)*
    ;

// equality (==, !=)
equalityExpr
    : relationalExpr ((EQ | NEQ) relationalExpr)*
    ;

// relational (<, <=, >, >=)
relationalExpr
    : additiveExpr ((LT | LEQ | GT | GEQ) additiveExpr)*
    ;

// additive (+, -)
additiveExpr
    : multiplicativeExpr ((PLUS | MINUS) multiplicativeExpr)*
    ;

// multiplicative (*, /, %)
multiplicativeExpr
    : unaryExpr ((MULT | DIV | MOD) unaryExpr)*
    ;

// unary (+, -, !)
unaryExpr
    : (PLUS | MINUS | NOT) unaryExpr
    | postfixExpr
    ;

// postfix (function calls, member access)
postfixExpr
    : primaryExpr (postfixOp)*
    ;

postfixOp
    : LPAREN argumentList? RPAREN                     // function call
    | DOT IDENTIFIER LPAREN argumentList? RPAREN      // method call (must come before field access!)
    | DOT IDENTIFIER                                   // member access (field)
    ;

// argument list
argumentList
    : expression (COMMA expression)*
    ;

// primary expressions
primaryExpr
    : literal
    | IDENTIFIER
    | LPAREN expression RPAREN
    | IDENTIFIER LPAREN argumentList? RPAREN  // constructor call (e.g. T(args))
    ;

// literals
literal
    : INT_LITERAL
    | BOOL_LITERAL
    | CHAR_LITERAL
    | STRING_LITERAL
    ;

// ============================================================================
// lexer rules
// ============================================================================

// keywords
CLASS       : 'class';
PUBLIC      : 'public';
VIRTUAL     : 'virtual';
IF          : 'if';
ELSE        : 'else';
WHILE       : 'while';
RETURN      : 'return';

// types
BOOL        : 'bool';
INT         : 'int';
CHAR        : 'char';
STRING      : 'string';
VOID        : 'void';

// boolean literals
BOOL_LITERAL : 'true' | 'false';

// operators
PLUS        : '+';
MINUS       : '-';
MULT        : '*';
DIV         : '/';
MOD         : '%';
ASSIGN      : '=';
EQ          : '==';
NEQ         : '!=';
LT          : '<';
LEQ         : '<=';
GT          : '>';
GEQ         : '>=';
AND         : '&&';
OR          : '||';
NOT         : '!';
AMPERSAND   : '&';
DOT         : '.';

// delimiters
LPAREN      : '(';
RPAREN      : ')';
LBRACE      : '{';
RBRACE      : '}';
SEMICOLON   : ';';
COMMA       : ',';
COLON       : ':';

// literals
INT_LITERAL
    : [0-9]+
    ;

CHAR_LITERAL
    : '\'' ( ESC_SEQ | ~['\\] ) '\''
    ;

STRING_LITERAL
    : '"' ( ESC_SEQ | ~["\\] )* '"'
    ;

// escape sequences
fragment ESC_SEQ
    : '\\' [0'"\\nrt]
    ;

// identifier
IDENTIFIER
    : [a-zA-Z_][a-zA-Z0-9_]*
    ;

// comments and preprocessor (skip)
LINE_COMMENT
    : '//' ~[\r\n]* -> skip
    ;

BLOCK_COMMENT
    : '/*' .*? '*/' -> skip
    ;

PREPROCESSOR
    : '#' ~[\r\n]* -> skip
    ;

// whitespace
WS
    : [ \t\r\n]+ -> skip
    ;
