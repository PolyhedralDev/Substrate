package com.dfsek.substrate.lang.node.expression.binary.comparison;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.binary.BinaryOperationNode;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.lexer.token.Token;
import org.objectweb.asm.Label;

public abstract class ComparisonBinaryNode extends BinaryOperationNode {
    public ComparisonBinaryNode(ExpressionNode left, ExpressionNode right, Token op) {
        super(left, right, op);
    }

    @Override
    public void applyOp(MethodBuilder visitor, BuildData data) {
        Signature leftType = ParserUtil.checkReturnType(left, Signature.integer(), Signature.decimal(), Signature.string())
                .reference().getSimpleReturn();
        Signature rightType = ParserUtil.checkReturnType(right, Signature.integer(), Signature.decimal(), Signature.string())
                .reference().getSimpleReturn();

        ParserUtil.checkReturnType(left, rightType);

        if (leftType.equals(Signature.integer())) {
            Label f = new Label();
            Label t = new Label();
            visitor.jump(intInsn(), f)
                    .pushTrue()
                    .goTo(t)
                    .label(f)
                    .pushFalse()
                    .label(t);
        } else if (leftType.equals(Signature.decimal())) {
            Label f = new Label();
            Label t = new Label();
            visitor.dcmpl()
                    .jump(doubleInsn(), f)
                    .pushTrue()
                    .goTo(t)
                    .label(f)
                    .pushFalse()
                    .label(t);
        } else if (leftType.equals(Signature.string())) {
            if (string()) {
                applyStrComparison(visitor);
            } else {
                throw new ParseException("Cannot apply operation " + op.getType() + " to type STR", op.getPosition());
            }
        }
    }

    protected boolean string() {
        return false;
    }

    protected abstract int intInsn();

    protected abstract int doubleInsn();

    protected void applyStrComparison(MethodBuilder visitor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Signature reference() {
        return Signature.bool();
    }
}
