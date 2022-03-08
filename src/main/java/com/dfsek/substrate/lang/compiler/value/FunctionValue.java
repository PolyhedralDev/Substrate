package com.dfsek.substrate.lang.compiler.value;

import com.dfsek.substrate.lang.compiler.api.Function;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.util.Lazy;
import io.vavr.collection.List;
import io.vavr.control.Either;

import java.util.function.Supplier;

public class FunctionValue implements Value {
    private final Function function;
    private final String implementationClassName;
    private final String fieldName;
    private final Lazy<String> name;

    public FunctionValue(Function function, String implementationClassName, String fieldName, Supplier<String> onInvoke) {
        this.function = function;
        this.implementationClassName = implementationClassName;
        this.fieldName = fieldName;
        this.name = Lazy.of(onInvoke);
    }

    @Override
    public Signature reference() {
        return function.reference();
    }

    @Override
    public List<Either<CompileError, Op>> load(BuildData data) {
        return List.of(Op.getStatic(implementationClassName,
                fieldName,
                "L" + name.get() + ";"));
    }
}
