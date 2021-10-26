package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import org.objectweb.asm.MethodVisitor;

import java.util.List;

public class ArrayNode extends ExpressionNode {
    private final List<ExpressionNode> args;
    private final Position position;

    public ArrayNode(List<ExpressionNode> args, Position position) {
        this.args = args;
        this.position = position;
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        if(args.stream().map(node -> node.returnType(data)).distinct().count() > 1) {
            throw new ParseException("", position);
        }
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public Signature returnType(BuildData data) {
        return null;
    }
}
