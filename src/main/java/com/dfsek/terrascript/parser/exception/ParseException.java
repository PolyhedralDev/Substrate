package com.dfsek.terrascript.parser.exception;

import com.dfsek.terrascript.tokenizer.Position;

public class ParseException extends Exception {
    private static final long serialVersionUID = 6744390543046766386L;
    private final Position position;

    public ParseException(String message, Position position) {
        super(message);
        this.position = position;
    }

    public ParseException(String message, Position position, Throwable cause) {
        super(message, cause);
        this.position = position;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + ": " + position;
    }

    public Position getPosition() {
        return position;
    }
}
