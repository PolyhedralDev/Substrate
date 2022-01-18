package com.dfsek.substrate.lang.compiler.value;

import com.dfsek.substrate.lang.compiler.api.Function;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;

public class FunctionValue implements Value {
    private final Function function;
    private final BuildData data;
    private final String implementationClassName;
    private final String delegate;
    private final String name;

    public FunctionValue(Function function, BuildData data, String implementationClassName, String delegate, String name) {
        this.function = function;
        this.data = data;
        this.implementationClassName = implementationClassName;
        this.delegate = delegate;
        this.name = name;
    }

    @Override
    public Signature reference() {
        return function.reference(data);
    }

    @Override
    public void load(MethodBuilder visitor, BuildData data) {
        visitor.getStatic(implementationClassName,
                name,
                "L" + delegate + ";");
    }
}
