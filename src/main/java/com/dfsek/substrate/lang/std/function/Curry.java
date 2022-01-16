package com.dfsek.substrate.lang.std.function;

import com.dfsek.substrate.lang.compiler.api.Macro;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.function.LambdaExpressionNode;

public class Curry implements Macro {
    @Override
    public Signature arguments() {
        return Signature.fun();
    }

    @Override
    public boolean argsMatch(Signature attempt) {
        return arguments().weakEquals(attempt);
    }

    @Override
    public void invoke(MethodBuilder visitor, BuildData data, Signature args) {

    }

    @Override
    public Signature reference(Signature arguments, BuildData data) {
        if(!arguments.isSimple() || !arguments.weakEquals(Signature.fun())) {
            throw new IllegalArgumentException("Invalid signature");
        }
        Signature argumentFunctionArguments = arguments.getGenericArguments(0);
        Signature argumentFunctionReturn = arguments.getGenericReturn(0);

        Signature fun = null;

        for (int i = argumentFunctionArguments.size() - 1; i >= 0; i--) {
            fun = Signature.fun()
                    .applyGenericArgument(0, argumentFunctionArguments.get(i))
                    .applyGenericReturn(0, fun == null ? argumentFunctionReturn : fun);
        }

        System.out.println(arguments);
        System.out.println(fun);

        return null;
    }
}
