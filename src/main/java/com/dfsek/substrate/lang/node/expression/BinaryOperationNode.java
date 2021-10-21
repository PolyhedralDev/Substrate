package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.tokenizer.Token;
import org.objectweb.asm.MethodVisitor;

public abstract class BinaryOperationNode extends ExpressionNode {
    protected final ExpressionNode left;
    protected final ExpressionNode right;

    private final Token op;

    public BinaryOperationNode(ExpressionNode left, ExpressionNode right, Token op) {
        this.left = left;
        this.right = right;
        this.op = op;
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        left.apply(visitor, data);
        right.apply(visitor, data);
        applyOp(visitor, data);
    }

    public abstract void applyOp(MethodVisitor visitor, BuildData data);

    @Override
    public Position getPosition() {
        return op.getPosition();
    }
}
