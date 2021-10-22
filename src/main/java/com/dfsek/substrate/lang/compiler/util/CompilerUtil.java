package com.dfsek.substrate.lang.compiler.util;

public final class CompilerUtil {
    public static String internalName(Class<?> clazz) {
        return clazz.getCanonicalName().replace('.', '/');
    }
}
