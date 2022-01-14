package com.dfsek.substrate.lang.node.expression.constant;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;

public class DecimalNode extends ConstantExpressionNode {
    public DecimalNode(Token token) {
        super(token);
    }

    @Override
    public void apply(MethodBuilder builder, BuildData data) throws ParseException {
        builder.pushDouble(Double.parseDouble(token.getContent()));
    }

    @Override
    public Signature reference(BuildData data) {
        return Signature.decimal();
    }
}
