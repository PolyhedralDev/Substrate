package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.node.expression.function.LambdaExpressionNode;
import com.dfsek.substrate.util.Lazy;

import java.util.Collection;
import java.util.stream.Stream;

public abstract class NodeHolder implements Node {
    protected abstract Collection<? extends Node> contents();

    private final Lazy<Collection<? extends Node>> cachedContents = Lazy.of(this::contents);

    public Stream<? extends Node> streamContents() {
        return streamContents(this);
    }

    private Stream<? extends Node> streamContents(Node start) {
        if (start instanceof NodeHolder) {
            return ((NodeHolder) start)
                    .cachedContents.get()
                    .stream()
                    .flatMap(node -> Stream.concat(Stream.of(node), streamContents(node)));
        } else {
            return Stream.empty();
        }
    }
}
