package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.lexer.read.Position;

import java.util.Collection;
import java.util.Collections;

public class NumberInverseNode extends ExpressionNode {
    private final Position position;
    private final ExpressionNode node;

    public NumberInverseNode(Position position, ExpressionNode node) {
        this.position = position;
        this.node = node;
    }

    @Override
    public void apply(MethodBuilder builder, BuildData data) throws ParseException {
        ParserUtil.checkReferenceType(node, Signature.integer(), Signature.decimal()).simplify().apply(builder, data);
        if(node.reference().equals(Signature.integer())) builder.iNeg();
        if(node.reference().equals(Signature.decimal())) builder.dNeg();
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public Signature reference() {
        return node.reference();
    }

    @Override
    public Collection<? extends Node> contents() {
        return Collections.singleton(node);
    }
}
