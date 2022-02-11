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

public abstract class ExpressionNode extends NodeHolder implements Typed {
    public void applyReferential(MethodBuilder visitor, BuildData data) {
        simplify().apply(visitor, data);
    }

    @Override
    public ExpressionNode simplify() {
        return this;
    }
}
