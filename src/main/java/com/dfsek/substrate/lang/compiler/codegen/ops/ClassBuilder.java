package com.dfsek.substrate.lang.compiler.codegen.ops;

import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.parser.DynamicClassLoader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.zip.ZipOutputStream;

@SuppressWarnings("UnusedReturnValue")
public class ClassBuilder implements Opcodes {
    private final List<Consumer<ClassWriter>> fields = new ArrayList<>();
    private final ClassWriter classWriter;
    private static final String[] EMPTY = new String[0];
    private final String name;

    public ClassBuilder(String name) {
        this(name, EMPTY);
    }

    public ClassBuilder(String name, String... interfaces) {
        this(name, false, interfaces);
    }

    public ClassBuilder(String name, boolean iface, String... interfaces) {
        this.classWriter = CompilerUtil.generateClass(name, iface, interfaces);
        this.name = name;
    }

    public ClassBuilder defaultConstructor() {
        MethodVisitor ctor = method("<init>", "()V", Access.PUBLIC);
        ctor.visitCode();
        ctor.visitVarInsn(ALOAD, 0);
        ctor.visitMethodInsn(INVOKESPECIAL,
                "java/lang/Object",
                "<init>",
                "()V",
                false);
        ctor.visitInsn(RETURN);
        ctor.visitMaxs(0, 0);
        return this;
    }

    public ClassBuilder inner(String name, String outerName, String innerName, Access... accesses) {
        int access = 0;

        for (Access level : accesses) {
            access |= level.getCode();
        }
        classWriter.visitInnerClass(name, outerName, innerName, access);
        return this;
    }

    public MethodVisitor method(String name, String descriptor, Access... accesses) {
        return method(name, descriptor, null, EMPTY, accesses);
    }

    public MethodVisitor method(String name, String descriptor, String signature, String[] exceptions, Access... accesses) {
        int access = 0;

        for (Access level : accesses) {
            access |= level.getCode();
        }
        return classWriter.visitMethod(access,
                name,
                descriptor,
                signature,
                exceptions);
    }


    public ClassBuilder field(String name, String descriptor, Access... accesses) {
        return field(name, descriptor, null, null, accesses);
    }

    public ClassBuilder field(String name, String descriptor, Object value, Access... accesses) {
        return field(name, descriptor, null, value, accesses);
    }

    public ClassBuilder field(String name, String descriptor, String signature, Object value, Access... accesses) {
        int access = 0;

        for (Access level : accesses) {
            access |= level.getCode();
        }

        int finalAccess = access;
        fields.add(classWriter1 -> classWriter1.visitField(finalAccess, name, descriptor, signature, value).visitEnd());
        return this;
    }

    public Class<?> build(DynamicClassLoader loader, final ZipOutputStream zipOutputStream) {
        fields.forEach(consumer -> consumer.accept(classWriter));
        byte[] bytes = classWriter.toByteArray();
        Class<?> clazz = loader.defineClass(name.replace('/', '.'), bytes);
        CompilerUtil.dump(name, bytes, zipOutputStream);
        return clazz;
    }

    public String getName() {
        return name;
    }
}
