package com.dfsek.substrate.lang.node;

import com.dfsek.substrate.lang.compiler.BuildData;
import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import org.objectweb.asm.MethodVisitor;

public class FunctionInvocationNode implements Node {
    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        Node.super.apply(visitor, data);
    }

    @Override
    public ReturnType getType() {
        return Node.super.getType();
    }

    @Override
    public Position getPosition() {
        return null;
    }
}
