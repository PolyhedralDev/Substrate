package com.dfsek.substrate.lang.node.expression.function;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.compiler.value.*;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
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
        List<Pair<Signature, String>> internalParameters = new ArrayList<>();

        BuildData delegate = data.detach((id, buildData) -> {
            if (data.valueExists(id) && !(data.getValue(id) instanceof FunctionValue)) {
                Signature sig = data.getValue(id).reference();
                if (!internalParameters.contains(Pair.of(sig, id))) {
                    internalParameters.add(Pair.of(sig, id));
                }
            }
        }, d -> data.lambdaFactory().name(parameters, content.reference(d).expandTuple()), parameters.frames());

        types.forEach(pair -> {
            Signature signature = pair.getRight();

            delegate.registerValue(pair.getLeft(), new PrimitiveValue(signature, delegate.getOffset()), signature.frames());
        });


        content.apply(new MethodVisitor(Opcodes.ASM5) {
        }, delegate); // dummy for creating values to pass.

        Signature merged = Signature.empty();


        for (int i = 0; i < internalParameters.size(); i++) {
            Pair<Signature, String> pair = internalParameters.get(i);
            merged = merged.and(pair.getLeft());
            System.out.println("shadowing " + pair.getRight());
            delegate.registerUnchecked(pair.getRight(), new ShadowValue(pair.getLeft(), i));
        }

        Class<?> lambda = data.lambdaFactory().implement(parameters, content.reference(delegate).expandTuple(), merged, (method, clazz) -> {
            content.apply(method, delegate);
            method.visitInsn(RETURN);
        });

        visitor.visitTypeInsn(NEW, CompilerUtil.internalName(lambda));
        visitor.visitInsn(DUP);

        for (Pair<Signature, String> pair : internalParameters) {
            String internalParameter = pair.getRight();
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
    public Signature reference(BuildData data) {
        BuildData data1 = data.detach((id, buildData) -> {
        }, d -> data.lambdaFactory().name(parameters, content.reference(d).expandTuple()), parameters.frames());

        types.forEach(pair -> {
            Signature signature = pair.getRight();
            data1.registerValue(pair.getLeft(), new PrimitiveValue(signature, data1.getOffset()), signature.frames());
        });
        return Signature.fun().applyGenericReturn(0, content.reference(data1)).applyGenericArgument(0, getParameters());
    }
}
