package com.dfsek.substrate.lang.node.expression.constant;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.error.ErrorNode;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import io.vavr.control.Either;

public class BooleanNode extends ConstantExpressionNode<Boolean> {
    private BooleanNode(boolean value, Position position) {
        super(value, position);
    }

    public static Unchecked<? extends ExpressionNode> of(String value, Position position) {
        if (value.equals("true") || value.equals("false")) {
            return Unchecked.of(new BooleanNode(Boolean.parseBoolean(value), position));
        } else {
            System.out.println("aa:" + value);
            return ErrorNode.of(position, "Malformed boolean literal: " + value, Signature.bool());
        }
    }

    public static Unchecked<BooleanNode> of(boolean value, Position position) {
        return Unchecked.of(new BooleanNode(value, position));
    }

    @Override
    public List<Either<CompileError, Op>> apply(BuildData data, LinkedHashMap<String, Value> valueMap) throws ParseException {
        if (value) {
            return List.of(Op.pushTrue()); // true
        } else {
            return List.of(Op.pushFalse()); // false
        }
    }

    @Override
    public Signature reference() {
        return Signature.bool();
    }
}
