package com.dfsek.substrate.lang.node.expression.binary.arithmetic;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.binary.NumericBinaryNode;
import com.dfsek.substrate.lang.node.expression.constant.DecimalNode;
import com.dfsek.substrate.lang.node.expression.constant.IntegerNode;
import com.dfsek.substrate.lang.node.expression.error.ErrorNode;
import com.dfsek.substrate.lexer.token.Token;

public class MultiplyNode extends NumericBinaryNode {
    private MultiplyNode(Unchecked<? extends ExpressionNode> left, Unchecked<? extends ExpressionNode> right, Token op) {
        super(left, right, op);
    }

    public static Unchecked<MultiplyNode> of(Unchecked<? extends ExpressionNode> left, Unchecked<? extends ExpressionNode> right, Token op) {
        return Unchecked.of(new MultiplyNode(left, right, op));
    }

    @Override
    protected int intOp() {
        return IMUL;
    }

    @Override
    protected int doubleOp() {
        return DMUL;
    }

    @Override
    public double apply(double left, double right) {
        return left * right;
    }

    @Override
    public int apply(int left, int right) {
        return left * right;
    }

    @Override
    public ExpressionNode simplify() {
        if (Node.disableOptimisation() || left instanceof ErrorNode || right instanceof ErrorNode) return this;
        if ((left instanceof IntegerNode && ((IntegerNode) left).getValue() == 0)
                || (right instanceof IntegerNode && ((IntegerNode) right).getValue() == 0)) {
            return IntegerNode.of(0, left.getPosition()).get(Signature.integer()); // 0 * a == 0
        }
        if ((left instanceof DecimalNode && ((DecimalNode) left).getValue() == 0)
                || (right instanceof DecimalNode && ((DecimalNode) right).getValue() == 0)) {
            return DecimalNode.of(0, left.getPosition()).get(Signature.decimal()); // 0 * a == 0
        }

        return super.simplify();
    }
}
