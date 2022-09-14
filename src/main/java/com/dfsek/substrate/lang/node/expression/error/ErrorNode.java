package com.dfsek.substrate.lang.node.expression.error;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.ParserScope;
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

    private final Exception e;

    public ErrorNode(Position position, String message, Signature signature) {
        this.position = position;
        this.message = message;
        this.signature = signature;
        this.e = new Exception();
    }

    public static Unchecked<ErrorNode> of(Position position, String message, Signature signature) {
        return Unchecked.of(new ErrorNode(position, message, signature));
    }

    public static Unchecked<ErrorNode> of(Position position, String message) {
        return Unchecked.of(new ErrorNode(position, message));
    }

    public static Unchecked<ErrorNode> of(Tuple2<String, Position> tuple) {
        return of(tuple._2, tuple._1);
    }

    public ErrorNode(Position position, String message) {
        this.position = position;
        this.message = message;
        this.signature = Signature.empty();
        this.e = new Exception();
    }

    @Override
    public List<Either<CompileError, Op>> apply(BuildData data, ParserScope scope) throws ParseException {
        return List.of(Op.error(message, position, e));
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
