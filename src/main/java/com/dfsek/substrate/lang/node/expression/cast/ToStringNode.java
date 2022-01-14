package com.dfsek.substrate.lang.node.expression.cast;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.tokenizer.Token;

public class ToStringNode extends TypeCastNode {
    public ToStringNode(Token type, ExpressionNode value) {
        super(type, value);
    }

    @Override
    public void applyCast(MethodBuilder visitor, BuildData data) {
        Signature ref = ParserUtil.checkType(value, data, Signature.decimal(), Signature.integer())
                .reference(data)
                .getSimpleReturn();
        if (ref.equals(Signature.integer())) {
            visitor.invokeStatic(
                    "java/lang/Integer",
                    "toString",
                    "(I)Ljava/lang/String;"
                    );
        } else if (ref.equals(Signature.decimal())) {
            visitor.invokeStatic(
                    "java/lang/Double",
                    "toString",
                    "(D)Ljava/lang/String;"
                    );
        }
    }

    @Override
    public Signature reference(BuildData data) {
        return Signature.string();
    }
}
