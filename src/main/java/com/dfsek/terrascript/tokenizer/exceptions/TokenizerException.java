package com.dfsek.terrascript.tokenizer.exceptions;


import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Position;

public abstract class TokenizerException extends ParseException {

    private static final long serialVersionUID = 2792384010083575420L;

    public TokenizerException(String message, Position position) {
        super(message, position);
    }

    public TokenizerException(String message, Position position, Throwable cause) {
        super(message, position, cause);
    }
}
