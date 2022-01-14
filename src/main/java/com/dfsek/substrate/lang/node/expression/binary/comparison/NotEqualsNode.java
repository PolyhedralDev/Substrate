package com.dfsek.substrate.lang.node.expression.binary.comparison;

import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.tokenizer.Token;

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
    protected void applyStrComparison(MethodBuilder visitor) {
        visitor.invokeVirtual(
                "java/lang/String",
                "equals",
                "(Ljava/lang/Object;)Z");
        visitor.invertBoolean();
    }

    @Override
    protected boolean string() {
        return true;
    }
}
