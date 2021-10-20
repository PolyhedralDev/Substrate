package com.dfsek.substrate.lang.node.expression.constant;

import com.dfsek.substrate.lang.compiler.BuildData;
import com.dfsek.substrate.lang.compiler.Signature;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import org.objectweb.asm.MethodVisitor;

public class DecimalNode extends ConstantExpressionNode {
    public DecimalNode(Token token) {
        super(token);
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        visitor.visitLdcInsn(Double.parseDouble(token.getContent()));
    }

    @Override
    public Signature returnType(BuildData data) {
        return Signature.decimal();
    }
}
