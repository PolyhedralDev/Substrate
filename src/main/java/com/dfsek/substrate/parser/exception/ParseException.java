package com.dfsek.substrate.parser.exception;

import com.dfsek.substrate.tokenizer.Position;

public class ParseException extends RuntimeException {
    private static final long serialVersionUID = 6744390543046766386L;
    private final Position position;

    public ParseException(String message, Position position) {
        super(message + ": " + position);
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }
}
