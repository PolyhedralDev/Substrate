package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.BuildData;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import org.objectweb.asm.MethodVisitor;

public class ExpressionNode implements Node {
    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {

    }

    @Override
    public Position getPosition() {
        return null;
    }
}
