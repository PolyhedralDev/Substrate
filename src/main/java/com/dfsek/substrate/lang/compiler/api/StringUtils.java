package com.dfsek.substrate.lang.compiler.api;

import java.util.Locale;

public final class StringUtils {
    private StringUtils() {
        throw new IllegalStateException();
    }

    public static String substring(String s, int start, int end) {
        return s.substring(start, end);
    }

    public static String toUpperCase(String s) {
        return s.toUpperCase(Locale.ROOT);
    }

    public static String toLowerCase(String s) {
        return s.toLowerCase(Locale.ROOT);
    }
}
