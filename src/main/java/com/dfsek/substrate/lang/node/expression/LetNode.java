package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.compiler.value.PrimitiveValue;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.lang.node.expression.error.ErrorNode;
import com.dfsek.substrate.lang.node.expression.value.ValueAssignmentNode;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.Function2;
import io.vavr.Tuple2;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Either;

import java.util.function.Function;

import static com.dfsek.substrate.lang.compiler.codegen.bytes.Op.dup;

public class LetNode extends ExpressionNode {
    private final Map<String, Either<ErrorNode, ValueAssignmentNode>> localValues;

    private final ExpressionNode node;

    private LetNode(Map<String, Either<ErrorNode, ValueAssignmentNode>> localValues, ExpressionNode node) {
        this.localValues = localValues;
        this.node = node;
    }

    public static Unchecked<LetNode> of(Map<String, Either<ErrorNode, ValueAssignmentNode>> values, Unchecked<? extends ExpressionNode> node) {
        return Unchecked.of(new LetNode(values, node.unchecked()));
    }

    @Override
    public List<Either<CompileError, Op>> apply(BuildData data, LinkedHashMap<String, Value> valueMap) throws ParseException {
        return localValues
                .foldLeft(
                        new Tuple2<>(List.<Either<CompileError, Op>>empty(), valueMap),
                        (t, n) -> t
                                .map1(l -> l.appendAll(n._2().fold(error -> error.apply(data, valueMap), assignment -> assignment.apply(data, t._2()))))
                                .map2(m -> n._2().fold(ignore -> t._2(), a -> t._2().put(a.getId().getContent(), a.getReference())))
                )
                .apply((compiled, map) -> compiled.appendAll(node.apply(data, map)));
    }


    @Override
    public Signature reference() {
        return node.reference();
    }

    @Override
    protected Iterable<? extends Node> contents() {
        return localValues.values().map(e -> e.fold(Function.identity(), Function.identity())).append(node);
    }

    @Override
    public Position getPosition() {
        return node.getPosition();
    }

    @Override
    public String toString() {
        return "let { " + localValues.map(Tuple2::_2).mkString(", ") + " } in " + node;
    }
}
