package com.dfsek.substrate.lang.node.expression.cast;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.tokenizer.Token;

import java.util.Collection;
import java.util.Collections;

public abstract class TypeCastNode<C, R> extends ExpressionNode {
    protected final Token type;
    protected final ExpressionNode value;

    public TypeCastNode(Token type, ExpressionNode value) {
        this.type = type;
        this.value = value.simplify();
    }

    @Override
    public void apply(MethodBuilder builder, BuildData data) throws ParseException {
        value.apply(builder, data);
        applyCast(builder, data);
    }

    public abstract void applyCast(MethodBuilder visitor, BuildData data);


    @Override
    public Position getPosition() {
        return type.getPosition();
    }

    @Override
    public Collection<? extends Node> contents() {
        return Collections.singleton(value);
    }
}
