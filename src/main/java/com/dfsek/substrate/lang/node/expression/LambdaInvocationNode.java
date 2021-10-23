package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import org.objectweb.asm.MethodVisitor;

/**
 * immediately invokes a lambda
 */
public class LambdaInvocationNode extends ExpressionNode{
    private final LambdaExpressionNode lambda;

    public LambdaInvocationNode(LambdaExpressionNode lambda) {
        this.lambda = lambda;
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        lambda.apply(visitor, data);

        Signature returnType = lambda.returnType(data);
        String ret = returnType.internalDescriptor();

        if (!returnType.isSimple()) {
            if (returnType.equals(Signature.empty())) ret = "V";
            else ret = "L" + CompilerUtil.internalName(data.tupleFactory().generate(returnType)) + ";";
        }

        visitor.visitMethodInsn(INVOKEINTERFACE,
                CompilerUtil.internalName(data.lambdaFactory().generate(lambda.getParameters(), returnType)),
                "apply",
                "(" + lambda.getParameters().internalDescriptor() + ")" + ret,
                true);
    }

    @Override
    public Position getPosition() {
        return lambda.getPosition();
    }

    @Override
    public Signature returnType(BuildData data) {
        return lambda.returnType(data);
    }
}
