package com.dfsek.substrate.lang.compiler.value;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import org.objectweb.asm.MethodVisitor;

import java.util.List;

public interface Function extends Value {
    Signature arguments();

    default boolean argsMatch(Signature attempt) {
        return arguments().equals(attempt);
    }

    default void preArgsPrep(MethodVisitor visitor, BuildData data) {
    }

    void invoke(MethodVisitor visitor, BuildData data, Signature args, List<ExpressionNode> argExpressions);
}
