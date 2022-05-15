package com.dfsek.substrate.lang.node.expression.binary.comparison;

import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lexer.token.Token;

public class LessThanOrEqualsNode extends ComparisonBinaryNode {
    private LessThanOrEqualsNode(Unchecked<? extends ExpressionNode> left, Unchecked<? extends ExpressionNode> right, Token op) {
        super(left, right, op);
    }

    public static Unchecked<LessThanOrEqualsNode> of(Unchecked<? extends ExpressionNode> left, Unchecked<? extends ExpressionNode> right, Token op) {
        return Unchecked.of(new LessThanOrEqualsNode(left, right, op));
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
