package com.dfsek.substrate.lang.node.expression.constant;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.tokenizer.Token;

public abstract class ConstantExpressionNode implements Node {
    protected final Token token;

    protected ConstantExpressionNode(Token token) {
        this.token = token;
    }

    @Override
    public Position getPosition() {
        return token.getPosition();
    }
}
