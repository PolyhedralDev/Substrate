package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;

import java.util.Collection;
import java.util.Collections;

public class ReturnNode extends ExpressionNode {
    private final Position position;

    private final ExpressionNode value;

    public ReturnNode(Position position, ExpressionNode value) {
        this.position = position;
        this.value = value;
    }

    @Override
    public void apply(MethodBuilder builder, BuildData data) throws ParseException {
        if (value == null) builder.voidReturn();
        else {
            value.simplify().apply(builder, data);
            Signature ret = value.reference();
            if (ret.size() == 1) builder.insn(ret.getType(0).returnInsn());
            else builder.refReturn();
        }
    }

    @Override
    public Position getPosition() {
        return position;
    }

    public Signature reference() {
        if (value == null) return Signature.empty();
        return value.reference();
    }

    @Override
    public Collection<Node> contents() {
        return Collections.singleton(value);
    }
}
