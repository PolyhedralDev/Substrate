package com.dfsek.substrate.lang.node.expression.binary.comparison;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.binary.BinaryOperationNode;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class EqualsNode extends ComparisonBinaryNode {
    public EqualsNode(ExpressionNode left, ExpressionNode right, Token op) {
        super(left, right, op);
    }

    @Override
    protected int intInsn() {
        return IF_ICMPNE;
    }

    @Override
    protected int doubleInsn() {
        return IFNE;
    }

    @Override
    protected void applyStrComparison(MethodVisitor visitor) {
        visitor.visitMethodInsn(INVOKEVIRTUAL,
                "java/lang/String",
                "equals",
                "(Ljava/lang/Object;)Z",
                false);
    }

    @Override
    public Signature referenceType(BuildData data) {
        return Signature.bool();
    }
}
