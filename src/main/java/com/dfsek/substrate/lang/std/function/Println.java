package com.dfsek.substrate.lang.std.function;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.value.Function;
import org.objectweb.asm.MethodVisitor;

public class Println implements Function {
    @Override
    public Signature arguments() {
        return Signature.string();
    }

    @Override
    public void preArgsPrep(MethodVisitor visitor, BuildData data) {
        visitor.visitFieldInsn(GETSTATIC,
                "java/lang/System",
                "out",
                "Ljava/io/PrintStream;");
    }

    @Override
    public void invoke(MethodVisitor visitor, BuildData data) {
        visitor.visitMethodInsn(INVOKEVIRTUAL,
                "java/io/PrintStream",
                "println",
                "(Ljava/lang/String;)V",
                false);
    }

    @Override
    public Signature returnType() {
        return Signature.empty();
    }
}
