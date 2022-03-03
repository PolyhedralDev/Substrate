package com.dfsek.substrate.lang.node.expression.binary.comparison;

import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lexer.token.Token;
import io.vavr.collection.List;
import io.vavr.control.Either;

import static com.dfsek.substrate.lang.compiler.codegen.bytes.Op.invokeVirtual;

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
    protected List<Either<CompileError, Op>> applyStrComparison() {
        return List.of(invokeVirtual("java/lang/String",
                "equals",
                "(Ljava/lang/Object;)Z"));
    }

    @Override
    protected boolean string() {
        return true;
    }
}
