package com.dfsek.substrate.lang.node.expression.binary.comparison;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.binary.BinaryOperationNode;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public abstract class ComparisonBinaryNode extends BinaryOperationNode {
    public ComparisonBinaryNode(ExpressionNode left, ExpressionNode right, Token op) {
        super(left, right, op);
    }

    @Override
    public void applyOp(MethodVisitor visitor, BuildData data) {
        Signature leftType = ParserUtil.checkType(left, data, Signature.integer(), Signature.decimal(), Signature.string())
                .reference(data).getSimpleReturn();
        Signature rightType = ParserUtil.checkType(right, data, Signature.integer(), Signature.decimal(), Signature.string())
                .reference(data).getSimpleReturn();

        ParserUtil.checkType(left, data, rightType);

        if (leftType.equals(Signature.integer())) {
            Label f = new Label();
            Label t = new Label();
            visitor.visitJumpInsn(intInsn(), f);
            visitor.visitInsn(ICONST_1);
            visitor.visitJumpInsn(GOTO, t);
            visitor.visitLabel(f);
            visitor.visitInsn(ICONST_0);
            visitor.visitLabel(t);
        } else if (leftType.equals(Signature.decimal())) {
            Label f = new Label();
            Label t = new Label();
            visitor.visitInsn(DCMPL);
            visitor.visitJumpInsn(doubleInsn(), f);
            visitor.visitInsn(ICONST_1);
            visitor.visitJumpInsn(GOTO, t);
            visitor.visitLabel(f);
            visitor.visitInsn(ICONST_0);
            visitor.visitLabel(t);
        } else if (leftType.equals(Signature.string())) {
            if(string()) {
                applyStrComparison(visitor);
            } else {
                throw new ParseException("Cannot apply operation " + op.getType() + " to type STR", op.getPosition());
            }
        }
    }

    protected boolean string() {
        return false;
    }

    protected abstract int intInsn();

    protected abstract int doubleInsn();

    protected void applyStrComparison(MethodVisitor visitor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Signature reference(BuildData data) {
        return Signature.bool();
    }
}
