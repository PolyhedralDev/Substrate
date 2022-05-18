package com.dfsek.substrate.lang.node.expression.cast;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.constant.ConstantExpressionNode;
import com.dfsek.substrate.lang.node.expression.constant.DecimalNode;
import com.dfsek.substrate.lang.node.expression.constant.IntegerNode;
import com.dfsek.substrate.lang.node.expression.constant.StringNode;
import com.dfsek.substrate.lexer.token.Token;
import com.dfsek.substrate.parser.ParserUtil;
import io.vavr.collection.List;
import io.vavr.control.Either;

public class ToStringNode extends TypeCastNode<Object, String> {
    private ToStringNode(Token type, Unchecked<? extends ExpressionNode> value) {
        super(type, value.get(Signature.decimal(), Signature.integer(), Signature.bool()));
    }

    public static Unchecked<ToStringNode> of(Token type, Unchecked<? extends ExpressionNode> value) {
        return Unchecked.of(new ToStringNode(type, value));
    }

    @Override
    public List<Either<CompileError, Op>> applyCast(BuildData data) {
        Signature ref = value
                .reference()
                .getSimpleReturn();
        if (ref.equals(Signature.integer())) {
            return List.of(Op.invokeStatic("java/lang/Integer",
                    "toString",
                    "(I)Ljava/lang/String;"));
        } else if (ref.equals(Signature.decimal())) {
            return List.of(Op.invokeStatic("java/lang/Double",
                    "toString",
                    "(D)Ljava/lang/String;"));
        } else if (ref.equals(Signature.bool())) {
            return List.of(Op.invokeStatic("java/lang/Boolean",
                    "toString",
                    "(Z)Ljava/lang/String;"));
        }
        return List.of(Op.error("Invalid string target type: " + ref, getPosition()));
    }

    @Override
    public ExpressionNode simplify() {
        if (Node.disableOptimisation()) return this;
        if (value instanceof DecimalNode || value instanceof IntegerNode) {
            return StringNode.of(((ConstantExpressionNode<?>) value).getValue().toString(), value.getPosition())
                    .get(Signature.string());
        }
        return super.simplify();
    }

    @Override
    public Signature reference() {
        return Signature.string();
    }
}
