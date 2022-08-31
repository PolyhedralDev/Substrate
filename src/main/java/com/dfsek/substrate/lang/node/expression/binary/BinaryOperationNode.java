package com.dfsek.substrate.lang.node.expression.binary;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.lexer.token.Token;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import io.vavr.control.Either;

import java.util.Arrays;
import java.util.Collection;

public abstract class BinaryOperationNode extends ExpressionNode {
    protected final ExpressionNode left;
    protected final ExpressionNode right;

    protected final Token op;

    protected BinaryOperationNode(Unchecked<? extends ExpressionNode> left, Unchecked<? extends ExpressionNode> right, Token op) {
        this.left = check(left);
        this.right = Unchecked.of(check(right)).get(left.reference());
        this.op = op;
    }

    @Override
    public List<Either<CompileError, Op>> apply(BuildData data, LinkedHashMap<String, Value> values) throws ParseException {
        return left.apply(data, values)
                .appendAll(right.apply(data, values))
                .appendAll(applyOp(data));
    }

    protected abstract ExpressionNode check(Unchecked<? extends ExpressionNode> unchecked);

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
