package com.dfsek.substrate.lexer;


import com.dfsek.substrate.lexer.exceptions.TokenizerException;
import com.dfsek.substrate.lexer.read.Char;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.lexer.token.Token;
import com.dfsek.substrate.lexer.token.TokenType;
import io.vavr.Function1;
import io.vavr.Predicates;
import io.vavr.Tuple2;
import io.vavr.collection.*;
import io.vavr.control.Option;

import static io.vavr.API.*;

public class FunctionalLexer {
    private static final Set<Character> syntaxSignificant = HashSet.of(';', '(', ')', '"', ',', '\\', '=', '{', '}', '+', '-', '*', '/', '>', '<', '!', ':', '.', '$', '[', ']');

    public static Stream<Token> stream(String data) {
        int line = 0;
        int index = 0;
        List<Char> chars = List();
        for (char c : data.toCharArray()) {
            if (c == '\n') {
                index = 0;
                line++;
            }
            index++;
            chars = chars.append(new Char(c, index, line));
        }
        return stream(chars.toStream());
    }

    public static Stream<Token> stream(Stream<Char> data) {
        return Stream.unfoldRight(data, (newData -> fetch(Match((newData).dropWhile(
                Predicates.anyOf(
                        CharOperations::isEOF,
                        CharOperations::isWhitespace
                )
        )).of(
                Case(startsWith("//"), d -> d.dropWhile(c -> c.getCharacter() != '\n')),
                Case(startsWith("/*"), d -> d.subSequence(d.map(Char::getCharacter).indexOfSlice(CharSeq("*/")))),
                Case($(), Function1.identity())
        ))));
    }

    private static Match.Pattern0<Stream<Char>> startsWith(String start) {
        return $(s -> s.map(Char::getCharacter).startsWith(CharSeq(start)));
    }

    private static <T> Match.Case<T, Option<Tuple2<Token, Stream<Char>>>> match(Stream<Char> chars, String match, TokenType tokenType) {
        return Case($(c -> chars.map(Char::getCharacter).startsWith(CharSeq(match))), c ->
                Option.of(new Tuple2<>(new Token(match, tokenType, chars.get().getPosition()), chars.drop(match.length()))));
    }

    private static <T> Match.Case<T, Option<Tuple2<Token, Stream<Char>>>> charMatch(Stream<Char> chars, char match, TokenType tokenType) {
        return Case($(c -> chars.get().getCharacter() == match), c ->
                Option.of(new Tuple2<>(new Token("" + chars.get().getCharacter(), tokenType, chars.get().getPosition()), chars.tail())));
    }

    private static <T> Match.Case<T, Option<Tuple2<Token, Stream<Char>>>> literalMatch(Stream<Char> chars, String literal, TokenType tokenType) {
        return Case($(t -> chars.map(Char::getCharacter).startsWith(CharSeq(literal))), s ->
                Option.of(new Tuple2<>(new Token(literal, tokenType, chars.get().getPosition()), chars.drop(literal.length()))));
    }

    private static String parseWord(Stream<Char> chars) {
        return chars.takeUntil(Predicates.anyOf(Char::isEOF, Char::isWhitespace, c -> syntaxSignificant.contains(c.getCharacter()))).map(Char::getCharacter).toCharSeq().mkString();
    }

    private static Option<Tuple2<Token, Stream<Char>>> parseNumber(Stream<Char> chars) {
        Stream<Char> numbers = chars;

        StringBuilder num = new StringBuilder();
        while (!chars.get().isEOF() && isNumberLike(numbers)) {
            num.append(numbers.get());
            numbers = numbers.tail();
        }
        String number = num.toString();
        if (number.contains(".")) {
            return Option.of(new Tuple2<>(new Token(num.toString(), TokenType.NUMBER, new Position(chars.get().getLine(), chars.get().getIndex())), numbers));
        } else {
            return Option.of(new Tuple2<>(new Token(num.toString(), TokenType.INT, new Position(chars.get().getLine(), chars.get().getIndex())), numbers));
        }
    }

