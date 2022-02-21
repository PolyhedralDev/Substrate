package com.dfsek.substrate.lang.node.expression.value;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.value.PrimitiveValue;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.constant.ConstantExpressionNode;
import com.dfsek.substrate.lang.node.expression.function.LambdaExpressionNode;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.tokenizer.Token;

import java.util.Collection;
import java.util.Collections;

public class ValueAssignmentNode extends ExpressionNode {
    private final Token id;
    private final ExpressionNode value;

    public ValueAssignmentNode(Token id, ExpressionNode value) {
        this.id = id;
        this.value = value.simplify();
    }

    public Token getId() {
        return id;
    }

    @Override
    public void apply(MethodBuilder builder, BuildData data) throws ParseException {
        Signature ref = value.reference();


        data.registerValue(id.getContent(), new PrimitiveValue(ref, data.getOffset()), value.reference().frames());

        if (value instanceof LambdaExpressionNode) {
            ((LambdaExpressionNode) value).setSelf(id.getContent());
        }
        value.apply(builder, data);

        if (ref.equals(Signature.decimal())) {
            builder.dup2();
        } else {
            builder.dup();
        }


        int offset = data.offset(id.getContent());
        if (ref.isSimple() && ref.getSimpleReturn().isSimple()) {
            if (value instanceof LambdaExpressionNode || ref.weakEquals(Signature.list())) {
                builder.aStore(offset);
            } else {
                builder.varInsn(ref.getSimpleReturn().getType(0).storeInsn(), offset);
            }
        } else {
            if (ref.equals(Signature.empty())) { // void non-lambda expression
                throw new ParseException("Cannot assign VOID expression to value", getPosition());
            }
            builder.aStore(offset);
        }
    }

    @Override
    public ExpressionNode simplify() {
        if(Node.disableOptimisation()) return this;
        if (value instanceof ConstantExpressionNode) {
            return new ConstantValueNode(id, (ConstantExpressionNode<?>) value);
        }
        return this;
    }

    @Override
    public Position getPosition() {
        return id.getPosition();
    }

    @Override
    public Signature reference() {
        return value.reference();
    }

    @Override
    public Collection<Node> contents() {
        return Collections.singleton(value);
    }
}
