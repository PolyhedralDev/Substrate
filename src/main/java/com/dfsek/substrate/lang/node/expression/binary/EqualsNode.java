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
        Signature leftType = left.returnType(data);
        Signature rightType = right.returnType(data);

        if(!rightType.equals(leftType)) {
            throw new ParseException("Expected " + leftType + ", got " + rightType, right.getPosition());
        }

        if (left.returnType(data).equals(Signature.integer())) {
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
        } else {
            throw new ParseException("Expected [INT, NUM], got " + leftType, left.getPosition());
        }
    }

    private void compJump(MethodVisitor visitor) {


    }

    @Override
    public Signature returnType(BuildData data) {
        return Signature.bool();
    }
}
