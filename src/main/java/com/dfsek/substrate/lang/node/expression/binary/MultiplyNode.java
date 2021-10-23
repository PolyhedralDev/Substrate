package com.dfsek.substrate.lang.node.expression.binary;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.BinaryOperationNode;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import org.objectweb.asm.MethodVisitor;

public class MultiplyNode extends BinaryOperationNode {
    public MultiplyNode(ExpressionNode left, ExpressionNode right, Token op) {
        super(left, right, op);
    }

    @Override
    public void applyOp(MethodVisitor visitor, BuildData data) {
        Signature leftType = left.returnType(data);
        Signature rightType = right.returnType(data);

        if(!rightType.equals(leftType)) {
            throw new ParseException("Expected " + leftType + ", got " + rightType, right.getPosition());
        }

        if(leftType.equals(Signature.integer())) {
            visitor.visitInsn(IMUL);
        } else if(leftType.equals(Signature.decimal())) {
            visitor.visitInsn(DMUL);
        } else {
            throw new ParseException("Expected [INT, NUM], got " + leftType, left.getPosition());
        }
    }

    @Override
    public Signature returnType(BuildData data) {
        return left.returnType(data);
    }
}
