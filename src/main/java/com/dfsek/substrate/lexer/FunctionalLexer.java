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
        return Stream.unfoldRight(data, FunctionalLexer::fetch);
    }

    private static Match.Pattern0<Stream<Char>> startsWith(String start) {
        return $(s -> s.map(Char::getCharacter).startsWith(CharSeq(start)));
    }

    private static Match.Case<Stream<Char>, Option<Tuple2<Token, Stream<Char>>>> match(String match, TokenType tokenType) {
        return Case($(c -> c.map(Char::getCharacter).startsWith(CharSeq(match))), c ->
                Option.of(new Tuple2<>(new Token(match, tokenType, c.get().getPosition()), c.drop(match.length()))));
    }

    private static Match.Case<Stream<Char>, Option<Tuple2<Token, Stream<Char>>>> charMatch(char match, TokenType tokenType) {
        return Case($(c -> c.get().getCharacter() == match), c ->
                Option.of(new Tuple2<>(new Token("" + c.get().getCharacter(), tokenType, c.get().getPosition()), c.tail())));
    }

    private static Match.Case<Stream<Char>, Option<Tuple2<Token, Stream<Char>>>> literalMatch(String literal, TokenType tokenType) {
        return Case($(c -> c.map(Char::getCharacter).startsWith(CharSeq(literal))), c ->
                Option.of(new Tuple2<>(new Token(literal, tokenType, c.get().getPosition()), c.drop(literal.length()))));
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
        return Option.narrow(Match(chars.dropWhile(
                Predicates.anyOf(
                        CharOperations::isEOF,
                        CharOperations::isWhitespace
                )
        )).of(
                Case($(Stream::isEmpty), c -> Option.none()),
                Case($(c -> c.get().isEOF()), c -> Option.none()),
                Case(startsWith("//"), d -> fetch(d.dropUntil(c -> c.getCharacter() == '\n').tail())),
                Case(startsWith("/*"), d -> fetch(d.subSequence(d.map(Char::getCharacter).indexOfSlice(CharSeq("*/"))).drop(2))),
                Case($(FunctionalLexer::isNumberStart), FunctionalLexer::parseNumber),
                Case($(c -> c.get().is('"')), FunctionalLexer::parseString),
                match("->", TokenType.ARROW),
                match("..", TokenType.RANGE),

                match("==", TokenType.EQUALS_OPERATOR),
                match("!=", TokenType.NOT_EQUALS_OPERATOR),
                match(">=", TokenType.GREATER_THAN_OR_EQUALS_OPERATOR),
                match("<=", TokenType.LESS_THAN_OR_EQUALS_OPERATOR),
                charMatch('>', TokenType.GREATER_THAN_OPERATOR),
                charMatch('<', TokenType.LESS_THAN_OPERATOR),

                match("||", TokenType.BOOLEAN_OR),
                match("&&", TokenType.BOOLEAN_AND),
                match("!", TokenType.BOOLEAN_NOT),

                charMatch('+', TokenType.ADDITION_OPERATOR),
                charMatch('-', TokenType.SUBTRACTION_OPERATOR),
                charMatch('/', TokenType.DIVISION_OPERATOR),
                charMatch('*', TokenType.MULTIPLICATION_OPERATOR),
                charMatch('%', TokenType.MODULO_OPERATOR),

                charMatch('(', TokenType.GROUP_BEGIN),
                charMatch(')', TokenType.GROUP_END),

                charMatch('[', TokenType.LIST_BEGIN),
                charMatch(']', TokenType.LIST_END),

                charMatch('{', TokenType.BLOCK_BEGIN),
                charMatch('}', TokenType.BLOCK_END),

                charMatch(';', TokenType.STATEMENT_END),
                charMatch(',', TokenType.SEPARATOR),

                charMatch('=', TokenType.ASSIGNMENT),
                charMatch(':', TokenType.TYPE),
                literalMatch("false", TokenType.BOOLEAN),
                literalMatch("true", TokenType.BOOLEAN),

                literalMatch("return", TokenType.RETURN),

                literalMatch("num", TokenType.NUM_TYPE),
                literalMatch("int", TokenType.INT_TYPE),
                literalMatch("bool", TokenType.BOOL_TYPE),
                literalMatch("str", TokenType.STRING_TYPE),
                literalMatch("fun", TokenType.FUN_TYPE),
                literalMatch("list", TokenType.LIST_TYPE),

                literalMatch("if", TokenType.IF),
                literalMatch("else", TokenType.ELSE),

                Case($(), c -> {
                    String word = parseWord(c);
                    return Option.of(new Tuple2<>(new Token(word, TokenType.IDENTIFIER, c.get().getPosition()), c.drop(word.length())));
                })
        ));
    }
}
