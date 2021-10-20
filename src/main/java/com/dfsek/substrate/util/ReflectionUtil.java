package com.dfsek.substrate.util;

public class ReflectionUtil {
    public static String internalName(Class<?> clazz) {
        return clazz.getCanonicalName().replace('.', '/');
    }
}
