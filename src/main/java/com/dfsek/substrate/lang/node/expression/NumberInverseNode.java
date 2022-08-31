package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import io.vavr.control.Either;

import java.util.Collection;
import java.util.Collections;

import static io.vavr.API.*;

public class NumberInverseNode extends ExpressionNode {
    private final Position position;
    private final ExpressionNode node;

    private NumberInverseNode(Position position, Unchecked<? extends ExpressionNode> node) {
        this.position = position;
        this.node = node.get(Signature.decimal(), Signature.integer());
    }

    public static Unchecked<NumberInverseNode> of(Position position, Unchecked<? extends ExpressionNode> node) {
        return Unchecked.of(new NumberInverseNode(position, node));
    }

    @Override
    public List<Either<CompileError, Op>> apply(BuildData data, LinkedHashMap<String, Value> values) throws ParseException {
        return node.simplify().apply(data, values)
                .append(Match(node.reference()).of(
                        Case($(Signature.integer()), Op.iNeg()),
                        Case($(Signature.decimal()), Op.dNeg()),
                        Case($(), t -> Op.error("Invalid type for negation operation: " + t, node.getPosition()))
                ));
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
