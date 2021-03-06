package com.dfsek.substrate.parser;

import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

public class TokenView {
    private final Tokenizer tokenizer;

    public TokenView(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public Token peek() throws ParseException {
        return tokenizer.peek();
    }

    public Token peek(int n) throws ParseException {
        return tokenizer.peek(n);
    }
}
