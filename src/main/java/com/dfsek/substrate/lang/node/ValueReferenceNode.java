package com.dfsek.substrate.lang.node;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.lang.internal.Tuple;
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
        if (!data.valueExists(id.getContent())) {
            throw new ParseException("No such value: " + id.getContent(), id.getPosition());
        }

        Value value = data.getValue(id.getContent());
        if (value.reference().getGenericReturn(0).size() <= 1) {
            load(visitor, data);
        } else {
            for (int i = 0; i < value.reference().getGenericReturn(0).size(); i++) {
                load(visitor, data);

                visitor.visitMethodInsn(INVOKEVIRTUAL,
                        CompilerUtil.internalName(Tuple.class) + "IMPL_" + value.reference().getGenericReturn(0).classDescriptor(),
                        "param" + i,
                        "()" + value.reference().getGenericReturn(0).getType(i).descriptor(),
                        false);
            }
        }
    }

    @Override
    public void applyReferential(MethodVisitor visitor, BuildData data) {
        if (!data.valueExists(id.getContent())) {
            throw new ParseException("No such value: " + id.getContent(), id.getPosition());
        }
        load(visitor, data);
    }

    private void load(MethodVisitor visitor, BuildData data) {
        if (data.isShadowed(id.getContent())) {
            Value value = data.getShadowValue(id.getContent());
            visitor.visitVarInsn(ALOAD, 0);
            visitor.visitFieldInsn(GETFIELD,
                    data.getClassName(),
                    "scope" + data.getShadowField(id.getContent()),
                    value.reference().internalDescriptor());
        } else {
            Value value = data.getValue(id.getContent());
            int offset = data.offset(id.getContent());
            visitor.visitVarInsn(value.reference().getType(0).loadInsn(), offset);
        }
    }

    @Override
    public Position getPosition() {
        return id.getPosition();
    }

    @Override
    public Signature referenceType(BuildData data) {
        if (!data.valueExists(id.getContent())) {
            throw new ParseException("No such value: " + id.getContent(), id.getPosition());
        }
        return data.getValue(id.getContent()).reference();
    }
}
