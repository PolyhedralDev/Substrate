package com.dfsek.substrate.lang.node.expression.constant;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.List;
import io.vavr.control.Either;

public class IntegerNode extends ConstantExpressionNode<Integer> {
    public IntegerNode(int value, Position position) {
        super(value, position);
    }

    @Override
    public List<Either<CompileError, Op>> apply(BuildData data) throws ParseException {
        return List.of(Op.pushInt(value));
    }

    @Override
    public Signature reference() {
        return Signature.integer();
    }
}
