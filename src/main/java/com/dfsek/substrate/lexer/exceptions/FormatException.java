package com.dfsek.substrate.lexer.exceptions;

import com.dfsek.substrate.lexer.read.Position;

public class FormatException extends TokenizerException {

    private static final long serialVersionUID = -791308012940744455L;

    public FormatException(String message, Position position) {
        super(message, position);
    }
}
