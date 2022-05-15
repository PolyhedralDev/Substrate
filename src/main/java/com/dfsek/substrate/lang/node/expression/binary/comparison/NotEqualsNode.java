package com.dfsek.substrate.lang.node.expression.binary.comparison;

import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lexer.token.Token;
import io.vavr.collection.List;
import io.vavr.control.Either;

public class NotEqualsNode extends ComparisonBinaryNode {
    private NotEqualsNode(Unchecked<? extends ExpressionNode> left, Unchecked<? extends ExpressionNode> right, Token op) {
        super(left, right, op);
    }

    public static Unchecked<NotEqualsNode> of(Unchecked<? extends ExpressionNode> left, Unchecked<? extends ExpressionNode> right, Token op) {
        return Unchecked.of(new NotEqualsNode(left, right, op));
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
    protected List<Either<CompileError, Op>> applyStrComparison() {
        return List.of(Op.invokeVirtual("java/lang/String",
                        "equals",
                        "(Ljava/lang/Object;)Z"))
                .appendAll(Op.invertBoolean());
    }

    @Override
    protected boolean string() {
        return true;
    }
}
