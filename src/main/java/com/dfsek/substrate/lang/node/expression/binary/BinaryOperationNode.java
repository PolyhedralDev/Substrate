package com.dfsek.substrate.lang.node.expression.binary;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.lexer.token.Token;
import io.vavr.collection.List;
import io.vavr.control.Either;

import java.util.Arrays;
import java.util.Collection;

public abstract class BinaryOperationNode extends ExpressionNode {
    protected final ExpressionNode left;
    protected final ExpressionNode right;

    protected final Token op;

    public BinaryOperationNode(ExpressionNode left, ExpressionNode right, Token op) {
        this.left = left.simplify();
        this.right = right.simplify();
        this.op = op;
    }

    @Override
    public List<Either<CompileError, Op>> apply(BuildData data) throws ParseException {
        return left.apply(data)
                .appendAll(right.apply(data))
                .appendAll(applyOp(data));
    }

    public abstract List<Either<CompileError, Op>> applyOp(BuildData data);

    @Override
    public Position getPosition() {
        return op.getPosition();
    }

    @Override
    public Collection<Node> contents() {
        return Arrays.asList(left, right);
    }
}
