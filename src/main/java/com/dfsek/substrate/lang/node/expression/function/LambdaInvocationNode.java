package com.dfsek.substrate.lang.node.expression.function;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;

/**
 * immediately invokes a lambda
 */
public class LambdaInvocationNode extends ExpressionNode {
    private final LambdaExpressionNode lambda;

    public LambdaInvocationNode(LambdaExpressionNode lambda) {
        this.lambda = lambda;
    }

    @Override
    public void apply(MethodBuilder builder, BuildData data) throws ParseException {
        lambda.apply(builder, data);

        CompilerUtil.invokeLambda(lambda, builder, data);
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
