package com.dfsek.substrate.lang.node.expression.cast;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.tokenizer.Token;
import org.objectweb.asm.MethodVisitor;

public abstract class TypeCastNode extends ExpressionNode {
    protected final Token type;
    protected final ExpressionNode value;

    public TypeCastNode(Token type, ExpressionNode value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        value.apply(visitor, data);
        applyCast(visitor, data);
    }

    public abstract void applyCast(MethodVisitor visitor, BuildData data);


    @Override
    public Position getPosition() {
        return type.getPosition();
    }
}
