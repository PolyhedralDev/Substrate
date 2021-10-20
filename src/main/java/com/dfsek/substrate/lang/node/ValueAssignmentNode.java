package com.dfsek.substrate.lang.node;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.PrimitiveValue;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.lambda.LocalLambdaReferenceFunction;
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

        if (value instanceof LambdaExpressionNode) { // register the lambda value as a function
            LambdaExpressionNode lambdaExpressionNode = (LambdaExpressionNode) value;
            data.registerValue(id.getContent(), new LocalLambdaReferenceFunction(lambdaExpressionNode.getParameters(), value.returnType(data), id.getContent()), value.returnType(data).frames());
        } else {
            data.registerValue(id.getContent(), new PrimitiveValue(value.returnType(data), id.getContent()), 1);
        }

        value.apply(visitor, data);

        int offset = data.offset(id.getContent());
        if (value.returnType(data).isSimple()) {
            visitor.visitVarInsn(value.returnType(data).getType(0).storeInsn(), offset);
        } else {
            visitor.visitVarInsn(ASTORE, offset);
        }
    }

    @Override
    public Position getPosition() {
        return id.getPosition();
    }
}
