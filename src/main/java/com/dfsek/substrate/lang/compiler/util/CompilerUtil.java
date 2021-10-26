package com.dfsek.substrate.lang.compiler.util;

import org.apache.commons.io.IOUtils;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

public final class CompilerUtil implements Opcodes {
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

    public static void pushInt(int i, MethodVisitor visitor) {
        if (i == -1) {
            visitor.visitInsn(ICONST_M1);
        } else if (i == 0) {
            visitor.visitInsn(ICONST_0);
        } else if (i == 1) {
            visitor.visitInsn(ICONST_1);
        } else if (i == 2) {
            visitor.visitInsn(ICONST_2);
        } else if (i == 3) {
            visitor.visitInsn(ICONST_3);
        } else if (i == 4) {
            visitor.visitInsn(ICONST_4);
        } else if (i == 5) {
            visitor.visitInsn(ICONST_5);
        } else if (i >= -128 && i < 128) {
            visitor.visitIntInsn(BIPUSH, i); // byte
        } else if (i >= -32768 && i < 32768) {
            visitor.visitIntInsn(SIPUSH, i); // short
        } else {
            visitor.visitLdcInsn(i); // constant pool
        }
    }
}
