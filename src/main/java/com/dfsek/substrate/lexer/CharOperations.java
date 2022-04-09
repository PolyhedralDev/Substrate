package com.dfsek.substrate.lexer;

import com.dfsek.substrate.lexer.read.Char;

public final class CharOperations {
    public static boolean isEOF(Char character) {
        return character.getCharacter() == '\0';
    }

    public static boolean isWhitespace(Char character) {
        return Character.isWhitespace(character.getCharacter());
    }
}
