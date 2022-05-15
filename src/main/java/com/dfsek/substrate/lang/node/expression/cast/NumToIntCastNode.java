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

public class NumToIntCastNode extends TypeCastNode<Double, Integer> {
    private NumToIntCastNode(Token type, Unchecked<? extends ExpressionNode> value) {
        super(type, value.get(Signature.decimal()));
    }

    public static Unchecked<NumToIntCastNode> of(Token type, Unchecked<? extends ExpressionNode> value) {
        return Unchecked.of(new NumToIntCastNode(type, value));
    }

    @Override
    public List<Either<CompileError, Op>> applyCast(BuildData data) {
        ParserUtil.checkReturnType(value, Signature.decimal());
        return List.of(Op.d2i());
    }

    @Override
    public ExpressionNode simplify() {
        if (Node.disableOptimisation()) return this;
        if (value instanceof DecimalNode) {
            return IntegerNode.of(((DecimalNode) value).getValue().intValue(), value.getPosition())
                    .get(Signature.integer());
        }
        return super.simplify();
    }

    @Override
    public Signature reference() {
        return Signature.integer();
    }
}
