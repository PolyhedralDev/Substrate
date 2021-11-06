package com.dfsek.substrate.lang.node.expression.binary.bool;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.tokenizer.Token;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class BooleanAndNode extends BooleanOperationNode{
    public BooleanAndNode(ExpressionNode left, ExpressionNode right, Token op) {
        super(left, right, op);
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        ParserUtil.checkType(left, data, Signature.bool()).apply(visitor, data);
        Label shortFalse = new Label();
        Label end = new Label();

        visitor.visitJumpInsn(IFEQ, shortFalse);

        ParserUtil.checkType(right, data, Signature.bool()).apply(visitor, data);

        visitor.visitJumpInsn(IFEQ, shortFalse);
        visitor.visitInsn(ICONST_1);
        visitor.visitJumpInsn(GOTO, end);
        visitor.visitLabel(shortFalse);
        visitor.visitInsn(ICONST_0);
        visitor.visitLabel(end);
    }
}
