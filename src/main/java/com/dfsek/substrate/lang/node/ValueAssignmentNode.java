package com.dfsek.substrate.lang.node;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.PrimitiveValue;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.lambda.LocalLambdaReferenceFunction;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.LambdaExpressionNode;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.tokenizer.Token;
import org.objectweb.asm.MethodVisitor;

public class ValueAssignmentNode implements Node {
    private final Token id;
    private final ExpressionNode value;

    public ValueAssignmentNode(Token id, ExpressionNode value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        if (data.valueExists(id.getContent())) {
            throw new ParseException("Value \"" + id.getContent() + "\" already exists in this scope.", id.getPosition());
        }

        value.apply(visitor, data);
        Signature ref = value.referenceType(data);

        if (value instanceof LambdaExpressionNode) { // register the lambda value as a function
            LambdaExpressionNode lambdaExpressionNode = (LambdaExpressionNode) value;
            data.registerValue(id.getContent(), new LocalLambdaReferenceFunction(lambdaExpressionNode.getParameters(), ref.getSimpleReturn(), id.getContent(), lambdaExpressionNode.internalParameters()), ref.frames());
        } else {
            data.registerValue(id.getContent(), new PrimitiveValue(value.referenceType(data).getSimpleReturn(), ref), value.referenceType(data).frames());
        }


        int offset = data.offset(id.getContent());
        if (ref.getSimpleReturn().isSimple()) {
            if (value instanceof LambdaExpressionNode || ref.weakEquals(Signature.list())) {
                visitor.visitVarInsn(ASTORE, offset);
            } else {
                visitor.visitVarInsn(ref.getSimpleReturn().getType(0).storeInsn(), offset);
            }
        } else {
            if(ref.equals(Signature.empty())) { // void non-lambda expression
                throw new ParseException("Cannot assign VOID expression to value", getPosition());
            }
            visitor.visitVarInsn(ASTORE, offset);
        }
    }

    @Override
    public Position getPosition() {
        return id.getPosition();
    }
}
