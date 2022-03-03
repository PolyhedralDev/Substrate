package com.dfsek.substrate.lang.node;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.NodeHolder;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.lexer.read.Position;
import io.vavr.collection.List;
import io.vavr.control.Either;
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
    public List<Either<CompileError, Op>> apply(BuildData data) throws ParseException {
        Signature ref = content.reference();
        return content.simplify().apply(data)
                .append(ref.equals(Signature.empty()) ? Op.nothing() : Op.pop(ref));
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
