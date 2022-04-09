package com.dfsek.substrate.lexer.token;

public enum TokenType {
    /**
     * Tokenizer error
     */
    ERROR,
    /**
     * Value identifier
     */
    IDENTIFIER,

    /**
     * Numeric literal
     */
    NUMBER,
    /**
     * Integer literal
     */
    INT,
    /**
     * String literal
     */
    STRING,
    /**
     * Boolean literal
     */
    BOOLEAN,
    /**
     * Beginning of group "("
     */
    GROUP_BEGIN,
    /**
     * Ending of group ")"
     */
    GROUP_END,
    /**
     * End of statement ";"
     */
    STATEMENT_END,
    /**
     * Argument separator ","
     */
    SEPARATOR,
    /**
     * Beginning of code block "{"
     */
    BLOCK_BEGIN,
    /**
     * End of code block "}"
     */
    BLOCK_END,
    /**
     * Beginning of list reference "["
     */
    LIST_BEGIN,
    /**
     * End of list reference "]"
     */
    LIST_END,
    /**
     * assignment operator "="
     */
    ASSIGNMENT,
    /**
     * Boolean equals operator "=="
     */
    EQUALS_OPERATOR,
    /**
     * Boolean not equals operator "!="
     */
    NOT_EQUALS_OPERATOR,
    /**
     * Boolean greater than operator or generic ending ">"
     */
    GREATER_THAN_OPERATOR,
    /**
     * Boolean less than operator or generic beginning "<"
     */
    LESS_THAN_OPERATOR,
    /**
     * Boolean greater than or equal to operator ">="
     */
    GREATER_THAN_OR_EQUALS_OPERATOR,
    /**
     * Boolean less than or equal to operator "<="
     */
    LESS_THAN_OR_EQUALS_OPERATOR,
    /**
     * Addition/concatenation operator "+"
     */
    ADDITION_OPERATOR,
    /**
     * Subtraction operator "-"
     */
    SUBTRACTION_OPERATOR,
    /**
     * Multiplication operator "*"
     */
    MULTIPLICATION_OPERATOR,
    /**
     * Division operator "/"
     */
    DIVISION_OPERATOR,
    /**
     * Modulo operator. "%"
     */
    MODULO_OPERATOR,
    /**
     * Boolean not operator "!"
     */
    BOOLEAN_NOT,
    /**
     * Boolean or "||"
     */
    BOOLEAN_OR,
    /**
     * Boolean and "&&
     */
    BOOLEAN_AND,
    /**
     * Return statement
     */
    RETURN,
    /**
     * Arrow token "->"
     */
    ARROW,
    /**
     * Range token ".."
     */
    RANGE,
    /**
     * Type specification token ":"
     */
    TYPE,
    /**
     * Decimal number type "num
     */
    NUM_TYPE,
    /**
     * Integer number type "int"
     */
    INT_TYPE,
    /**
     * Boolean type "bool"
     */
    BOOL_TYPE,
    /**
     * String type "str"
     */
    STRING_TYPE,
    /**
     * Function type "fun"
     */
    FUN_TYPE,
    /**
     * List type token "list"
     */
    LIST_TYPE,
    /**
     * If expression token "if"
     */
    IF,
    /**
     * Else token "else"
     */
    ELSE
}
