package com.dfsek.substrate.lang.node.expression.cast;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.lexer.token.Token;
import io.vavr.collection.List;
import io.vavr.control.Either;

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
    public List<Either<CompileError, Op>> apply(BuildData data) throws ParseException {
        return value.apply(data)
                .appendAll(applyCast(data));
    }

    public abstract List<Either<CompileError, Op>> applyCast(BuildData data);


    @Override
    public Position getPosition() {
        return type.getPosition();
    }

    @Override
    public Collection<? extends Node> contents() {
        return Collections.singleton(value);
    }
}
