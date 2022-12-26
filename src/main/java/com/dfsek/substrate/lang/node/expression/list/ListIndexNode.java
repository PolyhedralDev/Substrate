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
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.List;
import io.vavr.control.Either;

import java.util.Arrays;
import java.util.Collection;

public class ListIndexNode extends ExpressionNode {
    private final ExpressionNode listReference;
    private final ExpressionNode index;

    private ListIndexNode(Unchecked<? extends ExpressionNode> listReference, Unchecked<? extends ExpressionNode> index) {
        this.listReference = listReference.weak(Signature.list());
        this.index = index.get(Signature.integer());
    }

    public static Unchecked<ListIndexNode> of(Unchecked<? extends ExpressionNode> listReference, Unchecked<? extends ExpressionNode> index) {
        return Unchecked.of(new ListIndexNode(listReference, index));
    }

    @Override
    public List<Either<CompileError, Op>> apply(BuildData data) throws ParseException {
        return listReference
                .simplify().apply(data)
                .appendAll(index
                        .simplify().apply(data))
                .append(Op.invokeInterface(Classes.LIST, "get", "(I)L" + Classes.OBJECT + ";"))
                .appendAll(CompilerUtil.unbox(listReference.reference().getGenericReturn(0)));
    }

    @Override
    public Position getPosition() {
        return index.getPosition();
    }

    @Override
    public Signature reference() {
        return listReference.reference().getSimpleReturn();
    }

    @Override
    public Collection<? extends Node> contents() {
        return Arrays.asList(listReference, index);
    }

    @Override
    public String toString() {
        return listReference.toString() + "[" + index.toString() + "]";
    }
}
