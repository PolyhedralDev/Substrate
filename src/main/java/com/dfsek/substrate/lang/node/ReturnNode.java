package com.dfsek.substrate.lang.node;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import org.objectweb.asm.MethodVisitor;

public class ReturnNode extends ExpressionNode {
    private final Position position;

    private final ExpressionNode value;

    public ReturnNode(Position position, ExpressionNode value) {
        this.position = position;
        this.value = value;
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        if (value == null) visitor.visitInsn(RETURN);
        else {
            value.apply(visitor, data);
            Signature ret = value.reference(data);
            if (ret.size() == 1) visitor.visitInsn(ret.getType(0).returnInsn());
            else visitor.visitInsn(ARETURN);
        }
    }

    @Override
    public Position getPosition() {
        return position;
    }

    public Signature reference(BuildData data) {
        if (value == null) return Signature.empty();
        return value.reference(data);
    }
}
