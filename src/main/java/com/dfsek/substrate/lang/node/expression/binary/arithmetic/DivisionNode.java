package com.dfsek.substrate.lang.node.expression.binary.arithmetic;

import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.binary.NumericBinaryNode;
import com.dfsek.substrate.lexer.token.Token;

public class DivisionNode extends NumericBinaryNode {
    public DivisionNode(ExpressionNode left, ExpressionNode right, Token op) {
        super(left, right, op);
    }

    @Override
    public double apply(double left, double right) {
        return left / right;
    }

    @Override
    public int apply(int left, int right) {
        return left / right;
    }

    @Override
    protected int intOp() {
        return IDIV;
    }

    @Override
    protected int doubleOp() {
        return DDIV;
    }
}
