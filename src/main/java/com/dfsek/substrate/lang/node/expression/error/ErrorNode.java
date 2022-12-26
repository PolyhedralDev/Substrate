package com.dfsek.substrate.lang.node.expression.error;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.Tuple2;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import io.vavr.control.Either;

import java.util.Collection;
import java.util.Collections;

public class ErrorNode extends ExpressionNode {
    private final Position position;
    private final String message;
    private final Signature signature;

    private final Exception e;

    private final List<? extends Node> extra;

    public String getMessage() {
        return message;
    }

    public ErrorNode(Position position, String message, Signature signature, List<? extends Node> extra) {
        this.position = position;
        this.message = message;
        this.signature = signature;
        this.extra = extra;
        this.e = new Exception();
    }

    public static Unchecked<ErrorNode> of(Position position, String message, Signature signature) {
        return Unchecked.of(new ErrorNode(position, message, signature, List.empty()));
    }

    public static Unchecked<ErrorNode> of(Position position, String message) {
        return Unchecked.of(new ErrorNode(position, message, Signature.empty(), List.empty()));
    }

    public static Unchecked<ErrorNode> of(Tuple2<String, Position> tuple) {
        return of(tuple._2, tuple._1);
    }

    @Override
    public List<Either<CompileError, Op>> apply(BuildData data, LinkedHashMap<String, Value> valueMap) throws ParseException {
        return extra.foldLeft(List.of(Op.error(message, position, e)), (comp, a) -> comp.appendAll(a.apply(data, valueMap)));
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

    @Override
    public String toString() {
        return "(@ERROR: " + message + " @" + position + ")";
    }
}
