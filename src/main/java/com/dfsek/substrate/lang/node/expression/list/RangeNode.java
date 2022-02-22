package com.dfsek.substrate.lang.node.expression.list;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.lexer.read.Position;
import org.objectweb.asm.Label;

import java.util.Arrays;
import java.util.Collection;

public class RangeNode extends ExpressionNode {
    private final ExpressionNode lower;
    private final ExpressionNode upper;

    private final Position position;

    public RangeNode(ExpressionNode lower, ExpressionNode upper, Position position) {
        this.lower = lower;
        this.upper = upper;
        this.position = position;
    }

    @Override
    public void apply(MethodBuilder builder, BuildData data) throws ParseException {
        upper.simplify().apply(builder, data);

        lower.simplify().apply(builder, data);
        builder.dup();

        int lowerRef = data.getOffset();
        data.offsetInc(1);
        builder.iStore(lowerRef)
                .iSub()
                .dup();
        int totalRef = data.getOffset();
        data.offsetInc(1);

        builder.iStore(totalRef);

        builder.newArray(T_INT) // [ARef]
                .pushInt(0); // [ARef, i]

        Label start = new Label();
        Label end = new Label();

        builder.label(start)

                .dup() // [ARef, i, i]
                .iLoad(totalRef)
                .ifICmpGE(end) // [ARef, i]

                .dup2() // [ARef, i, ARef, i]
                .dup() // [ARef, i, ARef, i, i]

                .iLoad(lowerRef) // [ARef, i, ARef, i, i, size]
                .iAdd() // [ARef, i, ARef, i, n]

                .iastore() // [ARef, i]

                .pushInt(1) // [ARef, i, 1]
                .iAdd() // [ARef, i]


                .goTo(start)

                .label(end)
                .pop(); // [ARef]
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public Signature reference() {
        return Signature.list().applyGenericReturn(0, Signature.integer());
    }

    @Override
    public Collection<? extends Node> contents() {
        return Arrays.asList(lower, upper);
    }
}
