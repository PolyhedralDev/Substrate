package com.dfsek.substrate.lang.std.function;

import com.dfsek.substrate.lang.compiler.api.Macro;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;

public class Curry implements Macro {
    @Override
    public Signature arguments() {
        return Signature.fun();
    }

    @Override
    public void invoke(MethodBuilder visitor, BuildData data, Signature args) {

    }

    @Override
    public Signature reference(Signature arguments, BuildData data) {
        return null;
    }
}
