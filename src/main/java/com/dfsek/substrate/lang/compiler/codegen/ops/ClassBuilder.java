package com.dfsek.substrate.lang.compiler.codegen.ops;

import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.parser.DynamicClassLoader;
import org.objectweb.asm.ClassWriter;

import java.util.ArrayList;
import java.util.List;

public class ClassBuilder {
    private final List<MethodBuilder> methods = new ArrayList<>();
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

    public MethodBuilder method(String name, String descriptor) {
        return method(name, descriptor, null);
    }

    public MethodBuilder method(String name, String descriptor, String signature, String... exceptions) {
        MethodBuilder builder = new MethodBuilder(this, name, descriptor, signature, exceptions);
        methods.add(builder);
        return builder;
    }

    public Class<?> build() {
        methods.forEach(methodBuilder -> methodBuilder.apply(classWriter.visitMethod(methodBuilder.access(),
                methodBuilder.getName(),
                methodBuilder.getDescriptor(),
                methodBuilder.getSignature(),
                methodBuilder.getExceptions())));
        DynamicClassLoader dynamicClassLoader = new DynamicClassLoader();
        byte[] bytes = classWriter.toByteArray();
        Class<?> clazz = dynamicClassLoader.defineClass(name, bytes);
        CompilerUtil.dump(clazz, bytes);
        return clazz;
    }
}
