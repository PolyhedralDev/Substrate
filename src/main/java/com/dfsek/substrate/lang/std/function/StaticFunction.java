package com.dfsek.substrate.lang.std.function;

import com.dfsek.substrate.lang.compiler.api.Function;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class StaticFunction implements Function {
    private final String owner, name;
    private final Signature args;
    private final Signature ret;

    public StaticFunction(Method method) {
        if(!Modifier.isStatic(method.getModifiers())) {
            throw new IllegalArgumentException("Method must be static: " + method);
        }
        this.owner = CompilerUtil.internalName(method.getDeclaringClass().getCanonicalName());
        this.name = method.getName();
        Class<?> r = method.getReturnType();

        if(r.equals(void.class)) {
            this.ret = Signature.empty();
        } else if(r.equals(String.class)) {
            this.ret = Signature.string();
        } else if(r.equals(int.class)) {
            this.ret = Signature.integer();
        } else if(r.equals(double.class)) {
            this.ret = Signature.decimal();
        } else if(r.equals(boolean.class)) {
            this.ret = Signature.bool();
        } else {
            throw new IllegalArgumentException("Illegal return type: " + method);
        }

        Signature args = Signature.empty();

        for (Class<?> parameterType : method.getParameterTypes()) {
            if(parameterType.equals(String.class)) {
                args = args.and(Signature.string());
            } else if(parameterType.equals(int.class)) {
                args = args.and(Signature.integer());
            } else if(parameterType.equals(double.class)) {
                args = args.and(Signature.decimal());
            } else if(parameterType.equals(boolean.class)) {
                args = args.and(Signature.bool());
            } else {
                throw new IllegalArgumentException("Illegal parameter type: " + method);
            }
        }
        this.args = args;
    }

    @Override
    public Signature arguments() {
        return args;
    }

    @Override
    public void invoke(MethodBuilder visitor, BuildData data, Signature args) {
        String r = ret.equals(Signature.empty()) ? "V" : ret.internalDescriptor();
        visitor.invokeStatic(owner, name, "(" + args.internalDescriptor() + ")" + r);
    }

    @Override
    public Signature reference() {
        return Signature.fun().applyGenericArgument(0, args).applyGenericReturn(0, ret);
    }
}
