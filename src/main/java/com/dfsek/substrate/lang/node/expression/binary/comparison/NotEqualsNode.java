package com.dfsek.substrate.lang.node.expression.binary.comparison;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.tokenizer.Token;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class NotEqualsNode extends ComparisonBinaryNode {
    public NotEqualsNode(ExpressionNode left, ExpressionNode right, Token op) {
        super(left, right, op);
    }

    @Override
    protected int intInsn() {
        return IF_ICMPEQ;
    }

    @Override
    protected int doubleInsn() {
        return IFEQ;
    }

    @Override
    protected void applyStrComparison(MethodVisitor visitor) {
        visitor.visitMethodInsn(INVOKEVIRTUAL,
                "java/lang/String",
                "equals",
                "(Ljava/lang/Object;)Z",
                false);
        Label caseTrue = new Label();
        Label caseFalse = new Label();
        visitor.visitJumpInsn(IFNE, caseFalse);
        visitor.visitInsn(ICONST_1);
        visitor.visitJumpInsn(GOTO, caseTrue);
        visitor.visitLabel(caseFalse);
        visitor.visitInsn(ICONST_0);
        visitor.visitLabel(caseTrue);
    }

    @Override
    protected boolean string() {
        return true;
    }
}
