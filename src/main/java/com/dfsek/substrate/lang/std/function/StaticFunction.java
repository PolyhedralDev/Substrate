package com.dfsek.substrate.lang.std.function;

import com.dfsek.substrate.lang.compiler.api.Function;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import io.vavr.collection.List;
import io.vavr.control.Either;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

public class StaticFunction implements Function {
    private final String owner, name;
    private final Signature args;
    private final Signature ret;

    public StaticFunction(Method method) {
        if (!Modifier.isStatic(method.getModifiers())) {
            throw new IllegalArgumentException("Method must be static: " + method);
        }
        this.owner = CompilerUtil.internalName(method.getDeclaringClass().getCanonicalName());
        this.name = method.getName();
        Type r = method.getGenericReturnType();

        this.ret = Signature.fromType(r);

        Signature args = Signature.empty();

        for (Type parameterType : method.getGenericParameterTypes()) {
            args = args.and(Signature.fromType(parameterType));
        }
        this.args = args;
    }

    @Override
    public Signature arguments() {
        return args;
    }

    @Override
    public List<Either<CompileError, Op>> invoke(BuildData data, Signature args) {
        String r = ret.equals(Signature.empty()) ? "V" : ret.internalDescriptor();
        return List.of(Op.invokeStatic(owner, name, "(" + args.internalDescriptor() + ")" + r));
    }

    @Override
    public Signature reference() {
        return Signature.fun().applyGenericArgument(0, args).applyGenericReturn(0, ret);
    }
}
