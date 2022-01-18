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
    private final Runnable onInvoke;
    private boolean invoked = false;

    public FunctionValue(Function function, BuildData data, String implementationClassName, String delegate, String name, Runnable onInvoke) {
        this.function = function;
        this.data = data;
        this.implementationClassName = implementationClassName;
        this.delegate = delegate;
        this.name = name;
        this.onInvoke = onInvoke;
    }

    @Override
    public Signature reference() {
        return function.reference(data);
    }

    @Override
    public void load(MethodBuilder visitor, BuildData data) {
        if(!invoked) {
            invoked = true;
            onInvoke.run();
        }
        visitor.getStatic(implementationClassName,
                name,
                "L" + delegate + ";");
    }
}
