package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Typed;
import com.dfsek.substrate.util.pair.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class ExpressionNode extends NodeHolder implements Typed {
    private final List<Pair<ExpressionNode, Set<Signature>>> assertions = new ArrayList<>();

    @Override
    public ExpressionNode simplify() {
        return this;
    }

    public ExpressionNode assertType(ExpressionNode node, Signature... signatures) {
        assertions.add(Pair.of(node, Set.of(signatures)));
        return this;
    }
}
