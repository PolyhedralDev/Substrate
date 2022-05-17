package com.dfsek.substrate.lexer.token;

import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.lexer.read.Positioned;

public final class Token implements Positioned {
    private final String content;
    private final TokenType type;
    private final Position start;

    public Token(String content, TokenType type, Position start) {
        this.content = content;
        this.type = type;
        this.start = start;
    }

    public TokenType getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public Position getPosition() {
        return start;
    }

    @Override
    public String toString() {
        return type + ": '" + content + "': " + start;
    }

    public boolean isConstant() {
        return this.type.equals(TokenType.NUMBER) || this.type.equals(TokenType.STRING) || this.type.equals(TokenType.BOOLEAN) || this.type.equals(TokenType.INT);
    }

    public boolean isType() {
        return type == TokenType.NUM_TYPE
                || type == TokenType.STRING_TYPE
                || type == TokenType.FUN_TYPE
                || type == TokenType.INT_TYPE
                || type == TokenType.BOOL_TYPE
                || type == TokenType.LIST_TYPE
                || type == TokenType.IO;
    }

    public boolean isBinaryOperator() {
        return type.equals(TokenType.ADDITION_OPERATOR)
                || type.equals(TokenType.SUBTRACTION_OPERATOR)
                || type.equals(TokenType.MULTIPLICATION_OPERATOR)
                || type.equals(TokenType.DIVISION_OPERATOR)
                || type.equals(TokenType.EQUALS_OPERATOR)
                || type.equals(TokenType.NOT_EQUALS_OPERATOR)
                || type.equals(TokenType.LESS_THAN_OPERATOR)
                || type.equals(TokenType.GREATER_THAN_OPERATOR)
                || type.equals(TokenType.LESS_THAN_OR_EQUALS_OPERATOR)
                || type.equals(TokenType.GREATER_THAN_OR_EQUALS_OPERATOR)
                || type.equals(TokenType.BOOLEAN_OR)
                || type.equals(TokenType.BOOLEAN_AND)
                || type.equals(TokenType.MODULO_OPERATOR);
    }

    public boolean isIdentifier() {
        return type.equals(TokenType.IDENTIFIER);
    }

}
