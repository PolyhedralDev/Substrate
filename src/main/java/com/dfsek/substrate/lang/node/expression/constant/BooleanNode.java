package com.dfsek.substrate.lang.node.expression.constant;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import org.objectweb.asm.MethodVisitor;

public class BooleanNode extends ConstantExpressionNode {
    public BooleanNode(Token token) {
        super(token);
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        boolean val = Boolean.parseBoolean(token.getContent());
        if (val) {
            visitor.visitInsn(ICONST_1); // true
        } else {
            visitor.visitInsn(ICONST_0); // false
        }
    }

    public Signature returnType(BuildData data) {
        return Signature.bool();
    }
}
