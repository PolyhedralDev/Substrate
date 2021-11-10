package com.dfsek.substrate.tokenizer;

public class Position {
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
}
