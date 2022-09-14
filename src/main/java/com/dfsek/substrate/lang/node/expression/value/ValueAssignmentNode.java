package com.dfsek.substrate.lang.node.expression.value;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.compiler.value.PrimitiveValue;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.constant.ConstantExpressionNode;
import com.dfsek.substrate.lang.node.expression.function.LambdaExpressionNode;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.lexer.token.Token;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import io.vavr.control.Either;

import java.util.Collection;
import java.util.Collections;

import static com.dfsek.substrate.lang.compiler.codegen.bytes.Op.dup;

public class ValueAssignmentNode extends ExpressionNode {
    private final Token id;
    private final ExpressionNode value;

    private ValueAssignmentNode(Token id, ExpressionNode value) {
        this.id = id;
        this.value = value.simplify();
    }

    public static Unchecked<ValueAssignmentNode> of(Token id, Unchecked<? extends ExpressionNode> value) {
        return Unchecked.of(new ValueAssignmentNode(id, value.unchecked()));
    }

    public Token getId() {
        return id;
    }

    @Override
    public List<Either<CompileError, Op>> apply(BuildData data, LinkedHashMap<String, Value> values) throws ParseException {
        Signature ref = value.reference();

        if (value instanceof LambdaExpressionNode lambdaExpressionNode) {
            lambdaExpressionNode.setSelf(id.getContent());
        }

        int width = ref.frames();
        int offset = CompilerUtil.getTotalOffset(values) + width;

        LinkedHashMap<String, Value> newValues = values.put(id.getContent(), new PrimitiveValue(ref, id.toString(), width));

        return value.apply(data, newValues)
                .append(dup(ref))
                .append(ref
                        .storeInsn()
                        .mapLeft(m -> Op.errorUnwrapped(m, value.getPosition()))
                        .map(insn -> Op.varInsnUnwrapped(insn, offset)));
    }

    @Override
    public ExpressionNode simplify() {
        if (Node.disableOptimisation()) return this;
        if (value instanceof ConstantExpressionNode) {
            return new ConstantValueNode(id, (ConstantExpressionNode<?>) value);
        }
        return this;
    }

    @Override
    public Position getPosition() {
        return id.getPosition();
    }

    @Override
    public Signature reference() {
        return value.reference();
    }

    @Override
    public Collection<Node> contents() {
        return Collections.singleton(value);
    }
}
