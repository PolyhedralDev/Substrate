package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.lexer.read.Position;
import io.vavr.collection.List;
import io.vavr.control.Either;

import java.util.Collection;
import java.util.Collections;

import static io.vavr.API.*;

public class NumberInverseNode extends ExpressionNode {
    private final Position position;
    private final ExpressionNode node;

    public NumberInverseNode(Position position, ExpressionNode node) {
        this.position = position;
        this.node = node;
    }

    @Override
    public List<Either<CompileError, Op>> apply(BuildData data) throws ParseException {
        return ParserUtil.checkReferenceType(node, Signature.integer(), Signature.decimal()).simplify().apply(data)
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
