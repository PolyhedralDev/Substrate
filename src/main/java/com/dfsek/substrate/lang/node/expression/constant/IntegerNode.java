package com.dfsek.substrate.lang.node.expression.constant;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.tokenizer.Token;

import java.awt.*;

public class IntegerNode extends ConstantExpressionNode<Integer> {
    public IntegerNode(int value, Position position) {
        super(value, position);
    }

    @Override
    public void apply(MethodBuilder builder, BuildData data) throws ParseException {
        builder.pushInt(value);
    }

    @Override
    public Signature reference() {
        return Signature.integer();
    }
}
