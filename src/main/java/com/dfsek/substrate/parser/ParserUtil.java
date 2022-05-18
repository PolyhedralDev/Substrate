package com.dfsek.substrate.parser;

import com.dfsek.substrate.lang.compiler.type.DataType;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Typed;
import com.dfsek.substrate.lexer.Lexer;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.lexer.read.Positioned;
import com.dfsek.substrate.lexer.token.Token;
import com.dfsek.substrate.lexer.token.TokenType;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.Tuple2;
import io.vavr.control.Either;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ParserUtil {
    private static final Map<TokenType, Map<TokenType, Boolean>> PRECEDENCE = new HashMap<>(); // If second has precedence, true.
    private static final List<TokenType> ARITHMETIC = Arrays.asList(TokenType.ADDITION_OPERATOR, TokenType.SUBTRACTION_OPERATOR, TokenType.MULTIPLICATION_OPERATOR, TokenType.DIVISION_OPERATOR, TokenType.MODULO_OPERATOR);
    private static final List<TokenType> COMPARISON = Arrays.asList(TokenType.EQUALS_OPERATOR, TokenType.NOT_EQUALS_OPERATOR, TokenType.LESS_THAN_OPERATOR, TokenType.LESS_THAN_OR_EQUALS_OPERATOR, TokenType.GREATER_THAN_OPERATOR, TokenType.GREATER_THAN_OR_EQUALS_OPERATOR);

    static { // Setup precedence
        Map<TokenType, Boolean> add = new HashMap<>(); // Addition/subtraction before Multiplication/division.
        add.put(TokenType.MULTIPLICATION_OPERATOR, true);
        add.put(TokenType.DIVISION_OPERATOR, true);

        PRECEDENCE.put(TokenType.ADDITION_OPERATOR, add);
        PRECEDENCE.put(TokenType.SUBTRACTION_OPERATOR, add);

        Map<TokenType, Boolean> numericBoolean = new HashMap<>();

        ARITHMETIC.forEach(op -> numericBoolean.put(op, true)); // Numbers before comparison
        COMPARISON.forEach(op -> PRECEDENCE.put(op, numericBoolean));


        Map<TokenType, Boolean> booleanOps = new HashMap<>();
        ARITHMETIC.forEach(op -> booleanOps.put(op, true)); // Everything before boolean
        COMPARISON.forEach(op -> booleanOps.put(op, true));


        PRECEDENCE.put(TokenType.BOOLEAN_AND, booleanOps);
        PRECEDENCE.put(TokenType.BOOLEAN_OR, booleanOps);
    }

    public static Token checkType(Token token, TokenType... expected) throws ParseException {
        for (TokenType type : expected) if (token.getType().equals(type)) return token;
        throw new ParseException("Expected " + Arrays.toString(expected) + " but found " + token, token.getPosition());
    }

    public static Either<Tuple2<String, Position>, Token> checkTypeFunctional(Token token, TokenType... expected) throws ParseException {
        for (TokenType type : expected) if (token.getType().equals(type)) return Either.right(token);
        return Either.left(new Tuple2<>("Expected " + Arrays.toString(expected) + " but found " + token, token.getPosition()));
    }

    public static Signature parseSignatureNotation(Lexer lexer) {
        Signature signature = Signature.empty();
        while (lexer.peek().isType()) {
            Token type = checkType(lexer.consume(), TokenType.INT_TYPE, TokenType.NUM_TYPE, TokenType.STRING_TYPE, TokenType.BOOL_TYPE, TokenType.FUN_TYPE, TokenType.LIST_TYPE, TokenType.IO);
            Signature other = new Signature(DataType.fromToken(type));
            if (!(other.weakEquals(Signature.integer())
                    || other.weakEquals(Signature.bool())
                    || other.weakEquals(Signature.decimal())
                    || other.weakEquals(Signature.string()))) {
                if (other.weakEquals(Signature.list())) {
                    ParserUtil.checkType(lexer.consume(), TokenType.LESS_THAN_OPERATOR);
                    other = other.applyGenericReturn(0, parseSignatureNotation(lexer));
                    ParserUtil.checkType(lexer.consume(), TokenType.GREATER_THAN_OPERATOR);
                } else if (other.weakEquals(Signature.io()) && lexer.peek().getType() == TokenType.LESS_THAN_OPERATOR) {
                    ParserUtil.checkType(lexer.consume(), TokenType.LESS_THAN_OPERATOR);
                    other = other.applyGenericReturn(0, parseSignatureNotation(lexer));
                    ParserUtil.checkType(lexer.consume(), TokenType.GREATER_THAN_OPERATOR);
                } else if (other.weakEquals(Signature.fun())) {
                    ParserUtil.checkType(lexer.consume(), TokenType.LESS_THAN_OPERATOR);
                    other = other.applyGenericArgument(0, parseSignatureNotation(lexer));
                    if (lexer.peek().getType() == TokenType.ARROW) {
                        lexer.consume();
                        Signature ret = parseSignatureNotation(lexer);
                        other = other.applyGenericReturn(0, ret);
                    }
                    ParserUtil.checkType(lexer.consume(), TokenType.GREATER_THAN_OPERATOR);
                }
            }
            signature = signature.and(other);

            if (lexer.peek().getType() == TokenType.SEPARATOR && lexer.peek(2).getType() != TokenType.TYPE) {
                lexer.consume();
            }
        }
        return signature;
    }

    public static boolean hasPrecedence(TokenType first, TokenType second) {
        if (!PRECEDENCE.containsKey(first)) return false;
        Map<TokenType, Boolean> pre = PRECEDENCE.get(first);
        if (!pre.containsKey(second)) return false;
        return pre.get(second);
    }
}
