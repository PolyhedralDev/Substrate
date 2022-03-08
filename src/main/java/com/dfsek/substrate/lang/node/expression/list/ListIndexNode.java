package com.dfsek.substrate.lang.node.expression.list;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.lexer.read.Position;
import io.vavr.collection.List;
import io.vavr.control.Either;

import java.util.Arrays;
import java.util.Collection;

public class ListIndexNode extends ExpressionNode {
    private final ExpressionNode listReference;
    private final ExpressionNode index;

    public ListIndexNode(ExpressionNode listReference, ExpressionNode index) {
        this.listReference = listReference;
        this.index = index;
    }

    @Override
    public List<Either<CompileError, Op>> apply(BuildData data) throws ParseException {
        return ParserUtil.checkWeakReferenceType(listReference, Signature.list())
                .simplify().apply(data)
                .appendAll(ParserUtil.checkReturnType(index, Signature.integer())
                        .simplify().apply(data))
                .append(reference().arrayLoadInsn().bimap(
                        s -> Op.errorUnwrapped(s, getPosition()),
                        Op::insnUnwrapped
                ));
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
}
