package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Typed;
import org.objectweb.asm.MethodVisitor;

import java.util.Collection;
import java.util.Collections;

public abstract class ExpressionNode implements Node, Typed {
    public void applyReferential(MethodBuilder visitor, BuildData data) {
        apply(visitor, data);
    }

    public abstract Collection<? extends Node> contents();
}
