package com.dfsek.substrate.lang.node.expression.binary;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.constant.DecimalNode;
import com.dfsek.substrate.lang.node.expression.constant.IntegerNode;
import com.dfsek.substrate.lang.node.expression.error.ErrorNode;
import com.dfsek.substrate.lexer.token.Token;
import com.dfsek.substrate.parser.ParserUtil;
import io.vavr.collection.List;
import io.vavr.control.Either;

public abstract class NumericBinaryNode extends BinaryOperationNode {
    protected NumericBinaryNode(Unchecked<? extends ExpressionNode> left, Unchecked<? extends ExpressionNode> right, Token op) {
        super(left, right, op);
    }


    @Override
    public List<Either<CompileError, Op>> applyOp(BuildData data) {
        Signature leftType = left.reference();

        if (leftType.equals(Signature.integer())) {
            return List.of(Op.insn(intOp()));
        } else if (leftType.equals(Signature.decimal())) {
            return List.of(Op.insn(doubleOp()));
        } else if(!(left instanceof ErrorNode || right instanceof ErrorNode)){
            throw new IllegalStateException("Non integer/decimal values in NumericBinaryNode");
        } else {
            return List.empty();
        }
    }

    @Override
    protected ExpressionNode check(Unchecked<? extends ExpressionNode> unchecked) {
        return unchecked.get(Signature.integer(), Signature.decimal());
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
        if (Node.disableOptimisation()) return this;
        if (left instanceof DecimalNode && right instanceof DecimalNode) {
            return DecimalNode.of(
                    apply(((DecimalNode) left).getValue(), ((DecimalNode) right).getValue()),
                    left.getPosition()).get(Signature.decimal());
        }
        if (left instanceof IntegerNode && right instanceof IntegerNode) {
            return IntegerNode.of(
                    apply(((IntegerNode) left).getValue(), ((IntegerNode) right).getValue()),
                    left.getPosition()).get(Signature.integer());
        }
        return this;
    }
}
