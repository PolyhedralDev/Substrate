package com.dfsek.substrate.lang.node.expression.binary.bool;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.tokenizer.Token;

import java.util.Arrays;
import java.util.Collection;

public abstract class BooleanOperationNode extends ExpressionNode {
    protected ExpressionNode left;
    protected ExpressionNode right;
    private final Token op;

    public BooleanOperationNode(ExpressionNode left, ExpressionNode right, Token op) {
        this.left = left;
        this.right = right;
        this.op = op;
    }

    @Override
    public Signature reference(BuildData data) {
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
}
