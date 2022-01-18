package com.dfsek.substrate.lang.compiler.value;

import com.dfsek.substrate.lang.compiler.api.Function;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.util.Lazy;

import java.util.function.Supplier;

public class FunctionValue implements Value {
    private final Function function;
    private final BuildData data;
    private final String implementationClassName;
    private final String fieldName;
    private final Lazy<String> name;

    public FunctionValue(Function function, BuildData data, String implementationClassName, String fieldName, Supplier<String> onInvoke) {
        this.function = function;
        this.data = data;
        this.implementationClassName = implementationClassName;
        this.fieldName = fieldName;
        this.name = Lazy.of(onInvoke);
    }

    @Override
    public Signature reference() {
        return function.reference(data);
    }

    @Override
    public void load(MethodBuilder visitor, BuildData data) {
        visitor.getStatic(implementationClassName,
                fieldName,
                "L" + name.get() + ";");
    }
}
