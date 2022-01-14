package com.dfsek.substrate.lang.compiler.value;

import com.dfsek.substrate.lang.compiler.api.Function;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;

public class FunctionValue implements Value {
    private final Function function;
    private final BuildData data;
    private final String implementationClassName;
    private final int finalI;
    private final Class<?> delegate;

    public FunctionValue(Function function, BuildData data, String implementationClassName, int finalI, Class<?> delegate) {
        this.function = function;
        this.data = data;
        this.implementationClassName = implementationClassName;
        this.finalI = finalI;
        this.delegate = delegate;
    }

    @Override
    public Signature reference() {
        return function.reference(data);
    }

    @Override
    public void load(MethodBuilder visitor, BuildData data) {
        visitor.getStatic(implementationClassName,
                "fun" + finalI,
                "L" + CompilerUtil.internalName(delegate) + ";");
    }
}
