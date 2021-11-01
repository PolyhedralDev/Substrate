package com.dfsek.substrate.lang.compiler;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.compiler.value.Function;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import org.objectweb.asm.MethodVisitor;

import java.util.List;

public class EphemeralFunction implements Function {
    private final Signature signature;

    private final int offset;

    public EphemeralFunction(Signature signature, int offset) {
        this.signature = signature;
        this.offset = offset;
    }

    @Override
    public Signature arguments() {
        System.out.println(signature.getGenericArguments(0));
        System.out.println(signature);
        return signature.getGenericArguments(0);
    }

    @Override
    public void preArgsPrep(MethodVisitor visitor, BuildData data) {
        visitor.visitVarInsn(ALOAD, offset);
    }

    @Override
    public void invoke(MethodVisitor visitor, BuildData data, Signature args, List<ExpressionNode> argExpressions) {
        Signature returnType = signature.getGenericReturn(0);
        String ret = returnType.internalDescriptor();

        if (returnType.equals(Signature.empty())) ret = "V";
        else if(returnType.weakEquals(Signature.tup())) ret = "L" + CompilerUtil.internalName(data.tupleFactory().generate(returnType.expandTuple())) + ";";


        visitor.visitMethodInsn(INVOKEINTERFACE,
                CompilerUtil.internalName(data.lambdaFactory().generate(signature.getGenericArguments(0), returnType.expandTuple())),
                "apply",
                "(" + signature.getGenericArguments(0).internalDescriptor() + ")" + ret,
                true);
    }

    @Override
    public Signature reference() {
        return signature;
    }
}
