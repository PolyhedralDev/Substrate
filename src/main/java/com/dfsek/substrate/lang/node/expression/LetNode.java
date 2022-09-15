package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.exception.ParseException;
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
    public List<Either<CompileError, Op>> apply(BuildData data) throws ParseException {
        return localValues.values()
                .foldLeft(List.<Either<CompileError, Op>>empty(), (l, n) -> l.appendAll(n.apply(data)))
                .appendAll(node.apply(data));
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
