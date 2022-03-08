package com.dfsek.substrate.lexer.exceptions;


import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.exception.ParseException;

public abstract class TokenizerException extends ParseException {

    private static final long serialVersionUID = 2792384010083575420L;

    public TokenizerException(String message, Position position) {
        super(message, position);
    }
}
