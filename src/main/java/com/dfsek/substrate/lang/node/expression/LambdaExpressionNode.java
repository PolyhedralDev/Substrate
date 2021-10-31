package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.compiler.EphemeralValue;
import com.dfsek.substrate.lang.compiler.PrimitiveValue;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.lambda.LocalLambdaReferenceFunction;
import com.dfsek.substrate.lang.compiler.type.DataType;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
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
    private Signature parameters;

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
                extra.add(sig);
                buildData.shadowValue(id, data.getValue(id), sig.frames());
                if (!internalParameters.contains(id)) {
                    internalParameters.add(id);
                }
            }
        });

        types.forEach(pair -> {
            Signature signature = pair.getRight();
            System.out.println(signature);
            if (pair.getRight().weakEquals(Signature.fun())) { // register the lambda value as a function
                LambdaExpressionNode lambdaExpressionNode = (LambdaExpressionNode) data.getValue(pair.getLeft());
                delegate.registerValue(pair.getLeft(), new LocalLambdaReferenceFunction(lambdaExpressionNode.getParameters(), signature.getSimpleReturn(), pair.getLeft(), lambdaExpressionNode.internalParameters()), signature.frames());
            } else {
                delegate.registerValue(pair.getLeft(), new EphemeralValue(signature), signature.frames());
            }

        });


        content.apply(new MethodVisitor(Opcodes.ASM5) {
        }, delegate); // dummy for creating values to pass.


        Signature merged = parameters;

        for (Signature signature : extra) {
            merged = merged.and(signature);
        }

        this.parameters = merged;


        Class<?> lambda = data.lambdaFactory().implement(merged, content.referenceType(delegate).getSimpleReturn(), (method, clazz) -> {
            content.apply(method, delegate);
            method.visitInsn(RETURN);
        });

        visitor.visitTypeInsn(NEW, CompilerUtil.internalName(lambda));
        visitor.visitInsn(DUP);
        visitor.visitMethodInsn(INVOKESPECIAL,
                CompilerUtil.internalName(lambda),
                "<init>",
                "()V",
                false);
    }

    public Signature getParameters() {
        return parameters;
    }

    public List<String> internalParameters() {
        return internalParameters;
    }

    @Override
    public Position getPosition() {
        return start;
    }

    @Override
    public Signature referenceType(BuildData data) {
        BuildData data1 = data.detach((id, buildData) -> {
            if (data.valueExists(id) && !data.getValue(id).ephemeral() && !buildData.hasOffset(id)) {
                Signature sig = data.getValue(id).reference();
                buildData.shadowValue(id, data.getValue(id), sig.frames());
                if (!internalParameters.contains(id)) {
                    internalParameters.add(id);
                }
            }
        });

        types.forEach(pair -> {
            Signature signature = pair.getRight();
            data1.registerValue(pair.getLeft(), new EphemeralValue(signature), signature.frames());
        });
        return Signature.fun().applyGenericReturn(0, content.referenceType(data1)).applyGenericArgument(0, getParameters());
    }
}
