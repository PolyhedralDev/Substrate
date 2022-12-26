package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import io.vavr.control.Either;

import java.util.Collection;
import java.util.Collections;

public class ReturnNode extends ExpressionNode {
    private final Position position;

    private final ExpressionNode value;

    private final Signature record;

    private ReturnNode(Position position, Unchecked<? extends ExpressionNode> value, Signature record) {
        this.position = position;
        this.value = value.unchecked();
        this.record = record;
    }

    public static Unchecked<ReturnNode> of(Position position, Unchecked<? extends ExpressionNode> value, Signature record) {
        return Unchecked.of(new ReturnNode(position, value, record));
    }

    @Override
    public List<Either<CompileError, Op>> apply(BuildData data, LinkedHashMap<String, Value> valueMap) throws ParseException {
        if (record != null && record.size() == 1) {
            return List.of(record.equals(value.reference()) ? Op.nothing() : Op.error("Invalid return type: " + value.reference(), value.getPosition()))
                    .appendAll(data.tupleFactory().construct(record, value.simplify().apply(data, valueMap)))
                    .append(Op.aReturn());
        } else {
            Signature ret = value.reference();
            return value.simplify().apply(data, valueMap)
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

    @Override
    public String toString() {
        return value.toString();
    }
}
