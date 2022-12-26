package com.dfsek.substrate.lang.node.expression.value;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
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

public class ValueAssignmentNode extends ExpressionNode {
    private final Token id;
    private final ExpressionNode value;

    private final int offset;

    private final Value reference;

    private ValueAssignmentNode(Token id, ExpressionNode value, int offset, Value reference) {
        this.id = id;
        this.value = value.simplify();
        this.offset = offset;
        this.reference = reference;
    }

    public static Unchecked<ValueAssignmentNode> of(Token id, Unchecked<? extends ExpressionNode> value, int offset, Value reference) {
        return Unchecked.of(new ValueAssignmentNode(id, value.unchecked(), offset, reference));
    }

    public Token getId() {
        return id;
    }

    public Value getReference() {
        return reference;
    }

    @Override
    public List<Either<CompileError, Op>> apply(BuildData data, LinkedHashMap<String, Value> valueMap) throws ParseException {
        Signature ref = value.reference();

        if (value instanceof LambdaExpressionNode lambdaExpressionNode) {
            lambdaExpressionNode.setSelf(id.getContent());
        }


        return value.apply(data, valueMap)
                .append(ref
                        .storeInsn()
                        .mapLeft(m -> Op.errorUnwrapped(m, value.getPosition()))
                        .map(insn -> Op.varInsnUnwrapped(insn, offset)));
    }

    @Override
    public ExpressionNode simplify() {
        if (Node.disableOptimisation()) return this;
        if (value instanceof ConstantExpressionNode) {
            return new ConstantValueNode((ConstantExpressionNode<?>) value);
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

    @Override
    public String toString() {
        return id.getContent() + " = " + value.toString();
    }
}
