package com.dfsek.substrate.lang.node.expression.constant;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.error.ErrorNode;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.List;
import io.vavr.control.Either;

public class IntegerNode extends ConstantExpressionNode<Integer> {
    private IntegerNode(int value, Position position) {
        super(value, position);
    }

    public static Unchecked<IntegerNode> of(int value, Position position) {
        return Unchecked.of(new IntegerNode(value, position));
    }

    public static Unchecked<? extends ExpressionNode> of(String value, Position position) {
        try {
            return Unchecked.of(new IntegerNode(Integer.parseInt(value), position));
        } catch (NumberFormatException e) {
            return ErrorNode.of(position, "Malformed integer literal: " + value);
        }
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
