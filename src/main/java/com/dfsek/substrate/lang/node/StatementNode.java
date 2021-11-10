package com.dfsek.substrate.lang.node;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.DataType;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class StatementNode implements Node {
    private final Position position;
    private final ExpressionNode content;

    public StatementNode(Position position, ExpressionNode content) {
        this.position = position;
        this.content = content;
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        Label statement = new Label();
        visitor.visitLabel(statement);
        content.apply(visitor, data);

        Signature ref = content.reference(data);
        for (int i = 0; i < ref.size(); i++) {
            if(ref.getType(0) == DataType.NUM) {
                visitor.visitInsn(POP2);
            } else {
                visitor.visitInsn(POP);
            }
        }

        visitor.visitLineNumber(getPosition().getLine(), statement);
    }

    @Override
    public Position getPosition() {
        return position;
    }
}
