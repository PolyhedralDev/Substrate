package com.dfsek.substrate.lang.node.expression.constant;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.tokenizer.Token;

import java.util.Collection;
import java.util.Collections;

public abstract class ConstantExpressionNode extends ExpressionNode {
    protected final Token token;

    public ConstantExpressionNode(Token token) {
        this.token = token;
    }

    @Override
    public Position getPosition() {
        return token.getPosition();
    }

    @Override
    public Collection<Node> contents() {
        return Collections.emptyList();
    }
}
