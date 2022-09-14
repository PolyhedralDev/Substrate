package com.dfsek.substrate.lang.node.expression.value;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.value.BakedValue;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.constant.ConstantExpressionNode;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.lexer.token.Token;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import io.vavr.collection.Stream;
import io.vavr.control.Either;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class ConstantValueNode extends ExpressionNode {
    private final Token id;

    private final ConstantExpressionNode<?> value;

    public ConstantValueNode(Token id, ConstantExpressionNode<?> value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public List<Either<CompileError, Op>> apply(BuildData data, LinkedHashMap<String, Value> values) throws ParseException {
        if (values.containsKey(id.getContent())) {
            throw new ParseException("Value \"" + id.getContent() + "\" already exists in this scope.", id.getPosition());
        }
        LinkedHashMap<String, Value> newValues = values.put(id.getContent(), new BakedValue(value));
        return value.apply(data, newValues);
    }

    @Override
    public Signature reference() {
        return value.reference();
    }

    @Override
    protected Collection<? extends Node> contents() {
        return Set.of(value);
    }

    @Override
    public Position getPosition() {
        return value.getPosition();
    }
}
