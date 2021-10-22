package com.dfsek.substrate.tokenizer;

import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.exceptions.EOFException;
import com.dfsek.substrate.tokenizer.exceptions.FormatException;
import com.dfsek.substrate.tokenizer.exceptions.TokenizerException;

import java.io.StringReader;
import java.util.*;

public class Tokenizer {
    public static final Set<Character> syntaxSignificant;

    static {
        syntaxSignificant = new HashSet<>();
        syntaxSignificant.addAll(Arrays.asList(';', '(', ')', '"', ',', '\\', '=', '{', '}', '+', '-', '*', '/', '>', '<', '!', ':', '.')); // Reserved chars
    }

    private final Lookahead reader;
    private final List<Token> cache = new ArrayList<>();
    private Token current;

    public Tokenizer(String data) throws ParseException {
        reader = new com.dfsek.substrate.tokenizer.Lookahead(new StringReader(data + '\0'));
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
        return cache.get(n);
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

        if (reader.current().isEOF()) {
            return null; // EOF
        }

        if (reader.matches("==", true)) {
            return new Token("==", Token.Type.EQUALS_OPERATOR, new Position(reader.getLine(), reader.getIndex()));
        }
        if (reader.matches("!=", true)) {
            return new Token("!=", Token.Type.NOT_EQUALS_OPERATOR, new Position(reader.getLine(), reader.getIndex()));
        }
        if (reader.matches(">=", true)) {
            return new Token(">=", Token.Type.GREATER_THAN_OR_EQUALS_OPERATOR, new Position(reader.getLine(), reader.getIndex()));
        }
        if (reader.matches("<=", true)) {
            return new Token("<=", Token.Type.LESS_THAN_OR_EQUALS_OPERATOR, new Position(reader.getLine(), reader.getIndex()));
        }
        if (reader.matches(">", true)) {
            return new Token(">", Token.Type.GREATER_THAN_OPERATOR, new Position(reader.getLine(), reader.getIndex()));
        }
        if (reader.matches("<", true)) {
            return new Token("<", Token.Type.LESS_THAN_OPERATOR, new Position(reader.getLine(), reader.getIndex()));
        }


        if (reader.matches("||", true)) {
            return new Token("||", Token.Type.BOOLEAN_OR, new Position(reader.getLine(), reader.getIndex()));
        }
        if (reader.matches("&&", true)) {
            return new Token("&&", Token.Type.BOOLEAN_AND, new Position(reader.getLine(), reader.getIndex()));
        }

        if (reader.matches("->", true)) {
            return new Token("->", Token.Type.ARROW, new Position(reader.getLine(), reader.getIndex()));
        }

        if (reader.matches("..", true)) {
            return new Token("..", Token.Type.RANGE, new Position(reader.getLine(), reader.getIndex()));
        }


        if (isNumberStart()) {
            StringBuilder num = new StringBuilder();
            while (!reader.current().isEOF() && isNumberLike()) {
                num.append(reader.consume());
            }
            String number = num.toString();
            if (number.contains(".")) {
                return new Token(num.toString(), Token.Type.NUMBER, new Position(reader.getLine(), reader.getIndex()));
            } else {
                return new Token(num.toString(), Token.Type.INT, new Position(reader.getLine(), reader.getIndex()));
            }
        }

        if (reader.current().is('"')) {
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

            return new Token(string.toString(), Token.Type.STRING, new Position(reader.getLine(), reader.getIndex()));
        }

        switch (reader.current().getCharacter()) {
            case '(':
                return new Token(reader.consume().toString(), Token.Type.GROUP_BEGIN, new Position(reader.getLine(), reader.getIndex()));
            case ')':
                return new Token(reader.consume().toString(), Token.Type.GROUP_END, new Position(reader.getLine(), reader.getIndex()));
            case ';':
                return new Token(reader.consume().toString(), Token.Type.STATEMENT_END, new Position(reader.getLine(), reader.getIndex()));
            case ',':
                return new Token(reader.consume().toString(), Token.Type.SEPARATOR, new Position(reader.getLine(), reader.getIndex()));
            case '{':
                return new Token(reader.consume().toString(), Token.Type.BLOCK_BEGIN, new Position(reader.getLine(), reader.getIndex()));
            case '}':
                return new Token(reader.consume().toString(), Token.Type.BLOCK_END, new Position(reader.getLine(), reader.getIndex()));
            case '=':
                return new Token(reader.consume().toString(), Token.Type.ASSIGNMENT, new Position(reader.getLine(), reader.getIndex()));
            case '+':
                return new Token(reader.consume().toString(), Token.Type.ADDITION_OPERATOR, new Position(reader.getLine(), reader.getIndex()));
            case '-':
                return new Token(reader.consume().toString(), Token.Type.SUBTRACTION_OPERATOR, new Position(reader.getLine(), reader.getIndex()));
            case '*':
                return new Token(reader.consume().toString(), Token.Type.MULTIPLICATION_OPERATOR, new Position(reader.getLine(), reader.getIndex()));
            case '/':
                return new Token(reader.consume().toString(), Token.Type.DIVISION_OPERATOR, new Position(reader.getLine(), reader.getIndex()));
            case '%':
                return new Token(reader.consume().toString(), Token.Type.MODULO_OPERATOR, new Position(reader.getLine(), reader.getIndex()));
            case '!':
                return new Token(reader.consume().toString(), Token.Type.BOOLEAN_NOT, new Position(reader.getLine(), reader.getIndex()));
            case ':':
                return new Token(reader.consume().toString(), Token.Type.TYPE, new Position(reader.getLine(), reader.getIndex()));
        }


        StringBuilder token = new StringBuilder();
        while (!reader.current().isEOF() && !isSyntaxSignificant(reader.current().getCharacter())) {
            com.dfsek.substrate.tokenizer.Char c = reader.consume();
            if (c.isWhitespace()) break;
            token.append(c);
        }

        String tokenString = token.toString();

        if (tokenString.equals("true")) {
            return new Token(tokenString, Token.Type.BOOLEAN, new Position(reader.getLine(), reader.getIndex()));
        }
        if (tokenString.equals("false")) {
            return new Token(tokenString, Token.Type.BOOLEAN, new Position(reader.getLine(), reader.getIndex()));
        }

        if (tokenString.equals("return")) {
            return new Token(tokenString, Token.Type.RETURN, new Position(reader.getLine(), reader.getIndex()));
        }
        if (tokenString.equals("continue")) {
            return new Token(tokenString, Token.Type.CONTINUE, new Position(reader.getLine(), reader.getIndex()));
        }
        if (tokenString.equals("break")) {
            return new Token(tokenString, Token.Type.BREAK, new Position(reader.getLine(), reader.getIndex()));
        }
        if (tokenString.equals("fail")) {
            return new Token(tokenString, Token.Type.FAIL, new Position(reader.getLine(), reader.getIndex()));
        }


        if (tokenString.equals("num")) {
            return new Token(tokenString, Token.Type.NUM_TYPE, new Position(reader.getLine(), reader.getIndex()));
        }
        if (tokenString.equals("int")) {
            return new Token(tokenString, Token.Type.INT_TYPE, new Position(reader.getLine(), reader.getIndex()));
        }
        if (tokenString.equals("bool")) {
            return new Token(tokenString, Token.Type.BOOL_TYPE, new Position(reader.getLine(), reader.getIndex()));
        }
        if (tokenString.equals("str")) {
            return new Token(tokenString, Token.Type.STRING_TYPE, new Position(reader.getLine(), reader.getIndex()));
        }
        if (tokenString.equals("fun")) {
            return new Token(tokenString, Token.Type.FUN_TYPE, new Position(reader.getLine(), reader.getIndex()));
        }


        return new Token(tokenString, Token.Type.IDENTIFIER, new Position(reader.getLine(), reader.getIndex()));
    }

    private boolean isNumberLike() {
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
