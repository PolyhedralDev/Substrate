package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.compiler.value.PrimitiveValue;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.ParserScope;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.Tuple2;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Either;

import static com.dfsek.substrate.lang.compiler.codegen.bytes.Op.dup;

public class LetNode extends ExpressionNode {
    private final Map<String, ExpressionNode> localValues;

    private final ExpressionNode node;

    private LetNode(Map<String, ExpressionNode> localValues, ExpressionNode node) {
        this.localValues = localValues;
        this.node = node;
    }

    public static Unchecked<LetNode> of(Map<String, ExpressionNode> values, Unchecked<? extends ExpressionNode> node) {
        return Unchecked.of(new LetNode(values, node.unchecked()));
    }

    @Override
    public List<Either<CompileError, Op>> apply(BuildData data, ParserScope scope) throws ParseException {
        Tuple2<ParserScope, List<Either<CompileError, Op>>> tuple21 = localValues.foldLeft(new Tuple2<>(scope, List.empty()), (tuple2, stringExpressionNodeTuple2) -> tuple2.map((tuple2s, eithers) -> {
            Signature reference = stringExpressionNodeTuple2._2.reference();
            return new Tuple2<>(tuple2s.register(stringExpressionNodeTuple2._1, new PrimitiveValue(reference, stringExpressionNodeTuple2._1, tuple2s.getLocalWidth(), reference.frames())), eithers.appendAll(stringExpressionNodeTuple2._2.apply(data, tuple2s)));
        }));

        return tuple21._2
                .appendAll(node.apply(data, tuple21._1));
    }


    @Override
    public Signature reference() {
        return node.reference();
    }

    @Override
    protected Iterable<? extends Node> contents() {
        return localValues.values().append(node);
    }

    @Override
    public Position getPosition() {
        return node.getPosition();
    }
}
