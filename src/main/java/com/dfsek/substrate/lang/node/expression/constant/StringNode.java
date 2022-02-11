package com.dfsek.substrate.lang.node.expression.constant;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.tokenizer.Token;

public class StringNode extends ConstantExpressionNode<String> {
    public StringNode(String value, Position position) {
        super(value, position);
    }

    @Override
    public void apply(MethodBuilder builder, BuildData data) throws ParseException {
        builder.pushConst(value); // LDC string content
    }

    @Override
    public Signature reference() {
        return Signature.string();
    }
}
