package com.dfsek.substrate.lang.std.function;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.value.Function;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.exception.ParseException;
import org.objectweb.asm.MethodVisitor;

public class ForEach implements Function {
    @Override
    public Signature arguments() {
        return Signature.list()
                .and(Signature.fun()
                        .applyGenericReturn(0, Signature.empty()));
    }

    @Override
    public boolean argsMatch(Signature attempt) {
        return arguments().weakEquals(attempt) &&
                attempt.getGenericReturn(0).equals(attempt.getGenericArguments(1));
    }

    @Override
    public void invoke(MethodVisitor visitor, BuildData data, Signature args) {
        
    }

    @Override
    public Signature returnType() {
        return Signature.empty();
    }

    @Override
    public Signature reference() {
        return Signature.fun().applyGenericReturn(0, returnType()).applyGenericArgument(0, arguments());
    }
}
