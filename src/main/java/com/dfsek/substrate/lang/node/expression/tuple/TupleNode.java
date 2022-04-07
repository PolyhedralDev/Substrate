package com.dfsek.substrate.lang.node.expression.tuple;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.List;
import io.vavr.control.Either;

public class TupleNode extends ExpressionNode {
    private final List<ExpressionNode> args;
    private final Position position;

    public TupleNode(List<ExpressionNode> args, Position position) {
        this.args = args;
        this.position = position;
    }

    @Override
    public List<Either<CompileError, Op>> apply(BuildData data) throws ParseException {
        return data.tupleFactory()
                .construct(
                        reference(),
                        args.flatMap(arg -> arg.simplify().apply(data).appendAll(CompilerUtil.deconstructTuple(arg, data)))
                );
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public Signature reference() {
        return CompilerUtil.expandArguments(args);
    }

    @Override
    public Iterable<? extends Node> contents() {
        return args;
    }
}
