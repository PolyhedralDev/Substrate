package com.dfsek.substrate.lang.node.expression.binary.bool;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.constant.BooleanNode;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import org.objectweb.asm.Label;

public class BooleanAndNode extends BooleanOperationNode {
    public BooleanAndNode(ExpressionNode left, ExpressionNode right, Token op) {
        super(left, right, op);
    }

    @Override
    public boolean apply(boolean left, boolean right) {
        return left && right;
    }

    @Override
    public ExpressionNode simplify() {
        if(Node.disableOptimisation()) return this;
        if (left instanceof BooleanNode) {
            if (((BooleanNode) left).getValue()) {
                return right; // left does not matter.
            } else {
                return left; // short-circuit
            }
        }
        if (right instanceof BooleanNode) {
            if (((BooleanNode) right).getValue()) {
                return left; // right does not matter.
            } else {
                return right; // short-circuit
            }
        }
        return super.simplify();
    }

    @Override
    public void apply(MethodBuilder builder, BuildData data) throws ParseException {
        ParserUtil.checkReturnType(left, Signature.bool()).apply(builder, data);
        Label shortFalse = new Label();
        Label end = new Label();

        builder.ifEQ(shortFalse);

        ParserUtil.checkReturnType(right, Signature.bool()).apply(builder, data);

        builder.ifEQ(shortFalse)
                .pushInt(1)
                .goTo(end)
                .label(shortFalse)
                .pushInt(0)
                .label(end);
    }
}
