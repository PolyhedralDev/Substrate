package com.dfsek.substrate.lang.compiler.codegen.ops;

import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.parser.DynamicClassLoader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ClassBuilder {
    private final List<MethodBuilder> methods = new ArrayList<>();
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
        this.classWriter = CompilerUtil.generateClass(name, iface, false, interfaces);
        this.name = name;
    }

    public ClassBuilder defaultConstructor() {
        method("<init>", "()V").access(MethodBuilder.Access.PUBLIC)
                .aLoad(0)
                .invokeSpecial("java/lang/Object",
                        "<init>",
                        "()V")
                .voidReturn();

        return this;
    }

    public MethodBuilder method(String name, String descriptor) {
        return method(name, descriptor, null);
    }

    public MethodBuilder method(String name, String descriptor, BiConsumer<LocalVariable, MethodBuilder> delegateLoader) {
        return method(name, descriptor, null, delegateLoader, EMPTY);
    }

    public MethodBuilder method(String name, String descriptor, String signature, String... exceptions) {
        return method(name, descriptor, signature,  (a, b) -> {
            throw new IllegalStateException();
        }, exceptions);
    }

    public MethodBuilder method(String name, String descriptor, String signature, BiConsumer<LocalVariable, MethodBuilder> delegateLoader, String... exceptions) {
        MethodBuilder builder = new MethodBuilder(this, name, descriptor, signature, exceptions, delegateLoader);
        methods.add(builder);
        return builder;
    }

    public ClassBuilder field(String name, String descriptor, MethodBuilder.Access... accesses) {
        return field(name, descriptor, null, null, accesses);
    }

    public ClassBuilder field(String name, String descriptor, Object value, MethodBuilder.Access... accesses) {
        return field(name, descriptor, null, value, accesses);
    }

    public ClassBuilder field(String name, String descriptor, String signature, Object value, MethodBuilder.Access... accesses) {
        int access = 0;

        for (MethodBuilder.Access level : accesses) {
            access |= level.getCode();
        }

        int finalAccess = access;
        fields.add(classWriter1 -> classWriter1.visitField(finalAccess, name, descriptor, signature, value).visitEnd());
        return this;
    }

    public Class<?> build(DynamicClassLoader loader) {
        methods.forEach(methodBuilder -> {
            MethodVisitor visitor = classWriter.visitMethod(methodBuilder.access(),
                    methodBuilder.getName(),
                    methodBuilder.getDescriptor(),
                    methodBuilder.getSignature(),
                    methodBuilder.getExceptions());
            visitor.visitCode();
            methodBuilder.apply(visitor);
            visitor.visitMaxs(0, 0);
        });

        fields.forEach(consumer -> consumer.accept(classWriter));
        byte[] bytes = classWriter.toByteArray();
        Class<?> clazz = loader.defineClass(name.replace('/', '.'), bytes);
        CompilerUtil.dump(clazz, bytes);
        return clazz;
    }
}
