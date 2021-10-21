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
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
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
        List<Signature> extra = new ArrayList<>();

        BuildData delegate = data.detach((id, buildData) -> {
            if(data.valueExists(id) && !data.getValue(id).ephemeral() && !buildData.hasOffset(id)) {
                System.out.println("Registering external value: " + id);
                Signature sig = data.getValue(id).returnType();
                System.out.println(sig);
                extra.add(sig);
                buildData.shadowValue(id, new EphemeralValue(sig), sig.frames());
                System.out.println(buildData.getValue(id));
            }
        });

        content.apply(new MethodVisitor(Opcodes.ASM5) {}, delegate); // dummy for creating values to pass.


        Signature merged = parameters;

        for (Signature signature : extra) {
            merged = merged.and(signature);
        }

        System.out.println(extra);
        System.out.println("Merged signature: " + merged);


        Class<?> lambda = data.lambdaFactory().implement(merged, content.returnType(data), (method, clazz) -> {
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
