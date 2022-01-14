package com.dfsek.substrate.lang.node.expression.binary.comparison;

import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.tokenizer.Token;

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
    protected void applyStrComparison(MethodBuilder visitor) {
        visitor.invokeVirtual(
                "java/lang/String",
                "equals",
                "(Ljava/lang/Object;)Z");
    }

    @Override
    protected boolean string() {
        return true;
    }
}
