package com.dfsek.terrascript.parser;

import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Token;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ParserUtil {
    private static final Map<Token.Type, Map<Token.Type, Boolean>> PRECEDENCE = new HashMap<>(); // If second has precedence, true.
    private static final List<Token.Type> ARITHMETIC = Arrays.asList(Token.Type.ADDITION_OPERATOR, Token.Type.SUBTRACTION_OPERATOR, Token.Type.MULTIPLICATION_OPERATOR, Token.Type.DIVISION_OPERATOR, Token.Type.MODULO_OPERATOR);
    private static final List<Token.Type> COMPARISON = Arrays.asList(Token.Type.EQUALS_OPERATOR, Token.Type.NOT_EQUALS_OPERATOR, Token.Type.LESS_THAN_OPERATOR, Token.Type.LESS_THAN_OR_EQUALS_OPERATOR, Token.Type.GREATER_THAN_OPERATOR, Token.Type.GREATER_THAN_OR_EQUALS_OPERATOR);

    static { // Setup precedence
        Map<Token.Type, Boolean> add = new HashMap<>(); // Addition/subtraction before Multiplication/division.
        add.put(Token.Type.MULTIPLICATION_OPERATOR, true);
        add.put(Token.Type.DIVISION_OPERATOR, true);

        PRECEDENCE.put(Token.Type.ADDITION_OPERATOR, add);
        PRECEDENCE.put(Token.Type.SUBTRACTION_OPERATOR, add);

        Map<Token.Type, Boolean> numericBoolean = new HashMap<>();

        ARITHMETIC.forEach(op -> numericBoolean.put(op, true)); // Numbers before comparison
        COMPARISON.forEach(op -> PRECEDENCE.put(op, numericBoolean));


        Map<Token.Type, Boolean> booleanOps = new HashMap<>();
        ARITHMETIC.forEach(op -> booleanOps.put(op, true)); // Everything before boolean
        COMPARISON.forEach(op -> booleanOps.put(op, true));


        PRECEDENCE.put(Token.Type.BOOLEAN_AND, booleanOps);
        PRECEDENCE.put(Token.Type.BOOLEAN_OR, booleanOps);
    }

    public static void checkType(Token token, Token.Type... expected) throws ParseException {
        for(Token.Type type : expected) if(token.getType().equals(type)) return;
        throw new ParseException("Expected " + Arrays.toString(expected) + " but found " + token.getType(), token.getPosition());
    }


    /**
     * Checks if token is a binary operator
     *
     * @param token Token to check
     * @throws ParseException If token isn't a binary operator
     */
    public static void checkBinaryOperator(Token token) throws ParseException {
        if(!token.isBinaryOperator())
            throw new ParseException("Expected binary operator, found " + token.getType(), token.getPosition());
    }

    public static boolean hasPrecedence(Token.Type first, Token.Type second) {
        if(!PRECEDENCE.containsKey(first)) return false;
        Map<Token.Type, Boolean> pre = PRECEDENCE.get(first);
        if(!pre.containsKey(second)) return false;
        return pre.get(second);
    }
}
