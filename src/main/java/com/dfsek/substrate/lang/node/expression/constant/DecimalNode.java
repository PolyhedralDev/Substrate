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

public class DecimalNode extends ConstantExpressionNode<Double> {
    private DecimalNode(double value, Position position) {
        super(value, position);
    }

    public static Unchecked<DecimalNode> of(double value, Position position) {
        return Unchecked.of(new DecimalNode(value, position));
    }

    public static Unchecked<? extends ExpressionNode> of(String value, Position position) {
        try {
            return Unchecked.of(new DecimalNode(Double.parseDouble(value), position));
        } catch (NumberFormatException e) {
            return ErrorNode.of(position, "Malformed decimal literal: " + value, Signature.decimal());
        }
    }
    @Override
    public List<Either<CompileError, Op>> apply(BuildData data, LinkedHashMap<String, Value> valueMap) throws ParseException {
        return List.of(Op.pushDouble(value));
    }

    @Override
    public Signature reference() {
        return Signature.decimal();
    }
}
