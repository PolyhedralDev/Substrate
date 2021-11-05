package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import org.objectweb.asm.MethodVisitor;

public class BooleanNotNode extends ExpressionNode{
    private final Position position;
    private final ExpressionNode node;

    public BooleanNotNode(Position position, ExpressionNode node) {
        this.position = position;
        this.node = node;
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        if(!node.referenceType(data).getSimpleReturn().equals(Signature.bool())) {
            throw new ParseException("Expected BOOL, got " + node.referenceType(data).getSimpleReturn(), node.getPosition());
        }

        node.apply(visitor, data);
        CompilerUtil.invertBoolean(visitor);
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public Signature referenceType(BuildData data) {
        return Signature.bool();
    }
}
