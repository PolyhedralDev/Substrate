package com.dfsek.substrate.lang.node.expression.list;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Either;

import java.util.Collection;


public class ListNode extends ExpressionNode {
    private final List<Tuple2<ExpressionNode, Integer>> elements;
    private final Position position;

    public ListNode(List<ExpressionNode> elements, Position position) {
        this.elements = elements.zipWithIndex();
        this.position = position;
    }

    @Override
    public io.vavr.collection.List<Either<CompileError, Op>> apply(BuildData data) throws ParseException {
        Signature signature = elements.get(0)._1.reference();
        elements.forEach(element -> ParserUtil.checkReferenceType(element._1, signature));

        return List.of(Op.pushInt(elements.size()))
                .append(signature.newArrayInsn(position))
                .appendAll(elements.flatMap(element ->
                        List.of(Op.dup())
                                .append(Op.pushInt(element._2))
                                .appendAll(element._1.apply(data))
                                .append(element._1.reference().arrayStoreInsn().bimap(
                                        s -> Op.errorUnwrapped(s, element._1.getPosition()),
                                        Op::insnUnwrapped
                                )))
                );
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public Signature reference() {
        return Signature.list().applyGenericReturn(0, elements.get(0)._1.reference());
    }

    @Override
    public Collection<? extends Node> contents() {
        return elements.map(Tuple2::_1).asJava();
    }
}
