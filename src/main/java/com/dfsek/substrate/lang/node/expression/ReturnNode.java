package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.List;
import io.vavr.control.Either;

import java.util.Collection;
import java.util.Collections;

public class ReturnNode extends ExpressionNode {
    private final Position position;

    private final ExpressionNode value;

    private final Signature record;

    public ReturnNode(Position position, ExpressionNode value, Signature record) {
        this.position = position;
        this.value = value;
        this.record = record;
    }

    @Override
    public List<Either<CompileError, Op>> apply(BuildData data) throws ParseException {
        if (value == null) return List.empty();
        else {
            Signature ret = value.reference();
            return value.simplify().apply(data)
                    .append(ret
                            .retInsn()
                            .mapLeft(m -> Op.errorUnwrapped(m, value.getPosition()))
                            .map(Op::insnUnwrapped));
        }
    }

    @Override
    public Position getPosition() {
        return position;
    }

    public Signature reference() {
        if (value == null) return Signature.empty();
        return value.reference();
    }

    @Override
    public Collection<Node> contents() {
        return Collections.singleton(value);
    }
}
