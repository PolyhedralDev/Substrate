package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.compiler.value.PrimitiveValue;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.lang.node.expression.function.LambdaExpressionNode;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.Tuple2;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Either;

import java.util.function.BiFunction;

import static com.dfsek.substrate.lang.compiler.codegen.bytes.Op.dup;

public class LetNode extends ExpressionNode {
    private final Map<String, ExpressionNode> localValues;

    private final ExpressionNode node;

    private LetNode(Map<String, ExpressionNode> localValues, ExpressionNode node) {
        this.localValues = localValues;
        this.node = node;
    }

    public static LetNode of(Map<String, Unchecked<? extends ExpressionNode>> values, Unchecked<? extends ExpressionNode> node) {
        return new LetNode(values.mapValues(Unchecked::unchecked), node.unchecked());
    }

    @Override
    public List<Either<CompileError, Op>> apply(BuildData data, LinkedHashMap<String, Value> values) throws ParseException {
        return localValues.foldLeft(new Tuple2<List<Either<CompileError, Op>>, LinkedHashMap<String, Value>>(List.empty(), values), (listLinkedHashMapTuple2, value) -> applyAssignment(data, listLinkedHashMapTuple2._2, value._2, value._1))
                .apply((ops, newValues) -> ops.appendAll(node.apply(data, newValues)));
    }

    private Tuple2<List<Either<CompileError, Op>>, LinkedHashMap<String, Value>> applyAssignment(BuildData data, LinkedHashMap<String, Value> values, ExpressionNode value, String id) throws ParseException {
        Signature ref = value.reference();

        if (value instanceof LambdaExpressionNode) {
            ((LambdaExpressionNode) value).setSelf(id);
        }

        int width = ref.frames();
        int offset = CompilerUtil.getTotalOffset(values) + width;

        LinkedHashMap<String, Value> newValues = values.put(id, new PrimitiveValue(ref, id, width));

        return new Tuple2<>(value.apply(data, newValues)
                .append(dup(ref))
                .append(ref
                        .storeInsn()
                        .mapLeft(m -> Op.errorUnwrapped(m, value.getPosition()))
                        .map(insn -> Op.varInsnUnwrapped(insn, offset))),
                newValues);
    }

    @Override
    public Signature reference() {
        return node.reference();
    }

    @Override
    protected Iterable<? extends Node> contents() {
        return null;
    }

    @Override
    public Position getPosition() {
        return null;
    }
}
