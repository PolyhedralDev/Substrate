package com.dfsek.substrate.lang.node.expression.constant;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import org.objectweb.asm.MethodVisitor;

public class IntegerNode extends ConstantExpressionNode {
    public IntegerNode(Token token) {
        super(token);
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        int i = Integer.parseInt(token.getContent());
        CompilerUtil.pushInt(i, visitor);
    }

    @Override
    public Signature returnType(BuildData data) {
        return Signature.integer();
    }
}
