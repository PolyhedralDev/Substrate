package com.dfsek.substrate.lang.node.expression.constant;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;

public class BooleanNode extends ConstantExpressionNode<Boolean> {
    public BooleanNode(boolean value, Position position) {
        super(value, position);
    }

    @Override
    public void apply(MethodBuilder builder, BuildData data) throws ParseException {
        if (value) {
            builder.pushTrue(); // true
        } else {
            builder.pushFalse(); // false
        }
    }

    @Override
    public Signature reference() {
        return Signature.bool();
    }
}
