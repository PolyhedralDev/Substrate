package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.EphemeralValue;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.DataType;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.util.ReflectionUtil;
import com.dfsek.substrate.util.pair.ImmutablePair;
import org.objectweb.asm.MethodVisitor;

import java.util.List;

public class LambdaExpressionNode extends ExpressionNode {
    private final ExpressionNode content;
    private final List<ImmutablePair<String, DataType>> types;

    private final Signature parameters;
    private final Position start;

    public LambdaExpressionNode(ExpressionNode content, List<ImmutablePair<String, DataType>> types, Position start) {
        this.content = content;
        this.types = types;
        this.start = start;
        Signature signature = Signature.empty();

        for (ImmutablePair<String, DataType> type : types) {
            signature = signature.and(new Signature(type.getRight()));
        }
        this.parameters = signature;
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        Class<?> lambda = data.lambdaFactory().implement(parameters, content.returnType(data), (method, clazz) -> {
            BuildData delegate = data.detach((a, b) -> {});

            types.forEach(pair -> {
                Signature signature = new Signature(pair.getRight());
                delegate.registerValue(pair.getLeft(), new EphemeralValue(signature), signature.frames());
            });

            content.apply(method, delegate);
            method.visitInsn(RETURN);
        });

        visitor.visitTypeInsn(NEW, ReflectionUtil.internalName(lambda));
        visitor.visitInsn(DUP);
        visitor.visitMethodInsn(INVOKESPECIAL,
                ReflectionUtil.internalName(lambda),
                "<init>",
                "()V",
                false);
    }
    public Signature getParameters() {
        return parameters;
    }

    @Override
    public Position getPosition() {
        return start;
    }

    @Override
    public Signature returnType(BuildData data) {
        return content.returnType(data);
    }
}