    private static Option<Tuple2<Token, Stream<Char>>> parseString(Stream<Char> chars) {
        Stream<Char> str = chars.tail().takeUntil(c -> c.is('"', '\\'));
        Stream<Char> remaining = chars.drop(str.length() + 2);
        return Option.of(new Tuple2<>(new Token(str.map(Char::getCharacter).toCharSeq().mkString(), TokenType.STRING, new Position(chars.get().getLine(), chars.get().getIndex())), remaining));
    }

    private static boolean isNumberLike(Stream<Char> chars) {
        if (chars.get().is('.') && chars.get(1).is('.')) return false; // range
        return chars.get().isDigit()
                || chars.get().is('_', '.', 'E');
    }

    private static boolean isNumberStart(Stream<Char> chars) {
        return chars.get().isDigit()
                || chars.get().is('.') && chars.get(1).isDigit();
    }

    private static Option<Tuple2<? extends Token, ? extends Stream<Char>>> fetch(Stream<Char> chars) throws TokenizerException {
        return Option.narrow(Match(chars).of(
                Case($(Stream::isEmpty), c -> Option.none()),
                Case($(c -> c.get().isEOF()), c -> Option.none()),
                Case($(FunctionalLexer::isNumberStart), FunctionalLexer::parseNumber),
                Case($(c -> c.get().is('"')), FunctionalLexer::parseString),
                match(chars, "->", TokenType.ARROW),
                match(chars, "..", TokenType.RANGE),

                match(chars, "==", TokenType.EQUALS_OPERATOR),
                match(chars, "!=", TokenType.NOT_EQUALS_OPERATOR),
                match(chars, ">=", TokenType.GREATER_THAN_OR_EQUALS_OPERATOR),
                match(chars, "<=", TokenType.LESS_THAN_OR_EQUALS_OPERATOR),
                charMatch(chars, '>', TokenType.GREATER_THAN_OPERATOR),
                charMatch(chars, '<', TokenType.LESS_THAN_OPERATOR),

                match(chars, "||", TokenType.BOOLEAN_OR),
                match(chars, "&&", TokenType.BOOLEAN_AND),
                match(chars, "!", TokenType.BOOLEAN_NOT),

                charMatch(chars, '+', TokenType.ADDITION_OPERATOR),
                charMatch(chars, '-', TokenType.SUBTRACTION_OPERATOR),
                charMatch(chars, '/', TokenType.DIVISION_OPERATOR),
                charMatch(chars, '*', TokenType.MULTIPLICATION_OPERATOR),
                charMatch(chars, '%', TokenType.MODULO_OPERATOR),

                charMatch(chars, '(', TokenType.GROUP_BEGIN),
                charMatch(chars, ')', TokenType.GROUP_END),

                charMatch(chars, '[', TokenType.LIST_BEGIN),
                charMatch(chars, ']', TokenType.LIST_END),

                charMatch(chars, '{', TokenType.BLOCK_BEGIN),
                charMatch(chars, '}', TokenType.BLOCK_END),

                charMatch(chars, ';', TokenType.STATEMENT_END),
                charMatch(chars, ',', TokenType.SEPARATOR),

                charMatch(chars, '=', TokenType.ASSIGNMENT),
                charMatch(chars, ':', TokenType.TYPE),
                literalMatch(chars, "false", TokenType.BOOLEAN),
                literalMatch(chars, "true", TokenType.BOOLEAN),

                literalMatch(chars, "return", TokenType.RETURN),

                literalMatch(chars, "num", TokenType.NUM_TYPE),
                literalMatch(chars, "int", TokenType.INT_TYPE),
                literalMatch(chars, "bool", TokenType.BOOL_TYPE),
                literalMatch(chars, "str", TokenType.STRING_TYPE),
                literalMatch(chars, "fun", TokenType.FUN_TYPE),
                literalMatch(chars, "list", TokenType.LIST_TYPE),

                literalMatch(chars, "if", TokenType.IF),
                literalMatch(chars, "else", TokenType.ELSE),

                Case($(), () -> {
                    String word = parseWord(chars);
                    return Option.of(new Tuple2<>(new Token(word, TokenType.IDENTIFIER, chars.get().getPosition()), chars.drop(word.length())));
                })
        ));
    }
}
