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

        CompilerUtil.invokeLambda(lambda, visitor, data);
    }

    @Override
    public Position getPosition() {
        return lambda.getPosition();
    }

    @Override
    public Signature reference(BuildData data) {
        return lambda.reference(data);
    }
}
