package com.dfsek.substrate.lang.node.expression.binary.arithmetic;

import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.binary.NumericBinaryNode;
import com.dfsek.substrate.lexer.token.Token;

public class ModulusNode extends NumericBinaryNode {
    private ModulusNode(Unchecked<? extends ExpressionNode> left, Unchecked<? extends ExpressionNode> right, Token op) {
        super(left, right, op);
    }

    public static Unchecked<ModulusNode> of(Unchecked<? extends ExpressionNode> left, Unchecked<? extends ExpressionNode> right, Token op) {
        return Unchecked.of(new ModulusNode(left, right, op));
    }

    @Override
    protected int intOp() {
        return IREM;
    }

    @Override
    protected int doubleOp() {
        return DREM;
    }

    @Override
    public double apply(double left, double right) {
        return left % right;
    }

    @Override
    public int apply(int left, int right) {
        return left % right;
    }
}
