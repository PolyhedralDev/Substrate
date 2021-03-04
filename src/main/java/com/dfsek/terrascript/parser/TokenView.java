package com.dfsek.terrascript.parser;

import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Token;
import com.dfsek.terrascript.tokenizer.Tokenizer;

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
