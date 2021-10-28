package com.dfsek.substrate.lang.std.function;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.value.Function;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.exception.ParseException;
import org.objectweb.asm.MethodVisitor;

public class ForEach implements Function {
    private final Signature listGeneric;
    private final ExpressionNode consumer;

    public ForEach(Signature listGeneric, ExpressionNode consumer) {
        this.listGeneric = listGeneric;
        this.consumer = consumer;
    }

    @Override
    public Signature arguments() {
        return Signature.list()
                .applyGenericReturn(0, listGeneric)
                .and(Signature.fun()
                        .applyGenericArgument(0, listGeneric)
                        .applyGenericReturn(0, Signature.empty()));
    }

    @Override
    public void invoke(MethodVisitor visitor, BuildData data, Signature args) {
        Signature node = consumer.referenceType(data);

        if (!Signature.fun().applyGenericReturn(0, Signature.empty()).applyGenericArgument(0, listGeneric).equals(node)) {
            throw new ParseException("Invalid signature for consumer, expected " + listGeneric + ", got " + node, consumer.getPosition());
        }


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
