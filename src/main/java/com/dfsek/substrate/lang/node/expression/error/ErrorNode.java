package com.dfsek.substrate.lang.node.expression.error;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.exception.ParseException;

import java.util.Collection;
import java.util.Collections;

public class ErrorNode extends ExpressionNode {
    private final Position position;
    private final String message;

    public ErrorNode(Position position, String message) {
        this.position = position;
        this.message = message;
    }

    @Override
    public void apply(MethodBuilder builder, BuildData data) throws ParseException {
        throw new ParseException(message, position);
    }

    @Override
    public Signature reference() {
        return Signature.empty();
    }

    @Override
    protected Collection<? extends Node> contents() {
        return Collections.emptyList();
    }

    @Override
    public Position getPosition() {
        return position;
    }
}
