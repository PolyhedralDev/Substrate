package com.dfsek.substrate.lang.node.expression.binary.comparison;

import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lexer.token.Token;

public class LessThanNode extends ComparisonBinaryNode {
    private LessThanNode(Unchecked<? extends ExpressionNode> left, Unchecked<? extends ExpressionNode> right, Token op) {
        super(left, right, op);
    }

    public static Unchecked<LessThanNode> of(Unchecked<? extends ExpressionNode> left, Unchecked<? extends ExpressionNode> right, Token op) {
        return Unchecked.of(new LessThanNode(left, right, op));
    }
    @Override
    protected int intInsn() {
        return IF_ICMPGE;
    }

    @Override
    protected int doubleInsn() {
        return IFGE;
    }

    @Override
    public String toString() {
        return "(" + left.toString() + " < " + right.toString() + ")";
    }
}
