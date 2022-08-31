package com.dfsek.substrate.lang.node.expression.constant;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import io.vavr.control.Either;

public class StringNode extends ConstantExpressionNode<String> {
    private StringNode(String value, Position position) {
        super(value, position);
    }

    public static Unchecked<StringNode> of(String value, Position position) {
        return Unchecked.of(new StringNode(value, position));
    }

    @Override
    public List<Either<CompileError, Op>> apply(BuildData data, LinkedHashMap<String, Value> values) throws ParseException {
        return List.of(Op.pushConst(value)); // LDC string content
    }

    @Override
    public Signature reference() {
        return Signature.string();
    }
}
