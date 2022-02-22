package com.dfsek.substrate.lang.node.expression.cast;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.constant.DecimalNode;
import com.dfsek.substrate.lang.node.expression.constant.IntegerNode;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.lexer.token.Token;

public class NumToIntCastNode extends TypeCastNode<Double, Integer> {
    public NumToIntCastNode(Token type, ExpressionNode value) {
        super(type, value);
    }

    @Override
    public void applyCast(MethodBuilder visitor, BuildData data) {
        ParserUtil.checkReturnType(value, Signature.decimal());
        visitor.d2i();
    }

    @Override
    public ExpressionNode simplify() {
        if(Node.disableOptimisation()) return this;
        if (value instanceof DecimalNode) {
            return new IntegerNode(((DecimalNode) value).getValue().intValue(), value.getPosition());
        }
        return super.simplify();
    }

    @Override
    public Signature reference() {
        return Signature.integer();
    }
}
