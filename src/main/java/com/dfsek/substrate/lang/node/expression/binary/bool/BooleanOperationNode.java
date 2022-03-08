package com.dfsek.substrate.lang.node.expression.binary.bool;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.constant.BooleanNode;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.lexer.token.Token;

import java.util.Arrays;
import java.util.Collection;

public abstract class BooleanOperationNode extends ExpressionNode {
    private final Token op;
    protected ExpressionNode left;
    protected ExpressionNode right;

    public BooleanOperationNode(ExpressionNode left, ExpressionNode right, Token op) {
        this.left = left.simplify();
        this.right = right.simplify();
        this.op = op;
    }

    @Override
    public ExpressionNode simplify() {
        if (Node.disableOptimisation()) return this;
        if (left instanceof BooleanNode && right instanceof BooleanNode) {
            return new BooleanNode(apply(((BooleanNode) left).getValue(), ((BooleanNode) right).getValue()), left.getPosition());
        }

        return this;
    }

    @Override
    public Signature reference() {
        return Signature.bool();
    }

    @Override
    public Position getPosition() {
        return op.getPosition();
    }

    @Override
    public Collection<? extends Node> contents() {
        return Arrays.asList(left, right);
    }

    public abstract boolean apply(boolean left, boolean right);
}
