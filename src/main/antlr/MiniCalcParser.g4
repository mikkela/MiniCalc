parser grammar MiniCalcParser;

// We specify which lexer we are using: so it knows which terminals we can use
options { tokenVocab=MiniCalcLexer; }

miniCalcFile : lines=line+ ;

line      : statement (NEWLINE | EOF) ;

statement : inputDeclaration # inputDeclarationStatement
          | varDeclaration   # varDeclarationStatement
          | assignment       # assignmentStatement
          | print            # printStatement ;

print : PRINT LPAREN expression RPAREN ;

inputDeclaration : INPUT type name=ID ;

varDeclaration : VAR assignment ;

assignment : ID ASSIGN expression ;

expression : left=expression operator=(DIVISION|ASTERISK) right=expression # binaryOperation
           | left=expression operator=(PLUS|MINUS) right=expression        # binaryOperation
           | value=expression AS targetType=type                           # typeConversion
           | LPAREN expression RPAREN                                      # parenExpression
           | ID                                                            # varReference
           | MINUS expression                                              # minusExpression
           | STRING_OPEN (parts+=stringLiteralContent)* STRING_CLOSE       # stringLiteral
           | INTLIT                                                        # intLiteral
           | DECLIT                                                        # decimalLiteral ;

stringLiteralContent : STRING_CONTENT                                    # constantString
                     | INTERPOLATION_OPEN expression INTERPOLATION_CLOSE # inteRpolatedValue ;

type : INT     # integer
     | DECIMAL # decimal
     | STRING  # string ;
