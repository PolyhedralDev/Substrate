package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.DataType;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.util.pair.ImmutablePair;

import java.util.List;

public class LambdaExpressionNode extends ExpressionNode {
    private final ExpressionNode content;
    private final List<ImmutablePair<String, DataType>> types;
    private final Position start;

    public LambdaExpressionNode(ExpressionNode content, List<ImmutablePair<String, DataType>> types, Position start) {
        this.content = content;
        this.types = types;
        this.start = start;
    }

    @Override
    public Position getPosition() {
        return start;
    }

    @Override
    public Signature returnType(BuildData data) {
        return content.returnType(data);
    }
}
