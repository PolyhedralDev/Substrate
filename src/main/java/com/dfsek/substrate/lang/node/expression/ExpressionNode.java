package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.compiler.type.Typed;

public abstract class ExpressionNode extends NodeHolder implements Typed {
    @Override
    public ExpressionNode simplify() {
        return this;
    }
}
