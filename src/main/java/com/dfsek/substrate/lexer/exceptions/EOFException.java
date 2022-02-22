package com.dfsek.substrate.lexer.exceptions;

import com.dfsek.substrate.lexer.read.Position;

public class EOFException extends TokenizerException {

    private static final long serialVersionUID = 3980047409902809440L;

    public EOFException(String message, Position position) {
        super(message, position);
    }
}
