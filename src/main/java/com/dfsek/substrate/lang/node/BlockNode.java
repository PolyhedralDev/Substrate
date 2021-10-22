package com.dfsek.substrate.lang.node;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import org.objectweb.asm.MethodVisitor;

import java.util.List;

public class BlockNode extends ExpressionNode {
    private final List<Node> contents;

    private final List<ReturnNode> returnType;
    private final Position position;

    public BlockNode(List<Node> contents, List<ReturnNode> returnType, Position position) {
        this.contents = contents;
        this.position = position;
        this.returnType = returnType;
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        BuildData scope = data.sub();
        contents.forEach(node -> node.apply(visitor, scope));
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public Signature returnType(BuildData data) {
        if(returnType.isEmpty()) return Signature.empty();
        Signature test = returnType.get(0).type(data);
        returnType.forEach(type -> {
            if(!test.equals(type.type(data))) {
                throw new ParseException("Mismatched return types in block: expected " + test + ", got " + type.type(data), type.getPosition());
            }
        });
        return test;
    }
}
