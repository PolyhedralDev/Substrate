package com.dfsek.substrate.lang.node.expression.cast;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.constant.DecimalNode;
import com.dfsek.substrate.lang.node.expression.constant.IntegerNode;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.tokenizer.Token;

public class IntToNumCastNode extends TypeCastNode<Integer, Double> {
    public IntToNumCastNode(Token type, ExpressionNode value) {
        super(type, value);
    }

    @Override
    public void applyCast(MethodBuilder visitor, BuildData data) {
        ParserUtil.checkType(value, data, Signature.integer());
        visitor.i2d();
    }

    @Override
    public ExpressionNode simplify() {
        if(value instanceof IntegerNode) {
            return new DecimalNode(((IntegerNode) value).getValue(), value.getPosition());
        }
        return super.simplify();
    }

    @Override
    public Signature reference(BuildData data) {
        return Signature.decimal();
    }
}
