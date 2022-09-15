package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.List;
import io.vavr.control.Either;

public class NothingNode implements Node {
    private static final NothingNode NODE = new NothingNode();
    private NothingNode() {

    }

    public static NothingNode nothing() {
        return NODE;
    }
    @Override
    public List<Either<CompileError, Op>> apply(BuildData data) throws ParseException {
        return List.empty();
    }

    @Override
    public Position getPosition() {
        return Position.getNull();
    }
}
