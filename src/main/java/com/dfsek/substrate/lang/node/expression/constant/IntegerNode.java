package com.dfsek.substrate.lang.node.expression.constant;

import com.dfsek.substrate.lang.compiler.BuildData;
import com.dfsek.substrate.lang.compiler.Signature;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import org.objectweb.asm.MethodVisitor;

public class IntegerNode extends ConstantExpressionNode {
    public IntegerNode(Token token) {
        super(token);
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        int i = Integer.parseInt(token.getContent());
        if(i == -1) {
            visitor.visitInsn(ICONST_M1);
        } else if(i == 0) {
            visitor.visitInsn(ICONST_0);
        } else if(i == 1) {
            visitor.visitInsn(ICONST_1);
        } else if(i == 2) {
            visitor.visitInsn(ICONST_2);
        } else if(i == 3) {
            visitor.visitInsn(ICONST_3);
        } else if(i == 4) {
            visitor.visitInsn(ICONST_4);
        } else if(i == 5) {
            visitor.visitInsn(ICONST_5);
        } else if(i >= -128 && i < 128) {
            visitor.visitIntInsn(BIPUSH, i); // byte
        } else if(i >= -32768 && i < 32768) {
            visitor.visitIntInsn(SIPUSH, i); // short
        } else {
            visitor.visitLdcInsn(i); // constant pool
        }
    }

    @Override
    public Signature returnType(BuildData data) {
        return Signature.integer();
    }
}
