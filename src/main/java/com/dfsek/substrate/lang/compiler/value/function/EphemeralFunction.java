package com.dfsek.substrate.lang.compiler.value.function;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
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
        return signature.getGenericArguments(0);
    }

    @Override
    public void preArgsPrep(MethodVisitor visitor, BuildData data) {
        visitor.visitVarInsn(ALOAD, offset);
    }

    @Override
    public void invoke(MethodVisitor visitor, BuildData data, Signature args, List<ExpressionNode> argExpressions) {
        Signature returnType = signature.getGenericReturn(0);

        data.lambdaFactory().invoke(signature.getGenericArguments(0), returnType.expandTuple(), data, visitor);
    }

    @Override
    public Signature reference() {
        return signature;
    }
}
