package com.dfsek.substrate.lang.node.expression.constant;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;

public class IntegerNode extends ConstantExpressionNode {
    public IntegerNode(Token token) {
        super(token);
    }

    @Override
    public void apply(MethodBuilder builder, BuildData data) throws ParseException {
        int i = Integer.parseInt(token.getContent());
        builder.pushInt(i);
    }

    @Override
    public Signature reference(BuildData data) {
        return Signature.integer();
    }
}
