package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;

import java.util.Collection;
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
    public void apply(MethodBuilder builder, BuildData data) throws ParseException {
        BuildData scope = data.sub();
        contents.forEach(node -> node.apply(builder, scope));
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public Signature reference(BuildData data) {
        if (returnType.isEmpty()) return Signature.empty();
        Signature test = returnType.get(0).reference(data);
        returnType.forEach(type -> ParserUtil.checkReferenceType(type, data, test));
        return test;
    }

    @Override
    public Collection<Node> contents() {
        return contents;
    }
}
