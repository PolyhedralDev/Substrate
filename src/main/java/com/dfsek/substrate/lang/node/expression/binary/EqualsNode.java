package com.dfsek.substrate.lang.node.expression.binary;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.BinaryOperationNode;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class EqualsNode extends BinaryOperationNode {
    public EqualsNode(ExpressionNode left, ExpressionNode right, Token op) {
        super(left, right, op);
    }

    @Override
    public void applyOp(MethodVisitor visitor, BuildData data) {
        Signature leftType = left.referenceType(data).getSimpleReturn();
        Signature rightType = right.referenceType(data).getSimpleReturn();

        if(!rightType.equals(leftType)) {
            throw new ParseException("Expected " + leftType + ", got " + rightType, right.getPosition());
        }

        if (leftType.equals(Signature.integer())) {
            Label f = new Label();
            Label t = new Label();
            visitor.visitJumpInsn(IF_ICMPNE, f);
            visitor.visitInsn(ICONST_1);
            visitor.visitJumpInsn(GOTO, t);
            visitor.visitLabel(f);
            visitor.visitInsn(ICONST_0);
            visitor.visitLabel(t);
        } else if(leftType.equals(Signature.decimal())){
            Label f = new Label();
            Label t = new Label();
            visitor.visitInsn(DCMPL);
            visitor.visitJumpInsn(IFNE, f);
            visitor.visitInsn(ICONST_1);
            visitor.visitJumpInsn(GOTO, t);
            visitor.visitLabel(f);
            visitor.visitInsn(ICONST_0);
            visitor.visitLabel(t);
        } else if(leftType.equals(Signature.string())) {
            visitor.visitMethodInsn(INVOKEVIRTUAL,
                    "java/lang/String",
                    "equals",
                    "(Ljava/lang/Object;)Z",
                    false);
        } else {
            throw new ParseException("Expected [INT, NUM, STR], got " + leftType, left.getPosition());
        }
    }

    @Override
    public Signature referenceType(BuildData data) {
        return Signature.bool();
    }
}
