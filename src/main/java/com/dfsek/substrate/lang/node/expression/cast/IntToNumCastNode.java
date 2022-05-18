package com.dfsek.substrate.lang.node.expression.cast;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.constant.DecimalNode;
import com.dfsek.substrate.lang.node.expression.constant.IntegerNode;
import com.dfsek.substrate.lexer.token.Token;
import com.dfsek.substrate.parser.ParserUtil;
import io.vavr.collection.List;
import io.vavr.control.Either;

public class IntToNumCastNode extends TypeCastNode<Integer, Double> {
    private IntToNumCastNode(Token type, Unchecked<? extends ExpressionNode> value) {
        super(type, value.get(Signature.integer()));
    }

    public static Unchecked<IntToNumCastNode> of(Token type, Unchecked<? extends ExpressionNode> value) {
        return Unchecked.of(new IntToNumCastNode(type, value));
    }

    @Override
    public List<Either<CompileError, Op>> applyCast(BuildData data) {
        return List.of(Op.i2d());
    }

    @Override
    public ExpressionNode simplify() {
        if (Node.disableOptimisation()) return this;
        if (value instanceof IntegerNode) {
            return DecimalNode.of(((IntegerNode) value).getValue(), value.getPosition())
                    .get(Signature.decimal());
        }
        return super.simplify();
    }

    @Override
    public Signature reference() {
        return Signature.decimal();
    }
}
