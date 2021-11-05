package com.dfsek.substrate.lang.node.expression.cast;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import org.objectweb.asm.MethodVisitor;

public class IntToNumCastNode extends TypeCastNode {
    public IntToNumCastNode(Token type, ExpressionNode value) {
        super(type, value);
    }

    @Override
    public void applyCast(MethodVisitor visitor, BuildData data) {
        if (!value.reference(data).equals(Signature.integer())) {
            throw new ParseException("Expected INT, got " + value.reference(data), getPosition());
        }
        visitor.visitInsn(I2D);
    }

    @Override
    public Signature reference(BuildData data) {
        return Signature.decimal();
    }
}
