package com.dfsek.substrate.lang.node.expression.binary.arithmetic;

import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.binary.NumericBinaryNode;
import com.dfsek.substrate.tokenizer.Token;

public class MultiplyNode extends NumericBinaryNode {
    public MultiplyNode(ExpressionNode left, ExpressionNode right, Token op) {
        super(left, right, op);
    }

    @Override
    protected int intOp() {
        return IMUL;
    }

    @Override
    protected int doubleOp() {
        return DMUL;
    }
}