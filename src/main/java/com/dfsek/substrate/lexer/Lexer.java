package com.dfsek.substrate.lexer;

import com.dfsek.substrate.lexer.token.Token;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.Stream;

public class Lexer {
    private Stream<Token> tokens;
    private Token last;

    public Lexer(String data) throws ParseException {
        tokens = FunctionalLexer.stream(data);
    }

    /**
     * Get the first token.
     *
     * @return First token
     * @throws ParseException If token does not exist
     */
    public Token peek() throws ParseException {
        return tokens.get();
    }

    public Token peek(int n) throws ParseException {
        return tokens.get(n);
    }

    /**
     * Consume (get and remove) the first token.
     *
     * @return First token
     * @throws ParseException If token does not exist
     */
    public Token consume() throws ParseException {
        Token token = tokens.get();
        tokens = tokens.tail();
        return token;
    }

    /**
     * Whether this {@code Tokenizer} contains additional tokens.
     *
     * @return {@code true} if more tokens are present, otherwise {@code false}
     */
    public boolean hasNext() {
        return !tokens.isEmpty();
    }
}
