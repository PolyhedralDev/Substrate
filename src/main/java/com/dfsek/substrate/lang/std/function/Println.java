package com.dfsek.substrate.lang.std.function;

import com.dfsek.substrate.lang.compiler.api.Function;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import io.vavr.collection.List;
import io.vavr.control.Either;

public class Println implements Function {
    @Override
    public Signature arguments() {
        return Signature.string();
    }

    @Override
    public List<Either<CompileError, Op>> prepare() {
        return List.of(Op.getStatic("java/lang/System", "out", "Ljava/io/PrintStream;"));
    }

    @Override
    public List<Either<CompileError, Op>> invoke(BuildData data, Signature args) {
        return List.of(Op.invokeVirtual("java/io/PrintStream", "println", "(Ljava/lang/String;)V"));
    }

    @Override
    public Signature reference() {
        return Signature.fun().applyGenericArgument(0, Signature.string());
    }
}
