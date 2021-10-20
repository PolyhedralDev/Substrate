package com.dfsek.substrate.lang.compiler.lambda;

import com.dfsek.substrate.lang.compiler.value.Function;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.util.ReflectionUtil;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class LocalLambdaReferenceFunction implements Function {
    private final Signature args;
    private final Signature returnType;
    private final String id;

    public LocalLambdaReferenceFunction(Signature args, Signature returnType, String id) {
        this.args = args;
        this.returnType = returnType;
        this.id = id;
    }

    @Override
    public Signature arguments() {
        return args;
    }

    @Override
    public void preArgsPrep(MethodVisitor visitor, BuildData data) {
        visitor.visitVarInsn(ALOAD, data.offset(id));
    }

    @Override
    public void invoke(MethodVisitor visitor, BuildData data) {
        String ret = returnType.internalDescriptor();

        if(!returnType.isSimple()) {
            if(returnType.equals(Signature.empty())) ret = "V";
            else ret = "L" + ReflectionUtil.internalName(data.tupleFactory().generate(returnType)) + ";";
        }

        visitor.visitMethodInsn(INVOKEINTERFACE,
                ReflectionUtil.internalName(data.lambdaFactory().generate(args, returnType)),
                "apply",
                "(" + args.internalDescriptor() + ")" + ret,
                true);
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
