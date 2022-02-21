package com.dfsek.substrate.lang.node.expression.function;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;

import java.util.Collection;
import java.util.Collections;

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
        lambda.simplify().apply(builder, data);

        data.loadImplementationArguments(builder);

        ParserUtil.checkWeakReferenceType(lambda, Signature.fun());

        Signature returnType = lambda.reference().getSimpleReturn();

        Signature args = lambda.reference().getGenericArguments(0);

        data.lambdaFactory().invoke(args, returnType, data, builder);
    }

    @Override
    public Position getPosition() {
        return lambda.getPosition();
    }

    @Override
    public Signature reference() {
        return lambda.reference();
    }

    @Override
    public Collection<? extends Node> contents() {
        return Collections.singleton(lambda);
    }
}
