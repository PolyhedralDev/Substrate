package com.dfsek.substrate.tokenizer;

public class Position {
    private static final Position NULL = new Position(0, 0) {
        @Override
        public String toString() {
            return "NULL";
        }
    };


    private final int line;
    private final int index;

    public Position(int line, int index) {
        this.line = line;
        this.index = index;
    }

    @Override
    public String toString() {
        return (line + 1) + ":" + index;
    }


    public int getLine() {
        return line;
    }

    public static Position getNull() {
        return NULL;
    }
}
