package com.dfsek.substrate.lang.compiler.api;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import io.vavr.collection.List;
import io.vavr.control.Either;
import org.objectweb.asm.Opcodes;


/**
 * a function-like construct which emits implementation specific bytecode.
 */
public interface Macro extends Opcodes {
    Signature arguments();

    Signature reference(Signature arguments);

    default List<Either<CompileError, Op>> prepare() {
        return List.empty();
    }

    default boolean argsMatch(Signature attempt) {
        return arguments().equals(attempt);
    }

    List<Either<CompileError, Op>> invoke(BuildData data, Signature args, List<ExpressionNode> argNodes);
}
