package com.dfsek.substrate.lang.node;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.NodeHolder;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import org.objectweb.asm.Label;

import java.util.Collection;
import java.util.Collections;

public class StatementNode extends NodeHolder {
    private final Position position;
    private final ExpressionNode content;

    public StatementNode(Position position, ExpressionNode content) {
        this.position = position;
        this.content = content;
    }

    @Override
    public void apply(MethodBuilder builder, BuildData data) throws ParseException {
        Label statement = new Label();
        builder.label(statement);
        content.simplify().apply(builder, data);

        Signature ref = content.reference();
        if (!ref.equals(Signature.empty())) {
            if (ref.equals(Signature.decimal())) builder.pop2();
            else builder.pop();
        }

        builder.lineNumber(getPosition().getLine(), statement);
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    protected Collection<? extends Node> contents() {
        return Collections.singleton(content);
    }
}
