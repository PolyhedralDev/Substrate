package com.dfsek.substrate.lang.node.expression.binary.comparison;

import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lexer.token.Token;

public class GreaterThanNode extends ComparisonBinaryNode {
    private GreaterThanNode(Unchecked<? extends ExpressionNode> left, Unchecked<? extends ExpressionNode> right, Token op) {
        super(left, right, op);
    }

    public static Unchecked<GreaterThanNode> of(Unchecked<? extends ExpressionNode> left, Unchecked<? extends ExpressionNode> right, Token op) {
        return Unchecked.of(new GreaterThanNode(left, right, op));
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
