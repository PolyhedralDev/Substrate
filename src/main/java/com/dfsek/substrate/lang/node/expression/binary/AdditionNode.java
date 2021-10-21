package com.dfsek.substrate.lang.node.expression.binary;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.BinaryOperationNode;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import org.objectweb.asm.MethodVisitor;

public class AdditionNode extends BinaryOperationNode {
    public AdditionNode(ExpressionNode left, ExpressionNode right, Token op) {
        super(left, right, op);
    }

    @Override
    public void applyOp(MethodVisitor visitor, BuildData data) {
        if(left.returnType(data).equals(Signature.integer())) {
            if(!left.returnType(data).equals(Signature.integer())) {
                throw new ParseException("Expected INT, got " + left.returnType(data), left.getPosition());
            }

            if(!right.returnType(data).equals(Signature.integer())) {
                throw new ParseException("Expected INT, got " + right.returnType(data), right.getPosition());
            }

            visitor.visitInsn(IADD);
        } else {
            if(!left.returnType(data).equals(Signature.decimal())) {
                throw new ParseException("Expected NUM, got " + left.returnType(data), left.getPosition());
            }

            if(!right.returnType(data).equals(Signature.decimal())) {
                throw new ParseException("Expected NUM, got " + right.returnType(data), right.getPosition());
            }

            visitor.visitInsn(DADD);
        }
    }

    @Override
    public Signature returnType(BuildData data) {
        return left.returnType(data);
    }
}
