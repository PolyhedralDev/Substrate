package com.dfsek.substrate.lang.node.expression.list;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.Classes;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import io.vavr.control.Either;

import java.util.Arrays;
import java.util.Collection;

public class RangeNode extends ExpressionNode {
    private final ExpressionNode lower;
    private final ExpressionNode upper;

    private final Position position;

    private RangeNode(Unchecked<? extends ExpressionNode> lower, Unchecked<? extends ExpressionNode> upper, Position position) {
        this.lower = lower.get(Signature.integer());
        this.upper = upper.get(Signature.integer());
        this.position = position;
    }

    public static Unchecked<RangeNode> of(Unchecked<? extends ExpressionNode> lower, Unchecked<? extends ExpressionNode> upper, Position position) {
        return Unchecked.of(new RangeNode(lower, upper, position));
    }

    @Override
    public List<Either<CompileError, Op>> apply(BuildData data, LinkedHashMap<String, Value> valueMap) throws ParseException {
        return lower.simplify().apply(data, valueMap)
                .appendAll(upper.simplify().apply(data, valueMap))
                .append(Op.invokeStaticInterface(Classes.LIST, "range", "(II)L" + Classes.LIST + ";"));
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

    @Override
    public String toString() {
        return "(" + lower.toString() + " .. " + upper.toString() + ")";
    }
}
