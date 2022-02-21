package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;

import java.util.Collection;
import java.util.Collections;

public class BooleanNotNode extends ExpressionNode {
    private final Position position;
    private final ExpressionNode node;

    public BooleanNotNode(Position position, ExpressionNode node) {
        this.position = position;
        this.node = node;
    }

    @Override
    public void apply(MethodBuilder builder, BuildData data) throws ParseException {
        ParserUtil.checkType(node, Signature.bool()).simplify().apply(builder, data);
        builder.invertBoolean();
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public Signature reference() {
        return Signature.bool();
    }

    @Override
    public Collection<? extends Node> contents() {
        return Collections.singleton(node);
    }
}
