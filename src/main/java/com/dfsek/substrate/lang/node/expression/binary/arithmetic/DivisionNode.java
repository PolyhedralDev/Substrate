package com.dfsek.substrate.lang.node.expression.binary.arithmetic;

import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.binary.NumericBinaryNode;
import com.dfsek.substrate.lexer.token.Token;

public class DivisionNode extends NumericBinaryNode {
    private DivisionNode(Unchecked<? extends ExpressionNode> left, Unchecked<? extends ExpressionNode> right, Token op) {
        super(left, right, op);
    }

    public static Unchecked<DivisionNode> of(Unchecked<? extends ExpressionNode> left, Unchecked<? extends ExpressionNode> right, Token op) {
        return Unchecked.of(new DivisionNode(left, right, op));
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
