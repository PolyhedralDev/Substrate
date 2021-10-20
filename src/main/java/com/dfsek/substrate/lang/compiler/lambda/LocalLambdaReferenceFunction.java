package com.dfsek.substrate.lang.compiler.lambda;

import com.dfsek.substrate.lang.compiler.value.Function;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.util.ReflectionUtil;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class LocalLambdaReferenceFunction implements Function {
    private final Class<?> lambda;
    private final Signature args;
    private final Signature returnType;

    private final int offset;

    public LocalLambdaReferenceFunction(Class<?> lambda, Signature args, Signature returnType, int offset) {
        this.lambda = lambda;
        this.args = args;
        this.returnType = returnType;
        this.offset = offset;
    }

    @Override
    public Signature arguments() {
        return args;
    }

    @Override
    public void preArgsPrep(MethodVisitor visitor, BuildData data) {
        visitor.visitVarInsn(ALOAD, offset);
    }

    @Override
    public void invoke(MethodVisitor visitor, BuildData data) {
        String ret = returnType.internalDescriptor();

        if(!returnType.isSimple()) {
            if(returnType.equals(Signature.empty())) ret = "V";
            else ret = "L" + ReflectionUtil.internalName(data.tupleFactory().generate(returnType)) + ";";
        }

        visitor.visitMethodInsn(INVOKEVIRTUAL,
                ReflectionUtil.internalName(lambda),
                "apply",
                "(" + args.internalDescriptor() + ")" + ret,
                false);
    }

    @Override
    public void generate(ClassWriter writer, BuildData data) {

    }

    @Override
    public Signature returnType() {
        return returnType;
    }

    @Override
    public boolean ephemeral() {
        return false;
    }
}
