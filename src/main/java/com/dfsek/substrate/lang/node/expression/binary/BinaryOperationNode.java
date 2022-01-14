package com.dfsek.substrate.lang.node.expression.binary;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.tokenizer.Token;

public abstract class BinaryOperationNode extends ExpressionNode {
    protected final ExpressionNode left;
    protected final ExpressionNode right;

    protected final Token op;

    public BinaryOperationNode(ExpressionNode left, ExpressionNode right, Token op) {
        this.left = left;
        this.right = right;
        this.op = op;
    }

    @Override
    public void apply(MethodBuilder builder, BuildData data) throws ParseException {
        left.apply(builder, data);
        right.apply(builder, data);
        applyOp(builder, data);
    }

    public abstract void applyOp(MethodBuilder visitor, BuildData data);

    @Override
    public Position getPosition() {
        return op.getPosition();
    }
}
