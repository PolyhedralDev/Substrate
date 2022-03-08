package com.dfsek.substrate.lang.node.expression.cast;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.constant.DecimalNode;
import com.dfsek.substrate.lang.node.expression.constant.IntegerNode;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.lexer.token.Token;
import io.vavr.collection.List;
import io.vavr.control.Either;

public class IntToNumCastNode extends TypeCastNode<Integer, Double> {
    public IntToNumCastNode(Token type, ExpressionNode value) {
        super(type, value);
    }

    @Override
    public List<Either<CompileError, Op>> applyCast(BuildData data) {
        ParserUtil.checkReturnType(value, Signature.integer());
        return List.of(Op.i2d());
    }

    @Override
    public ExpressionNode simplify() {
        if(Node.disableOptimisation()) return this;
        if (value instanceof IntegerNode) {
            return new DecimalNode(((IntegerNode) value).getValue(), value.getPosition());
        }
        return super.simplify();
    }

    @Override
    public Signature reference() {
        return Signature.decimal();
    }
}
