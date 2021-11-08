package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.parser.ParserUtil;
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
        ParserUtil.checkType(node, data, Signature.bool()).apply(visitor, data);
        CompilerUtil.invertBoolean(visitor);
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public Signature reference(BuildData data) {
        return Signature.bool();
    }
}