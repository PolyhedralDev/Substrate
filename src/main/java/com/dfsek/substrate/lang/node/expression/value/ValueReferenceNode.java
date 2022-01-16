package com.dfsek.substrate.lang.node.expression.value;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.tokenizer.Token;

import java.util.Collection;
import java.util.Collections;

public class ValueReferenceNode extends ExpressionNode {
    private final Token id;

    private boolean isLambdaArgument = false;
    private boolean isLocal = false;

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLambdaArgument(boolean lambdaArgument) {
        isLambdaArgument = lambdaArgument;
    }

    public boolean isLambdaArgument() {
        return isLambdaArgument;
    }

    public ValueReferenceNode(Token id) {
        this.id = id;
    }

    @Override
    public void apply(MethodBuilder builder, BuildData data) throws ParseException {
        if (!data.valueExists(id.getContent())) {
            throw new ParseException("No such value: " + id.getContent(), id.getPosition());
        }

        Value value = data.getValue(id.getContent());
        value.load(builder, data);
    }

    @Override
    public void applyReferential(MethodBuilder visitor, BuildData data) {
        if (!data.valueExists(id.getContent())) {
            throw new ParseException("No such value: " + id.getContent(), id.getPosition());
        }
        data.getValue(id.getContent()).load(visitor, data);
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
    public Signature reference(BuildData data) {
        if (!data.valueExists(id.getContent())) {
            System.out.println(data.getValues());
            throw new ParseException("No such value: " + id.getContent(), id.getPosition());
        }
        return data.getValue(id.getContent()).reference();
    }

    public Token getId() {
        return id;
    }
}
