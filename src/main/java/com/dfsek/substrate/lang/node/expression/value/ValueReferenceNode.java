package com.dfsek.substrate.lang.node.expression.value;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.lexer.token.Token;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.List;
import io.vavr.control.Either;

import java.util.Collection;
import java.util.Collections;

public class ValueReferenceNode extends ExpressionNode {
    private final Token id;

    private boolean isLambdaArgument = false;
    private boolean isLocal = false;

    private final Value value;

    private ValueReferenceNode(Token id, Value value) {
        this.id = id;
        this.value = value;
    }

    public static Unchecked<ValueReferenceNode> of(Token id, Value value) {
        return Unchecked.of(new ValueReferenceNode(id, value));
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public boolean isLambdaArgument() {
        return isLambdaArgument;
    }

    public void setLambdaArgument(boolean lambdaArgument) {
        isLambdaArgument = lambdaArgument;
    }

    @Override
    public List<Either<CompileError, Op>> apply(BuildData data) throws ParseException {
        return value.load(data);
    }

    @Override
    public Collection<? extends Node> contents() {
        return Collections.emptyList();
    }


    @Override
    public Position getPosition() {
        return id.getPosition();
    }

    @Override
    public Signature reference() {
        return value.reference();
    }

    public Token getId() {
        return id;
    }

    @Override
    public String toString() {
        return id.getContent();
    }
}
