package com.dfsek.substrate.lang.node.expression.list;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.List;
import io.vavr.control.Either;
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
    public List<Either<CompileError, Op>> apply(BuildData data) throws ParseException {
        int lowerRef = data.getOffset();
        data.offsetInc(1);

        int totalRef = data.getOffset();
        data.offsetInc(1);

        Label start = new Label();
        Label end = new Label();

        return upper.simplify().apply(data)
                .appendAll(lower.simplify().apply(data))
                .append(Op.dup())
                .append(Op.iStore(lowerRef))
                .append(Op.iSub())
                .append(Op.dup())
                .append(Op.iStore(totalRef))
                .append(Op.newArray(T_INT)) // [ARef]
                .append(Op.pushInt(0)) // [ARef, i]

                // loop
                .append(Op.label(start))

                .append(Op.dup()) // [ARef, i, i]
                .append(Op.iLoad(totalRef))
                .append(Op.ifICmpGE(end)) // [ARef, i]

                .append(Op.dup2()) // [ARef, i, ARef, i]
                .append(Op.dup()) // [ARef, i, ARef, i, i]

                .append(Op.iLoad(lowerRef)) // [ARef, i, ARef, i, i, size]
                .append(Op.iAdd()) // [ARef, i, ARef, i, n]

                .append(Op.iaStore()) // [Aref, i]

                .append(Op.pushInt(1)) // [ARef, i, 1]
                .append(Op.iAdd()) // [ARef, i]

                .append(Op.goTo(start))

                .append(Op.label(end))
                .append(Op.pop());
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
