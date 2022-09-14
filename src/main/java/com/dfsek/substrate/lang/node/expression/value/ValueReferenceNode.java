package com.dfsek.substrate.lang.node.expression.value;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.lexer.token.Token;
import com.dfsek.substrate.parser.ParserScope;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.List;
import io.vavr.control.Either;

import java.util.Collection;
import java.util.Collections;

public class ValueReferenceNode extends ExpressionNode {
    private final Token id;
    private final Signature signature;

    private boolean isLambdaArgument = false;
    private boolean isLocal = false;

    private ValueReferenceNode(Token id, Signature signature) {
        this.id = id;
        this.signature = signature;
    }

    public static Unchecked<ValueReferenceNode> of(Token id, Signature signature) {
        return Unchecked.of(new ValueReferenceNode(id, signature));
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
    public List<Either<CompileError, Op>> apply(BuildData data, ParserScope scope) throws ParseException {
        return Op.getValue(values, data, id.getContent(), id.getPosition());
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
        return signature;
    }

    public Token getId() {
        return id;
    }
}
