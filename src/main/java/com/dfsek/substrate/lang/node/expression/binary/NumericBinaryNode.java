package com.dfsek.substrate.lang.node.expression.binary;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.constant.DecimalNode;
import com.dfsek.substrate.lang.node.expression.constant.IntegerNode;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.lexer.token.Token;
import io.vavr.collection.List;
import io.vavr.control.Either;

public abstract class NumericBinaryNode extends BinaryOperationNode {
    public NumericBinaryNode(ExpressionNode left, ExpressionNode right, Token op) {
        super(left, right, op);
    }


    @Override
    public List<Either<CompileError, Op>> applyOp(BuildData data) {
        Signature leftType = ParserUtil.checkReturnType(left, Signature.integer(), Signature.decimal()).reference();
        ParserUtil.checkReturnType(right, Signature.integer(), Signature.decimal()).reference();

        ParserUtil.checkReturnType(right, leftType);
        if (leftType.equals(Signature.integer())) {
            return List.of(Op.insn(intOp()));
        } else if (leftType.equals(Signature.decimal())) {
            return List.of(Op.insn(doubleOp()));
        } else {
            throw new IllegalStateException("Non integer/decimal values in NumericBinaryNode");
        }
    }

    protected abstract int intOp();

    protected abstract int doubleOp();

    @Override
    public Signature reference() {
        Signature ref = left.reference();
        if (ref.weakEquals(Signature.fun())) {
            return ref.getGenericReturn(0);
        }
        return ref;
    }

    public abstract double apply(double left, double right);

    public abstract int apply(int left, int right);

    @Override
    public ExpressionNode simplify() {
        if(Node.disableOptimisation()) return this;
        if (left instanceof DecimalNode && right instanceof DecimalNode) {
            return new DecimalNode(
                    apply(((DecimalNode) left).getValue(), ((DecimalNode) right).getValue()),
                    left.getPosition());
        }
        if (left instanceof IntegerNode && right instanceof IntegerNode) {
            return new IntegerNode(
                    apply(((IntegerNode) left).getValue(), ((IntegerNode) right).getValue()),
                    left.getPosition());
        }
        return this;
    }
}
