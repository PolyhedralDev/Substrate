package com.dfsek.substrate.lang.std.function;

import com.dfsek.substrate.lang.compiler.api.Function;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;

public class Println implements Function {
    @Override
    public Signature arguments() {
        return Signature.string();
    }

    @Override
    public void prepare(MethodBuilder visitor) {
        visitor.getStatic("java/lang/System", "out", "Ljava/io/PrintStream;");
    }

    @Override
    public void invoke(MethodBuilder visitor, BuildData data, Signature args) {
        visitor.invokeVirtual("java/io/PrintStream", "println", "(Ljava/lang/String;)V");
    }

    @Override
    public Signature reference(BuildData data) {
        return Signature.fun().applyGenericArgument(0, Signature.string());
    }
}
