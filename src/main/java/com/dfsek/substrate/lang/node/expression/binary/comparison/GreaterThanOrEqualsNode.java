package com.dfsek.substrate.lang.node.expression.binary.comparison;

import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lexer.token.Token;

public class GreaterThanOrEqualsNode extends ComparisonBinaryNode {
    private GreaterThanOrEqualsNode(Unchecked<? extends ExpressionNode> left, Unchecked<? extends ExpressionNode> right, Token op) {
        super(left, right, op);
    }

    public static Unchecked<GreaterThanOrEqualsNode> of(Unchecked<? extends ExpressionNode> left, Unchecked<? extends ExpressionNode> right, Token op) {
        return Unchecked.of(new GreaterThanOrEqualsNode(left, right, op));
    }
    @Override
    protected int intInsn() {
        return IF_ICMPLT;
    }

    @Override
    protected int doubleInsn() {
        return IFLT;
    }

    @Override
    public String toString() {
        return "(" + left.toString() + " >= " + right.toString() + ")";
    }
}
