package com.dfsek.substrate.lang.compiler.util;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

public final class CompilerUtil {
    public static String internalName(Class<?> clazz) {
        return clazz.getCanonicalName().replace('.', '/');
    }

    public static void dump(Class<?> clazz, byte[] bytes) {
        File dump = new File("./.substrate/dumps/" + internalName(clazz) + ".class");
        dump.getParentFile().mkdirs();
        System.out.println("Dumping to " + dump.getAbsolutePath());
        try {
            IOUtils.write(bytes, new FileOutputStream(dump));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
