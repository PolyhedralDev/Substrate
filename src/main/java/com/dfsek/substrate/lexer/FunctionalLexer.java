package com.dfsek.substrate.lexer;


import com.dfsek.substrate.lexer.exceptions.TokenizerException;
import com.dfsek.substrate.lexer.read.Char;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.lexer.token.Token;
import com.dfsek.substrate.lexer.token.TokenType;
import io.vavr.Function1;
import io.vavr.Predicates;
import io.vavr.Tuple2;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import io.vavr.collection.Stream;

import static io.vavr.API.*;

public class FunctionalLexer {
    public static final Set<Character> syntaxSignificant = HashSet.of(';', '(', ')', '"', ',', '\\', '=', '{', '}', '+', '-', '*', '/', '>', '<', '!', ':', '.', '$', '[', ']');
    private final Stream<Char> data;
    private final Token current;

    public FunctionalLexer(String data) {
        this(Function(() -> {
            int line = 1;
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
            return chars.toStream();
        }).apply());
    }

    public FunctionalLexer(Stream<Char> data) {
        Tuple2<Stream<Char>, Token> fetch = fetch(Match(data.dropWhile(
                Predicates.anyOf(
                        CharOperations::isEOF,
                        CharOperations::isWhitespace
                )
        )).of(
                Case(startsWith("//"), d -> d.dropWhile(c -> c.getCharacter() != '\n')),
                Case(startsWith("/*"), d -> d.subSequence(d.map(Char::getCharacter).indexOfSlice(CharSeq("*/")))),
                Case($(), Function1.identity())
        ));
        this.data = fetch._1;

        this.current = fetch._2;
    }

    private static Match.Pattern0<Stream<Char>> startsWith(String start) {
        return $(s -> s.map(Char::getCharacter).startsWith(CharSeq(start)));
    }

    private <T> Match.Case<T, Tuple2<Stream<Char>, Token>> match(Stream<Char> chars, String match, TokenType tokenType) {
        return Case($(c -> chars.map(Char::getCharacter).startsWith(CharSeq(match))), c ->
                new Tuple2<>(chars.drop(match.length()), new Token(match, tokenType, chars.get().getPosition())));
    }

    private <T> Match.Case<T, Tuple2<Stream<Char>, Token>> charMatch(Stream<Char> chars, char match, TokenType tokenType) {
        return Case($(c -> chars.get().getCharacter() == match), c ->
                new Tuple2<>(chars.tail(), new Token("" + chars.get().getCharacter(), tokenType, chars.get().getPosition())));
    }

    private <T> Match.Case<T, Tuple2<Stream<Char>, Token>> literalMatch(Stream<Char> chars, String literal, TokenType tokenType) {
        return Case($(t -> chars.map(Char::getCharacter).startsWith(CharSeq(literal))), s ->
                new Tuple2<>(chars.drop(literal.length()), new Token(literal, tokenType, chars.get().getPosition())));
    }

    private String parseWord(Stream<Char> chars) {
        return chars.takeUntil(Predicates.anyOf(Char::isEOF, Char::isWhitespace, c -> syntaxSignificant.contains(c.getCharacter()))).map(Char::getCharacter).toCharSeq().mkString();
    }

    private Tuple2<Stream<Char>, Token> parseNumber(Stream<Char> chars) {
        Stream<Char> numbers = chars;

        StringBuilder num = new StringBuilder();
        while (!chars.get().isEOF() && isNumberLike(numbers)) {
            numbers = numbers.tail();
            num.append(numbers.get());
        }
        String number = num.toString();
        if (number.contains(".")) {
            return new Tuple2<>(numbers, new Token(num.toString(), TokenType.NUMBER, new Position(chars.get().getLine(), chars.get().getIndex())));
        } else {
            return new Tuple2<>(numbers, new Token(num.toString(), TokenType.INT, new Position(chars.get().getLine(), chars.get().getIndex())));
        }
    }

    private Tuple2<Stream<Char>, Token> parseString(Stream<Char> chars) {
        Stream<Char> str = chars.tail().takeUntil(c -> c.is('"', '\\'));
        Stream<Char> remaining = chars.drop(str.length() + 2);
        return new Tuple2<>(remaining, new Token(str.map(Char::getCharacter).toCharSeq().mkString(), TokenType.STRING, new Position(chars.get().getLine(), chars.get().getIndex())));
    }

    private boolean isNumberLike(Stream<Char> chars) {
        if (chars.get().is('.') && chars.get(1).is('.')) return false; // range
        return chars.get().isDigit()
                || chars.get().is('_', '.', 'E');
    }

    private boolean isNumberStart(Stream<Char> chars) {
        return chars.get().isDigit()
                || chars.get().is('.') && chars.get(1).isDigit();
    }

    private Tuple2<Stream<Char>, Token> fetch(Stream<Char> chars) throws TokenizerException {
        return Match(chars).of(
                Case($(c -> c.get().isEOF()), c -> null),
                Case($(this::isNumberStart), this::parseNumber),
                Case($(c -> c.get().is('"')), this::parseString),
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
                    return new Tuple2<>(chars.drop(word.length()), new Token(word, TokenType.IDENTIFIER, chars.get().getPosition()));
                })
        );
    }

    public FunctionalLexer next() {
        return new FunctionalLexer(data);
    }

    public Token current() {
        return current;
    }
}
