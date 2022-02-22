package com.dfsek.substrate.lang.node.expression.binary.comparison;

import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lexer.token.Token;

public class GreaterThanNode extends ComparisonBinaryNode {
    public GreaterThanNode(ExpressionNode left, ExpressionNode right, Token op) {
        super(left, right, op);
    }

    @Override
    protected int intInsn() {
        return IF_ICMPLE;
    }

    @Override
    protected int doubleInsn() {
        return IFLE;
    }
}
