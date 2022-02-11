package com.dfsek.substrate.lang.node.expression.binary;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.constant.DecimalNode;
import com.dfsek.substrate.lang.node.expression.constant.IntegerNode;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.tokenizer.Token;

public abstract class NumericBinaryNode extends BinaryOperationNode {
    public NumericBinaryNode(ExpressionNode left, ExpressionNode right, Token op) {
        super(left, right, op);
    }


    @Override
    public void applyOp(MethodBuilder visitor, BuildData data) {
        Signature leftType = ParserUtil.checkType(left, data, Signature.integer(), Signature.decimal()).reference(data);
        ParserUtil.checkType(right, data, Signature.integer(), Signature.decimal()).reference(data);

        ParserUtil.checkType(right, data, leftType);
        if(leftType.equals(Signature.integer())) {
            visitor.insn(intOp());
        } else if(leftType.equals(Signature.decimal())) {
            visitor.insn(doubleOp());
        }
    }

    protected abstract int intOp();
    protected abstract int doubleOp();

    @Override
    public Signature reference(BuildData data) {
        Signature ref = left.reference(data);
        if(ref.weakEquals(Signature.fun())) {
            return ref.getGenericReturn(0);
        }
        return ref;
    }

    public abstract double apply(double left, double right);

    public abstract int apply(int left, int right);

    @Override
    public ExpressionNode simplify() {
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
