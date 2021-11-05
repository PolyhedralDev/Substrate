package com.dfsek.substrate.lang.node.expression.binary;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import org.objectweb.asm.MethodVisitor;

public abstract class NumericBinaryNode extends BinaryOperationNode {
    public NumericBinaryNode(ExpressionNode left, ExpressionNode right, Token op) {
        super(left, right, op);
    }


    @Override
    public void applyOp(MethodVisitor visitor, BuildData data) {
        Signature leftType = left.referenceType(data);
        Signature rightType = right.referenceType(data);

        if(!rightType.equals(leftType)) {
            throw new ParseException("Expected " + leftType + ", got " + rightType, right.getPosition());
        }

        if(leftType.equals(Signature.integer())) {
            visitor.visitInsn(intOp());
        } else if(leftType.equals(Signature.decimal())) {
            visitor.visitInsn(doubleOp());
        } else {
            throw new ParseException("Expected [INT, NUM], got " + leftType, left.getPosition());
        }
    }

    protected abstract int intOp();
    protected abstract int doubleOp();

    @Override
    public Signature referenceType(BuildData data) {
        Signature ref = left.referenceType(data);
        if(ref.weakEquals(Signature.fun())) {
            return ref.getGenericReturn(0);
        }
        return ref;
    }
}
