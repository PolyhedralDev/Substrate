package com.dfsek.substrate.lang.node.expression.cast;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import org.objectweb.asm.MethodVisitor;

public class ToStringNode extends TypeCastNode {
    public ToStringNode(Token type, ExpressionNode value) {
        super(type, value);
    }

    @Override
    public void applyCast(MethodVisitor visitor, BuildData data) {
        Signature ref = value.referenceType(data).getSimpleReturn();
        if (ref.equals(Signature.integer())) {
            visitor.visitMethodInsn(INVOKESTATIC,
                    "java/lang/Integer",
                    "toString",
                    "(I)Ljava/lang/String;",
                    false);
        } else if (ref.equals(Signature.decimal())) {
            visitor.visitMethodInsn(INVOKESTATIC,
                    "java/lang/Double",
                    "toString",
                    "(D)Ljava/lang/String;",
                    false);
        } else {
            throw new ParseException("Expected INT | NUM, got " + ref, getPosition());
        }
    }

    @Override
    public Signature referenceType(BuildData data) {
        return Signature.string();
    }
}
