package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;

import java.util.Collection;

public class BlockNode extends ExpressionNode {
    private final List<Node> contents;

    private final Option<ReturnNode> returnType;
    private final Position position;

    private BlockNode(List<Node> contents, Option<ReturnNode> returnType, Position position) {
        this.contents = contents;
        this.position = position;
        this.returnType = returnType;
    }

    public static Unchecked<BlockNode> of(List<Node> contents, Option<ReturnNode> returnType, Position position) {
        return Unchecked.of(new BlockNode(contents, returnType, position));
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
        return returnType.map(ExpressionNode::reference).getOrElse(Signature.empty());
    }

    @Override
    public Collection<Node> contents() {
        return contents.asJava();
    }
}
