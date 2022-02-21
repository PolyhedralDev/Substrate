package com.dfsek.substrate.lang.node.expression.cast;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.constant.ConstantExpressionNode;
import com.dfsek.substrate.lang.node.expression.constant.DecimalNode;
import com.dfsek.substrate.lang.node.expression.constant.IntegerNode;
import com.dfsek.substrate.lang.node.expression.constant.StringNode;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.tokenizer.Token;

public class ToStringNode extends TypeCastNode<Object, String> {
    public ToStringNode(Token type, ExpressionNode value) {
        super(type, value);
    }

    @Override
    public void applyCast(MethodBuilder visitor, BuildData data) {
        Signature ref = ParserUtil.checkType(value, Signature.decimal(), Signature.integer(), Signature.bool())
                .reference()
                .getSimpleReturn();
        if (ref.equals(Signature.integer())) {
            visitor.invokeStatic("java/lang/Integer",
                    "toString",
                    "(I)Ljava/lang/String;");
        } else if (ref.equals(Signature.decimal())) {
            visitor.invokeStatic("java/lang/Double",
                    "toString",
                    "(D)Ljava/lang/String;");
        } else if (ref.equals(Signature.bool())) {
            visitor.invokeStatic("java/lang/Boolean",
                    "toString",
                    "(Z)Ljava/lang/String;");
        }
    }

    @Override
    public ExpressionNode simplify() {
        if (value instanceof DecimalNode || value instanceof IntegerNode) {
            return new StringNode(((ConstantExpressionNode<?>) value).getValue().toString(), value.getPosition());
        }
        return super.simplify();
    }

    @Override
    public Signature reference() {
        return Signature.string();
    }
}
