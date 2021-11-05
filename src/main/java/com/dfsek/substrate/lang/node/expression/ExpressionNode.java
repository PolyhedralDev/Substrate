package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import org.objectweb.asm.MethodVisitor;

public abstract class ExpressionNode implements Node {
    public abstract Signature reference(BuildData data);

    public void applyReferential(MethodVisitor visitor, BuildData data) {
        apply(visitor, data);
    }
}
