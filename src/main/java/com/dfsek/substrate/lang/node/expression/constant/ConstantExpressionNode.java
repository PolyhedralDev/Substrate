package com.dfsek.substrate.lang.node.expression.constant;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.tokenizer.Position;

import java.util.Collection;
import java.util.Collections;

public abstract class ConstantExpressionNode<T> extends ExpressionNode {
    protected final T value;
    private final Position position;

    public ConstantExpressionNode(T value, Position position) {
        this.value = value;
        this.position = position;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public Collection<Node> contents() {
        return Collections.emptyList();
    }

    public T getValue() {
        return value;
    }

    public abstract Signature reference();

    @Override
    public Signature reference(BuildData data) {
        return reference();
    }
}
