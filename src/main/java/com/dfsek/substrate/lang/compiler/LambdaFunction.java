package com.dfsek.substrate.lang.compiler;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class LambdaFunction implements Function {
    @Override
    public Signature arguments() {
        return null;
    }

    @Override
    public void preArgsPrep(MethodVisitor visitor, BuildData data) {

    }

    @Override
    public void invoke(MethodVisitor visitor, BuildData data) {

    }

    @Override
    public void generate(ClassWriter writer, BuildData data) {

    }

    @Override
    public Signature returnType() {
        return null;
    }

    @Override
    public boolean ephemeral() {
        return false;
    }
}
