package com.dfsek.substrate.lang.node;

import com.dfsek.substrate.lang.compiler.BuildData;
import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import org.objectweb.asm.MethodVisitor;

import java.util.List;

public class BlockNode implements Node {
    private final List<Node> contents;
    private final Position position;

    public BlockNode(List<Node> contents, Position position) {
        this.contents = contents;
        this.position = position;
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
    public String toString() {
        StringBuilder builder = new StringBuilder("{\n");
        contents.forEach(node -> builder.append(node).append('\n'));
        return builder.append('}').toString();
    }
}
