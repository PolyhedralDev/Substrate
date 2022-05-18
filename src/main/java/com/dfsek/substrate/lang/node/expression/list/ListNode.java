package com.dfsek.substrate.lang.node.expression.list;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.Classes;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;

import java.util.Collection;


public class ListNode extends ExpressionNode {
    private final List<Tuple2<ExpressionNode, Integer>> elements;
    private final Position position;

    private ListNode(List<ExpressionNode> elements, Position position) {
        this.elements = elements.zipWithIndex();
        this.position = position;
    }

    public static Unchecked<ListNode> of(List<Unchecked<? extends ExpressionNode>> elements, Position position) {
        Option<Signature> headType = elements.headOption().map(Unchecked::reference);
        return Unchecked.of(new ListNode(headType.map(type -> elements.map(e -> e.get(type))).getOrElse(List.empty()), position));
    }

    @Override
    public io.vavr.collection.List<Either<CompileError, Op>> apply(BuildData data) throws ParseException {
        return List.of(Op.invokeStaticInterface(Classes.LIST, "empty", "()L" + Classes.LIST + ";"))
                .appendAll(elements.flatMap(element ->
                        element._1.apply(data)
                                .appendAll(CompilerUtil.box(element._1))
                                .append(Op.invokeInterface(Classes.LIST, "append", "(L" + Classes.OBJECT + ";)L" + Classes.LIST + ";")))
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
