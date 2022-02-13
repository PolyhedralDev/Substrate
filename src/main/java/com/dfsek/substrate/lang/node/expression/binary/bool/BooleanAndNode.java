package com.dfsek.substrate.lang.node.expression.binary.bool;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
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
    public void apply(MethodBuilder builder, BuildData data) throws ParseException {
        ParserUtil.checkType(left, data, Signature.bool()).simplify().apply(builder, data);
        Label shortFalse = new Label();
        Label end = new Label();

        builder.ifEQ(shortFalse);

        ParserUtil.checkType(right, data, Signature.bool()).simplify().apply(builder, data);

        builder.ifEQ(shortFalse)
                .pushInt(1)
                .goTo(end)
                .label(shortFalse)
                .pushInt(0)
                .label(end);
    }
}
