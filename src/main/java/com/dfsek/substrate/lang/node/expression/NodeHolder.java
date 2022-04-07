package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.util.Lazy;
import io.vavr.collection.Stream;


public abstract class NodeHolder implements Node {
    private final Lazy<Iterable<? extends Node>> cachedContents = Lazy.of(this::contents);

    protected abstract Iterable<? extends Node> contents();

    public Stream<? extends Node> streamContents() {
        return streamContents(this);
    }

    private Stream<? extends Node> streamContents(Node start) {
        if (start instanceof NodeHolder) {
            return Stream.concat(Stream.of(start), Stream.ofAll(((NodeHolder) start)
                            .cachedContents.get())
                    .flatMap(node -> Stream.concat(Stream.of(node), streamContents(node))));
        } else {
            return Stream.of(start);
        }
    }
}
