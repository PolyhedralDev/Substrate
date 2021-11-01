package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.compiler.EphemeralFunction;
import com.dfsek.substrate.lang.compiler.EphemeralValue;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.lang.internal.Lambda;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.util.Lazy;
import com.dfsek.substrate.util.pair.Pair;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;

public class LambdaExpressionNode extends ExpressionNode {
    private final ExpressionNode content;
    private final List<Pair<String, Signature>> types;
    private final Position start;
    private final List<String> internalParameters = new ArrayList<>();
    private final Signature parameters;

    public LambdaExpressionNode(ExpressionNode content, List<Pair<String, Signature>> types, Position start) {
        this.content = content;
        this.types = types;
        this.start = start;
        Signature signature = Signature.empty();

        for (Pair<String, Signature> type : types) {
            signature = signature.and(type.getRight());
        }
        this.parameters = signature;
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        List<Signature> extra = new ArrayList<>();

        BuildData delegate = data.detach((id, buildData) -> {
            if (data.valueExists(id) && !data.getValue(id).ephemeral() && !buildData.hasOffset(id)) {
                Signature sig = data.getValue(id).reference();
                if (!internalParameters.contains(id)) {
                    System.out.println("Shadowing " + id);
                    buildData.shadowValue(id, data.getValue(id));
                    System.out.println(buildData.isShadowed(id) + ": " + buildData);
                    internalParameters.add(id);
                    extra.add(sig);
                }
            }
        }, d  -> data.lambdaFactory().name(parameters, content.referenceType(d).getSimpleReturn()));

        types.forEach(pair -> {
            Signature signature = pair.getRight();
            if (pair.getRight().weakEquals(Signature.fun())) { // register the lambda value as a function
                delegate.registerValue(pair.getLeft(), new EphemeralFunction(pair.getRight(), delegate.getOffset()), signature.frames());
            } else {
                delegate.registerValue(pair.getLeft(), new EphemeralValue(signature), signature.frames());
            }
        });


        content.apply(new MethodVisitor(Opcodes.ASM5) {
        }, delegate); // dummy for creating values to pass.

        Signature merged = Signature.empty();

        for (Signature signature : extra) {
            merged = merged.and(signature);
        }

        Class<?> lambda = data.lambdaFactory().implement(parameters, content.referenceType(delegate).getSimpleReturn(), merged, (method, clazz) -> {
            content.apply(method, delegate);
            method.visitInsn(RETURN);
        });

        visitor.visitTypeInsn(NEW, CompilerUtil.internalName(lambda));
        visitor.visitInsn(DUP);

        for (String internalParameter : internalParameters) {
            Value internal = data.getValue(internalParameter);
            visitor.visitVarInsn(internal.reference().getType(0).loadInsn(), data.offset(internalParameter));
        }

        visitor.visitMethodInsn(INVOKESPECIAL,
                CompilerUtil.internalName(lambda),
                "<init>",
                "(" + merged.internalDescriptor() + ")V",
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
    public Signature referenceType(BuildData data) {
        BuildData data1 = data.detach((id, buildData) -> {
            if (data.valueExists(id) && !data.getValue(id).ephemeral() && !buildData.hasOffset(id)) {
                buildData.shadowValue(id, data.getValue(id));
            }
        }, d  -> data.lambdaFactory().name(parameters, content.referenceType(d).getSimpleReturn()));

        types.forEach(pair -> {
            Signature signature = pair.getRight();
            data1.registerValue(pair.getLeft(), new EphemeralValue(signature), signature.frames());
        });
        return Signature.fun().applyGenericReturn(0, content.referenceType(data1)).applyGenericArgument(0, getParameters());
    }
}
