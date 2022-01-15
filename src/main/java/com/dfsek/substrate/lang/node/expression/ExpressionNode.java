package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Typed;
import com.dfsek.substrate.util.Lazy;
import org.objectweb.asm.MethodVisitor;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class ExpressionNode implements Node, Typed {
    public void applyReferential(MethodBuilder visitor, BuildData data) {
        apply(visitor, data);
    }

    public abstract Collection<? extends Node> contents();

    private final Lazy<Collection<? extends Node>> cachedContents = Lazy.of(this::contents);

    public Stream<? extends Node> streamContents() {
        return streamContents(this);
    }

    private Stream<? extends Node> streamContents(Node start) {
        if (start instanceof ExpressionNode) {
            return ((ExpressionNode) start)
                    .cachedContents.get()
                    .stream()
                    .flatMap(node -> Stream.concat(Stream.of(node), streamContents(node)));
        } else {
            return Stream.empty();
        }
    }
}
