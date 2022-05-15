package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.List;
import io.vavr.control.Either;

import java.util.Collection;
import java.util.Collections;

public class BooleanNotNode extends ExpressionNode {
    private final Position position;
    private final ExpressionNode node;

    public BooleanNotNode(Position position, ExpressionNode node) {
        this.position = position;
        this.node = node;
    }

    public static Unchecked<BooleanNotNode> of(Position position, Unchecked<? extends ExpressionNode> node) {
        return Unchecked.of(new BooleanNotNode(position, node.get(Signature.bool())));
    }

    @Override
    public List<Either<CompileError, Op>> apply(BuildData data) throws ParseException {
        return ParserUtil.checkReferenceType(node, Signature.bool())
                .simplify()
                .apply(data)
                .appendAll(Op.invertBoolean());
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public Signature reference() {
        return Signature.bool();
    }

    @Override
    public Collection<? extends Node> contents() {
        return Collections.singleton(node);
    }
}
