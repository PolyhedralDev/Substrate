package com.dfsek.substrate.lang.compiler;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
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
    public void invoke(MethodVisitor visitor, BuildData data, Signature args, List<ExpressionNode> argExpressions) {

    }

    @Override
    public Signature reference() {
        return signature;
    }
}
