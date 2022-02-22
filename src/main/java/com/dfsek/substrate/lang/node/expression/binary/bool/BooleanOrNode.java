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

public class BooleanOrNode extends BooleanOperationNode {
    public BooleanOrNode(ExpressionNode left, ExpressionNode right, Token op) {
        super(left, right, op);
    }

    @Override
    public boolean apply(boolean left, boolean right) {
        return left || right;
    }

    @Override
    public ExpressionNode simplify() {
        if(Node.disableOptimisation()) return this;
        if (left instanceof BooleanNode) {
            if (((BooleanNode) left).getValue()) {
                return left; // short-circuit.
            } else {
                return right; // left does not matter
            }
        }
        if (right instanceof BooleanNode) {
            if (((BooleanNode) right).getValue()) {
                return left; // short-circuit.
            } else {
                return right; // left does not matter
            }
        }
        return super.simplify();
    }

    @Override
    public void apply(MethodBuilder builder, BuildData data) throws ParseException {
        ParserUtil.checkReturnType(left, Signature.bool()).apply(builder, data);
        Label shortTrue = new Label();
        Label shortFalse = new Label();
        Label end = new Label();

        builder.ifNE(shortTrue);
        ParserUtil.checkReturnType(right, Signature.bool()).apply(builder, data);
        builder.ifEQ(shortFalse);

        builder.label(shortTrue)
                .pushInt(1)
                .goTo(end)

                .label(shortFalse)
                .pushInt(0)

                .label(end);
    }
}
