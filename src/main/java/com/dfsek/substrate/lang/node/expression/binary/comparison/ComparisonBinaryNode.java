package com.dfsek.substrate.lang.node.expression.binary.comparison;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.binary.BinaryOperationNode;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.lexer.token.Token;
import io.vavr.collection.List;
import io.vavr.control.Either;
import org.objectweb.asm.Label;

import static com.dfsek.substrate.lang.compiler.codegen.bytes.Op.*;

public abstract class ComparisonBinaryNode extends BinaryOperationNode {
    public ComparisonBinaryNode(ExpressionNode left, ExpressionNode right, Token op) {
        super(left, right, op);
    }

    @Override
    public List<Either<CompileError, Op>> applyOp(BuildData data) {
        Signature leftType = ParserUtil.checkReturnType(left, Signature.integer(), Signature.decimal(), Signature.string())
                .reference().getSimpleReturn();
        Signature rightType = ParserUtil.checkReturnType(right, Signature.integer(), Signature.decimal(), Signature.string())
                .reference().getSimpleReturn();

        ParserUtil.checkReturnType(left, rightType);

        if (leftType.equals(Signature.integer())) {
            Label f = new Label();
            Label t = new Label();
            return List.of(
                    jumpInsn(intInsn(), f),
                    pushTrue(),
                    goTo(t),
                    label(f),
                    pushFalse(),
                    label(t)
            );
        } else if (leftType.equals(Signature.decimal())) {
            Label f = new Label();
            Label t = new Label();
            return List.of(
                    dcmpl(),
                    jumpInsn(doubleInsn(), f),
                    pushTrue(),
                    goTo(t),
                    label(f),
                    pushFalse(),
                    label(t)
            );
        } else if (leftType.equals(Signature.string())) {
            if (string()) {
                return applyStrComparison();
            } else {
                throw new ParseException("Cannot apply operation " + op.getType() + " to type STR", op.getPosition());
            }
        } else {
            throw new IllegalStateException("Invalid type " + leftType + " for comparison node");
        }
    }

    protected boolean string() {
        return false;
    }

    protected abstract int intInsn();

    protected abstract int doubleInsn();

    protected List<Either<CompileError, Op>> applyStrComparison() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Signature reference() {
        return Signature.bool();
    }
}
