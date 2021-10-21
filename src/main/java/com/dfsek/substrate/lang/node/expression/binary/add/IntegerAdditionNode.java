package com.dfsek.substrate.lang.node.expression.binary.add;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.BinaryOperationNode;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import org.objectweb.asm.MethodVisitor;

public class IntegerAdditionNode extends BinaryOperationNode {
    public IntegerAdditionNode(ExpressionNode left, ExpressionNode right, Token op) {
        super(left, right, op);
    }

    @Override
    public void applyOp(MethodVisitor visitor, BuildData data) {
        if(!left.returnType(data).equals(Signature.integer())) {
            throw new ParseException("Expected INT, got " + left.returnType(data), left.getPosition());
        }

        if(!right.returnType(data).equals(Signature.integer())) {
            throw new ParseException("Expected INT, got " + right.returnType(data), right.getPosition());
        }

        visitor.visitInsn(IADD);
    }

    @Override
    public Signature returnType(BuildData data) {
        return Signature.integer();
    }
}
