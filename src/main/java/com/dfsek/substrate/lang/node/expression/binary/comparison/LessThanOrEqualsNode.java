package com.dfsek.substrate.lang.node.expression.binary.comparison;

import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lexer.token.Token;

public class LessThanOrEqualsNode extends ComparisonBinaryNode {
    public LessThanOrEqualsNode(ExpressionNode left, ExpressionNode right, Token op) {
        super(left, right, op);
    }

    @Override
    protected int intInsn() {
        return IF_ICMPGT;
    }

    @Override
    protected int doubleInsn() {
        return IFGT;
    }
}
