package com.dfsek.substrate.lang.node.expression.error;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Either;

import java.util.Collection;
import java.util.Collections;

public class ErrorNode extends ExpressionNode {
    private final Position position;
    private final String message;
    private final Signature signature;

    public ErrorNode(Position position, String message, Signature signature) {
        this.position = position;
        this.message = message;
        this.signature = signature;
    }

    public ErrorNode(Position position, String message) {
        this.position = position;
        this.message = message;
        this.signature = Signature.empty();
    }

    public ErrorNode(Tuple2<String, Position> tuple) {
        this(tuple._2, tuple._1);
    }

    @Override
    public List<Either<CompileError, Op>> apply(BuildData data) throws ParseException {
        return List.of(Op.error(message, position));
    }

    @Override
    public Signature reference() {
        return signature;
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
