package com.dfsek.substrate.lang.node.expression.list;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;

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
    public void apply(MethodBuilder builder, BuildData data) throws ParseException {
        ParserUtil.checkWeakReferenceType(listReference, Signature.list())
                .simplify().apply(builder, data);

        ParserUtil.checkType(index, Signature.integer())
                .simplify().apply(builder, data);

        Signature ref = reference();

        if (ref.isSimple()) {
            builder.insn(ref.getType(0).arrayLoadInsn());
        } else {
            builder.aaload();
        }
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
