package com.dfsek.substrate.lexer;

import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.lexer.read.Lookahead;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.lexer.exceptions.EOFException;
import com.dfsek.substrate.lexer.exceptions.FormatException;
import com.dfsek.substrate.lexer.exceptions.TokenizerException;
import com.dfsek.substrate.lexer.read.Char;
import com.dfsek.substrate.lexer.token.Token;
import com.dfsek.substrate.lexer.token.TokenType;

import java.io.StringReader;
import java.util.*;

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
            return new Token("==", TokenType.EQUALS_OPERATOR, new Position(reader.getLine(), reader.getIndex()));
        }
        if (reader.matches("!=", true)) {
            return new Token("!=", TokenType.NOT_EQUALS_OPERATOR, new Position(reader.getLine(), reader.getIndex()));
        }
        if (reader.matches(">=", true)) {
            return new Token(">=", TokenType.GREATER_THAN_OR_EQUALS_OPERATOR, new Position(reader.getLine(), reader.getIndex()));
        }
        if (reader.matches("<=", true)) {
            return new Token("<=", TokenType.LESS_THAN_OR_EQUALS_OPERATOR, new Position(reader.getLine(), reader.getIndex()));
        }
        if (reader.matches(">", true)) {
            return new Token(">", TokenType.GREATER_THAN_OPERATOR, new Position(reader.getLine(), reader.getIndex()));
        }
        if (reader.matches("<", true)) {
            return new Token("<", TokenType.LESS_THAN_OPERATOR, new Position(reader.getLine(), reader.getIndex()));
        }


        if (reader.matches("||", true)) {
            return new Token("||", TokenType.BOOLEAN_OR, new Position(reader.getLine(), reader.getIndex()));
        }
        if (reader.matches("&&", true)) {
            return new Token("&&", TokenType.BOOLEAN_AND, new Position(reader.getLine(), reader.getIndex()));
        }

        if (reader.matches("->", true)) {
            return new Token("->", TokenType.ARROW, new Position(reader.getLine(), reader.getIndex()));
        }

        if (reader.matches("..", true)) {
            return new Token("..", TokenType.RANGE, new Position(reader.getLine(), reader.getIndex()));
        }


        if (isNumberStart()) {
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

            return new Token(string.toString(), TokenType.STRING, new Position(reader.getLine(), reader.getIndex()));
        }

        switch (reader.current().getCharacter()) {
            case '(':
                return new Token(reader.consume().toString(), TokenType.GROUP_BEGIN, new Position(reader.getLine(), reader.getIndex()));
            case ')':
                return new Token(reader.consume().toString(), TokenType.GROUP_END, new Position(reader.getLine(), reader.getIndex()));
            case ';':
                return new Token(reader.consume().toString(), TokenType.STATEMENT_END, new Position(reader.getLine(), reader.getIndex()));
            case ',':
                return new Token(reader.consume().toString(), TokenType.SEPARATOR, new Position(reader.getLine(), reader.getIndex()));
            case '{':
                return new Token(reader.consume().toString(), TokenType.BLOCK_BEGIN, new Position(reader.getLine(), reader.getIndex()));
            case '}':
                return new Token(reader.consume().toString(), TokenType.BLOCK_END, new Position(reader.getLine(), reader.getIndex()));
            case '=':
                return new Token(reader.consume().toString(), TokenType.ASSIGNMENT, new Position(reader.getLine(), reader.getIndex()));
            case '+':
                return new Token(reader.consume().toString(), TokenType.ADDITION_OPERATOR, new Position(reader.getLine(), reader.getIndex()));
            case '-':
                return new Token(reader.consume().toString(), TokenType.SUBTRACTION_OPERATOR, new Position(reader.getLine(), reader.getIndex()));
            case '*':
                return new Token(reader.consume().toString(), TokenType.MULTIPLICATION_OPERATOR, new Position(reader.getLine(), reader.getIndex()));
            case '/':
                return new Token(reader.consume().toString(), TokenType.DIVISION_OPERATOR, new Position(reader.getLine(), reader.getIndex()));
            case '%':
                return new Token(reader.consume().toString(), TokenType.MODULO_OPERATOR, new Position(reader.getLine(), reader.getIndex()));
            case '!':
                return new Token(reader.consume().toString(), TokenType.BOOLEAN_NOT, new Position(reader.getLine(), reader.getIndex()));
            case ':':
                return new Token(reader.consume().toString(), TokenType.TYPE, new Position(reader.getLine(), reader.getIndex()));
            case '[':
                return new Token(reader.consume().toString(), TokenType.LIST_BEGIN, new Position(reader.getLine(), reader.getIndex()));
            case ']':
                return new Token(reader.consume().toString(), TokenType.LIST_END, new Position(reader.getLine(), reader.getIndex()));
        }


        StringBuilder token = new StringBuilder();
        while (!reader.current().isEOF() && !isSyntaxSignificant(reader.current().getCharacter())) {
            Char c = reader.consume();
            if (c.isWhitespace()) break;
            token.append(c);
        }

        String tokenString = token.toString();

        if (tokenString.equals("true")) {
            return new Token(tokenString, TokenType.BOOLEAN, new Position(reader.getLine(), reader.getIndex()));
        }
        if (tokenString.equals("false")) {
            return new Token(tokenString, TokenType.BOOLEAN, new Position(reader.getLine(), reader.getIndex()));
        }

        if (tokenString.equals("return")) {
            return new Token(tokenString, TokenType.RETURN, new Position(reader.getLine(), reader.getIndex()));
        }

        if (tokenString.equals("num")) {
            return new Token(tokenString, TokenType.NUM_TYPE, new Position(reader.getLine(), reader.getIndex()));
        }
        if (tokenString.equals("int")) {
            return new Token(tokenString, TokenType.INT_TYPE, new Position(reader.getLine(), reader.getIndex()));
        }
        if (tokenString.equals("bool")) {
            return new Token(tokenString, TokenType.BOOL_TYPE, new Position(reader.getLine(), reader.getIndex()));
        }
        if (tokenString.equals("str")) {
            return new Token(tokenString, TokenType.STRING_TYPE, new Position(reader.getLine(), reader.getIndex()));
        }
        if (tokenString.equals("fun")) {
            return new Token(tokenString, TokenType.FUN_TYPE, new Position(reader.getLine(), reader.getIndex()));
        }
        if (tokenString.equals("list")) {
            return new Token(tokenString, TokenType.LIST_TYPE, new Position(reader.getLine(), reader.getIndex()));
        }

        if (tokenString.equals("if")) {
            return new Token(tokenString, TokenType.IF, new Position(reader.getLine(), reader.getIndex()));
        }
        if (tokenString.equals("else")) {
            return new Token(tokenString, TokenType.ELSE, new Position(reader.getLine(), reader.getIndex()));
        }


        return new Token(tokenString, TokenType.IDENTIFIER, new Position(reader.getLine(), reader.getIndex()));
    }

    private boolean isNumberLike() {
        if(reader.current().is('.') && reader.next(1).is('.')) return false; // range
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
