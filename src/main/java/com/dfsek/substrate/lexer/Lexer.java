package com.dfsek.substrate.lexer;

import com.dfsek.substrate.lexer.exceptions.EOFException;
import com.dfsek.substrate.lexer.exceptions.FormatException;
import com.dfsek.substrate.lexer.exceptions.TokenizerException;
import com.dfsek.substrate.lexer.read.Char;
import com.dfsek.substrate.lexer.read.Lookahead;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.lexer.token.Token;
import com.dfsek.substrate.lexer.token.TokenType;
import com.dfsek.substrate.parser.exception.ParseException;

import java.io.StringReader;
import java.util.List;
import java.util.Set;
import java.util.*;

import static io.vavr.API.*;

public class Lexer {
    public static final Set<Character> syntaxSignificant;

    static {
        syntaxSignificant = new HashSet<>();
        syntaxSignificant.addAll(Arrays.asList(';', '(', ')', '"', ',', '\\', '=', '{', '}', '+', '-', '*', '/', '>', '<', '!', ':', '.', '$', '[', ']')); // Reserved chars
    }

    private final Lookahead reader;
    private final List<Token> cache = new ArrayList<>();
    private Token current;

    public Lexer(String data) throws ParseException {
        reader = new Lookahead(new StringReader(data + '\0'));
        cache.add(fetch());
    }

    /**
     * Get the first token.
     *
     * @return First token
     * @throws ParseException If token does not exist
     */
    public Token peek() throws ParseException {
        if (!hasNext()) throw new ParseException("Unexpected end of input", current.getPosition());
        return cache.get(0);
    }

    public Token peek(int n) throws ParseException {
        while (cache.size() <= n) {
            if (!hasNext()) throw new ParseException("Unexpected end of input", current.getPosition());
            cache.add(fetch());
        }
        Token result = cache.get(n);
        if (result == null) {
            throw new ParseException("Unexpected end of input", current.getPosition());
        }
        return result;
    }

    /**
     * Consume (get and remove) the first token.
     *
     * @return First token
     * @throws ParseException If token does not exist
     */
    public Token consume() throws ParseException {
        if (!hasNext()) throw new ParseException("Unexpected end of input", current.getPosition());
        cache.add(fetch());
        current = cache.remove(0);
        return current;
    }

    /**
     * Whether this {@code Tokenizer} contains additional tokens.
     *
     * @return {@code true} if more tokens are present, otherwise {@code false}
     */
    public boolean hasNext() {
        return cache.size() != 0 && cache.get(0) != null;
    }

    private Match.Case<Char, Token> match(String match, TokenType tokenType) {
        return Case($(c -> reader.matches(match, true)), c ->
                new Token(match, tokenType, reader.getPosition()));
    }

    private Match.Case<Char, Token> charMatch(char match, TokenType tokenType) {
        return Case($(c -> c.getCharacter() == match), c ->
                new Token("" + reader.consume(), tokenType, reader.getPosition()));
    }

    private Match.Case<String, Token> literalMatch(io.vavr.collection.Set<String> literal, TokenType tokenType) {
        return Case($(s -> literal.toStream().find(s::equals).isDefined()), s ->
                new Token(s, tokenType, reader.getPosition()));
    }

    private Match.Case<String, Token> literalMatch(String literal, TokenType tokenType) {
        return Case($(literal::equals), s ->
                new Token(s, tokenType, reader.getPosition()));
    }

    private Token fetch() throws TokenizerException {
        while (!reader.current().isEOF() && reader.current().isWhitespace()) {
            reader.consume();
        }

        while (reader.matches("//", true)) {
            skipLine(); // Skip line if comment
        }

        if (reader.matches("/*", true)) {
            skipTo("*/"); // Skip multi line comment
        }

        return Match(reader.current()).of(
                Case($(Char::isEOF), c -> null),
                Case($(c -> isNumberStart()), c -> parseNumber()),
                Case($(c -> c.is('"')), c -> parseString()),
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
                Case($(), c -> Match(parseWord()).of(
                        literalMatch(Set("true", "false"), TokenType.BOOLEAN),

                        literalMatch("return", TokenType.RETURN),

                        literalMatch("num", TokenType.NUM_TYPE),
                        literalMatch("int", TokenType.INT_TYPE),
                        literalMatch("bool", TokenType.BOOL_TYPE),
                        literalMatch("str", TokenType.STRING_TYPE),
                        literalMatch("fun", TokenType.FUN_TYPE),
                        literalMatch("list", TokenType.LIST_TYPE),

                        literalMatch("if", TokenType.IF),
                        literalMatch("else", TokenType.ELSE),

                        Case($(), s -> new Token(s, TokenType.IDENTIFIER, reader.getPosition()))
                ))
        );
    }

    private String parseWord() {
        StringBuilder token = new StringBuilder();
        while (!reader.current().isEOF() && !isSyntaxSignificant(reader.current().getCharacter())) {
            Char c = reader.consume();
            if (c.isWhitespace()) break;
            token.append(c);
        }
        return token.toString();
    }

    private Token parseNumber() {
        StringBuilder num = new StringBuilder();
        while (!reader.current().isEOF() && isNumberLike()) {
            num.append(reader.consume());
        }
        String number = num.toString();
        if (number.contains(".")) {
            return new Token(num.toString(), TokenType.NUMBER, new Position(reader.getLine(), reader.getIndex()));
        } else {
            return new Token(num.toString(), TokenType.INT, new Position(reader.getLine(), reader.getIndex()));
        }
    }

    private Token parseString() {
        reader.consume(); // Consume first quote
        StringBuilder string = new StringBuilder();
        boolean ignoreNext = false;
        while ((!reader.current().is('"')) || ignoreNext) {
            if (reader.current().is('\\') && !ignoreNext) {
                ignoreNext = true;
                reader.consume();
                continue;
            } else ignoreNext = false;
            if (reader.current().isEOF())
                throw new FormatException("No end of string literal found. ", new Position(reader.getLine(), reader.getIndex()));
            string.append(reader.consume());
        }
        reader.consume(); // Consume last quote

        return new Token(string.toString(), TokenType.STRING, new Position(reader.getLine(), reader.getIndex()));
    }

    private boolean isNumberLike() {
        if (reader.current().is('.') && reader.next(1).is('.')) return false; // range
        return reader.current().isDigit()
                || reader.current().is('_', '.', 'E');
    }

    private boolean isNumberStart() {
        return reader.current().isDigit()
                || reader.current().is('.') && reader.next(1).isDigit();
    }

    private void skipLine() {
        while (!reader.current().isEOF() && !reader.current().isNewLine()) reader.consume();
        consumeWhitespace();
    }

    private void consumeWhitespace() {
        while (!reader.current().isEOF() && reader.current().isWhitespace()) reader.consume(); // Consume whitespace.
    }

    private void skipTo(String s) throws EOFException {
        Position begin = new Position(reader.getLine(), reader.getIndex());
        while (!reader.current().isEOF()) {
            if (reader.matches(s, true)) {
                consumeWhitespace();
                return;
            }
            reader.consume();
        }
        throw new EOFException("No end of expression found.", begin);
    }

    public boolean isSyntaxSignificant(char c) {
        return syntaxSignificant.contains(c);
    }

}
