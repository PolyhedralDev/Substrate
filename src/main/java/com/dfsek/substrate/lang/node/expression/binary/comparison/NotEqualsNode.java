package com.dfsek.substrate.lang.node.expression.binary.comparison;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
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
        CompilerUtil.invertBoolean(visitor);
    }

    @Override
    protected boolean string() {
        return true;
    }
}
