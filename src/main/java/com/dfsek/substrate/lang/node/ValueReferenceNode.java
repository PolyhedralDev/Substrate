package com.dfsek.substrate.lang.node;

import com.dfsek.substrate.lang.compiler.BuildData;
import com.dfsek.substrate.lang.compiler.Signature;
import com.dfsek.substrate.lang.compiler.Value;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.tokenizer.Token;
import org.objectweb.asm.MethodVisitor;

public class ValueReferenceNode extends ExpressionNode {
    private final Token id;

    public ValueReferenceNode(Token id) {
        this.id = id;
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        if(!data.valueExists(id.getContent())) {
            throw new ParseException("No such value: " + id.getContent(), id.getPosition());
        }
        Value value = data.getValue(id.getContent());
        int offset = data.offset(id.getContent());
        if(value.returnType().isSimple()) {
            visitor.visitVarInsn(value.returnType().getType(0).loadInsn(), offset);
        } else {
            for (int i = 0; i < value.returnType().size(); i++) {
                visitor.visitVarInsn(ALOAD, offset);

                visitor.visitMethodInsn(INVOKEVIRTUAL,
                        "com/dfsek/substrate/lang/internal/tuple/TupleIMPL" + value.returnType().classDescriptor(),
                        "param" + i,
                        "()" + value.returnType().getType(i).descriptor(),
                        false);
            }
        }
    }

    @Override
    public Position getPosition() {
        return id.getPosition();
    }

    @Override
    public Signature returnType(BuildData data) {
        if(!data.valueExists(id.getContent())) {
            throw new ParseException("No such value: " + id.getContent(), id.getPosition());
        }
        return data.getValue(id.getContent()).returnType();
    }
}
