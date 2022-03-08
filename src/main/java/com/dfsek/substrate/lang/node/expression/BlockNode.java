package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.lexer.read.Position;
import io.vavr.collection.List;
import io.vavr.control.Either;

import java.util.Collection;

public class BlockNode extends ExpressionNode {
    private final List<Node> contents;

    private final List<ReturnNode> returnType;
    private final Position position;

    public BlockNode(List<Node> contents, List<ReturnNode> returnType, Position position) {
        this.contents = contents;
        this.position = position;
        this.returnType = returnType;
    }

    @Override
    public io.vavr.collection.List<Either<CompileError, Op>> apply(BuildData data) throws ParseException {
        BuildData scope = data.sub();
        return contents.flatMap(node -> node.simplify().apply(scope));
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public Signature reference() {
        if (returnType.isEmpty()) return Signature.empty();
        Signature test = returnType.get(0).reference();
        returnType.forEach(type -> ParserUtil.checkReferenceType(type, test));
        return test;
    }

    @Override
    public Collection<Node> contents() {
        return contents.asJava();
    }
}
