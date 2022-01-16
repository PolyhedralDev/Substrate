package com.dfsek.substrate.parser;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.DataType;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Typed;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Positioned;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

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

    public static Token checkType(Token token, Token.Type... expected) throws ParseException {
        for (Token.Type type : expected) if (token.getType().equals(type)) return token;
        throw new ParseException("Expected " + Arrays.toString(expected) + " but found " + token, token.getPosition());
    }

    public static <T extends Typed & Positioned> T checkType(T typed, BuildData data, Signature... expected) throws ParseException {
        Signature ref = typed.reference(data).getSimpleReturn();
        for (Signature type : expected) if (ref.equals(type)) return typed;
        throw new ParseException("Expected type(s) " + Arrays.toString(expected) + " but found " + ref, typed.getPosition());
    }

    public static <T extends Typed & Positioned> T checkReferenceType(T typed, BuildData data, Signature... expected) throws ParseException {
        Signature ref = typed.reference(data);
        for (Signature type : expected) if (ref.equals(type)) return typed;
        throw new ParseException("Expected type(s) " + Arrays.toString(expected) + " but found " + ref, typed.getPosition());
    }

    public static <T extends Typed & Positioned> T checkWeakReferenceType(T typed, BuildData data, Signature... expected) throws ParseException {
        Signature ref = typed.reference(data);
        for (Signature type : expected) if (ref.weakEquals(type)) return typed;
        throw new ParseException("Expected type(s) " + Arrays.toString(expected) + " but found " + ref, typed.getPosition());
    }

    public static Signature parseSignatureNotation(Tokenizer tokenizer) {
        Signature signature = Signature.empty();
        while(tokenizer.peek().isType()) {
            Token type = checkType(tokenizer.consume(), Token.Type.INT_TYPE, Token.Type.NUM_TYPE, Token.Type.STRING_TYPE, Token.Type.BOOL_TYPE, Token.Type.FUN_TYPE, Token.Type.LIST_TYPE);
            Signature other = new Signature(DataType.fromToken(type));
            if(!(other.weakEquals(Signature.integer())
                    || other.weakEquals(Signature.bool())
                    || other.weakEquals(Signature.decimal())
                    || other.weakEquals(Signature.string()))) {
                ParserUtil.checkType(tokenizer.consume(), Token.Type.LESS_THAN_OPERATOR);
                if(other.weakEquals(Signature.list())) {
                    other = other.applyGenericReturn(0, parseSignatureNotation(tokenizer));
                } else if(other.weakEquals(Signature.fun())) {
                    other = other.applyGenericArgument(0, parseSignatureNotation(tokenizer));
                    if(tokenizer.peek().getType() == Token.Type.ARROW) {
                        tokenizer.consume();
                        Signature ret = parseSignatureNotation(tokenizer);
                        other = other.applyGenericReturn(0, ret);
                    }
                }
                ParserUtil.checkType(tokenizer.consume(), Token.Type.GREATER_THAN_OPERATOR);
            }
            signature = signature.and(other);

            if(tokenizer.peek().getType() == Token.Type.SEPARATOR &&tokenizer.peek(2).getType() != Token.Type.TYPE) {
                tokenizer.consume();
            }
        }
        return signature;
    }

    public static boolean hasPrecedence(Token.Type first, Token.Type second) {
        if (!PRECEDENCE.containsKey(first)) return false;
        Map<Token.Type, Boolean> pre = PRECEDENCE.get(first);
        if (!pre.containsKey(second)) return false;
        return pre.get(second);
    }
}
