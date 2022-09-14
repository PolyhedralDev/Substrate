package com.dfsek.substrate.lang.node.expression.function;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.ParserScope;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.List;
import io.vavr.control.Either;

import java.util.Collection;
import java.util.Collections;

/**
 * immediately invokes a lambda
 */
public class LambdaInvocationNode extends ExpressionNode {
    private final ExpressionNode lambda;

    private LambdaInvocationNode(Unchecked<? extends ExpressionNode> lambda) {
        this.lambda = lambda.weak(Signature.fun());
    }

    public static Unchecked<LambdaInvocationNode> of(Unchecked<? extends ExpressionNode> lambda) {
        return Unchecked.of(new LambdaInvocationNode(lambda));
    }

    @Override
    public List<Either<CompileError, Op>> apply(BuildData data, ParserScope scope) throws ParseException {
        Signature returnType = lambda.reference().getSimpleReturn();

        Signature args = lambda.reference().getGenericArguments(0);

        return lambda.simplify().apply(data, scope)
                .append(Op.aLoad(data.getImplArgsOffset()))
                .append(data.lambdaFactory().invoke(args, returnType, data));
    }

    @Override
    public Position getPosition() {
        return lambda.getPosition();
    }

    @Override
    public Signature reference() {
        return lambda.reference().getGenericReturn(0);
    }

    @Override
    public Collection<? extends Node> contents() {
        return Collections.singleton(lambda);
    }
}
