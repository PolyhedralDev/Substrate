package com.dfsek.substrate.lang.node.expression.constant;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.exception.ParseException;

public class DecimalNode extends ConstantExpressionNode<Double> {
    public DecimalNode(double value, Position position) {
        super(value, position);
    }

    @Override
    public void apply(MethodBuilder builder, BuildData data) throws ParseException {
        builder.pushDouble(value);
    }

    @Override
    public Signature reference() {
        return Signature.decimal();
    }
}
