package com.dfsek.substrate.lang.node.expression.binary.arithmetic;

import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.binary.NumericBinaryNode;
import com.dfsek.substrate.tokenizer.Token;

public class ModulusNode extends NumericBinaryNode {
    public ModulusNode(ExpressionNode left, ExpressionNode right, Token op) {
        super(left, right, op);
    }

    @Override
    protected int intOp() {
        return IREM;
    }

    @Override
    protected int doubleOp() {
        return DREM;
    }
}
