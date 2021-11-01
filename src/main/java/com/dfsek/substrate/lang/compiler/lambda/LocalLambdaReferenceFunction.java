package com.dfsek.substrate.lang.compiler.lambda;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.compiler.value.Function;
import com.dfsek.substrate.lang.internal.Lambda;
import com.dfsek.substrate.lang.internal.Tuple;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import org.objectweb.asm.MethodVisitor;

import java.util.List;

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
        if(data.isShadowed(id)) {
            visitor.visitVarInsn(ALOAD, 0);
            visitor.visitFieldInsn(GETFIELD,
                    data.getClassName(),
                    "scope" + data.getShadowField(id),
                    data.getShadowValue(id).reference().internalDescriptor());
        } else {
            visitor.visitVarInsn(ALOAD, data.offset(id));
        }
    }

    @Override
    public void invoke(MethodVisitor visitor, BuildData data, Signature args, List<ExpressionNode> argExpressions) {
        String ret = returnType.internalDescriptor();

        if (returnType.equals(Signature.empty())) ret = "V";
        else if(returnType.weakEquals(Signature.tup())) ret = "L" + CompilerUtil.internalName(data.tupleFactory().generate(returnType.expandTuple())) + ";";


        visitor.visitMethodInsn(INVOKEINTERFACE,
                CompilerUtil.internalName(data.lambdaFactory().generate(this.args, returnType.expandTuple())),
                "apply",
                "(" + this.args.internalDescriptor() + ")" + ret,
                true);
        if (returnType.weakEquals(Signature.tup())) { // tuple deconstruction
            data.offsetInc(1);
            int offset = data.getOffset();
            visitor.visitVarInsn(ASTORE, offset);

            Signature tup = returnType.expandTuple();

            for (int i = 0; i < tup.size(); i++) {
                visitor.visitVarInsn(ALOAD, offset);

                visitor.visitMethodInsn(INVOKEVIRTUAL,
                        CompilerUtil.internalName(Tuple.class) + "IMPL_" + tup.classDescriptor(),
                        "param" + i,
                        "()" + tup.getType(i).descriptor(),
                        false);
            }
        }
    }

    @Override
    public Signature reference() {
        return Signature.fun().applyGenericArgument(0, args).applyGenericReturn(0, returnType);
    }

    @Override
    public boolean ephemeral() {
        return false;
    }
}
