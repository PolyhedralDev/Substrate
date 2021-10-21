package com.dfsek.substrate.lang.std.function;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.DataType;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.value.Function;
import org.objectweb.asm.MethodVisitor;

public class PrintlnTest implements Function {
    public static void test(String string, double d, boolean bool, double d2, String s2) {
        System.out.println("String: " + string + ", " + s2);
        System.out.println("Double: " + d + ", " + d2);
        System.out.println("Boolean: " + bool);
    }

    @Override
    public Signature arguments() {
        return new Signature(DataType.STR, DataType.NUM, DataType.BOOL, DataType.NUM, DataType.STR);
    }

    @Override
    public void preArgsPrep(MethodVisitor visitor, BuildData data) {

    }

    @Override
    public void invoke(MethodVisitor visitor, BuildData data) {
        visitor.visitMethodInsn(INVOKESTATIC,
                "com/dfsek/substrate/lang/std/function/PrintlnTest",
                "test",
                "(Ljava/lang/String;DZDLjava/lang/String;)V",
                false);
    }

    @Override
    public Signature returnType() {
        return new Signature();
    }
}
