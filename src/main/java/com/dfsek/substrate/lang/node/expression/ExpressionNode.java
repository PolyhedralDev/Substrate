package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.Signature;

public abstract class ExpressionNode implements Node {
    public abstract Signature returnType();
}
