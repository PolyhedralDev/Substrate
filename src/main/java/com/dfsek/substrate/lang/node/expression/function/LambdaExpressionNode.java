package com.dfsek.substrate.lang.node.expression.function;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.compiler.value.*;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.ValueReferenceNode;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.util.pair.Pair;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.*;
import java.util.stream.Collectors;

public class LambdaExpressionNode extends ExpressionNode {
    private final ExpressionNode content;
    private final List<Pair<String, Signature>> types;
    private final Position start;
    private final Signature parameters;

    private String self;

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

    public void setSelf(String self) {
        this.self = self;
    }

    @Override
    public void apply(MethodBuilder builder, BuildData data) throws ParseException {
        List<Pair<Signature, String>> closureValues = new ArrayList<>();
        Set<String> closureIDs = new HashSet<>();

        BuildData delegate = data.sub();

        Set<String> paramIDs = types.stream().map(Pair::getLeft).collect(Collectors.toSet());

        content
                .streamContents()
                .filter(node -> node instanceof ValueReferenceNode)
                .map(node -> ((ValueReferenceNode) node).getId().getContent())
                .forEach(id -> {
                    if(!closureIDs.contains(id) && !paramIDs.contains(id)) {
                        closureValues.add(Pair.of(reference(delegate), id));
                        closureIDs.add(id);
                    }
                });

        int param = 1;
        for (Pair<String, Signature> type : types) {
            Signature signature = type.getRight();
            delegate.registerValue(type.getLeft(), new PrimitiveValue(signature, param++), signature.frames());
        }


        Signature merged = Signature.empty();


        for (int i = 0; i < closureValues.size(); i++) {
            Pair<Signature, String> pair = closureValues.get(i);
            merged = merged.and(pair.getLeft());

            if(!paramIDs.contains(pair.getRight())) {
                System.out.println("attempt:" + self + "," + pair.getRight() + "," + pair.getLeft());
                delegate.registerUnchecked(pair.getRight(), new ShadowValue(pair.getLeft(), i));
            }
        }
        if (self != null) {
            delegate.registerUnchecked(self, new ThisReferenceValue(reference(data)));
        }

        Class<?> lambda = data.lambdaFactory().implement(parameters, content.reference(delegate).expandTuple(), merged, (method) -> {
            content.apply(method, delegate);
            method.voidReturn();
        }, (localVariable, method) -> {

        });

        builder.newInsn(CompilerUtil.internalName(lambda))
                .dup();

        for (Pair<Signature, String> pair : closureValues) {
            String internalParameter = pair.getRight();
            Value internal = data.getValue(internalParameter);
            builder.varInsn(internal.reference().getType(0).loadInsn(), data.offset(internalParameter));
        }

        builder.invokeSpecial(CompilerUtil.internalName(lambda),
                "<init>",
                "(" + merged.internalDescriptor() + ")V");
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
        BuildData data1 = data.sub();

        types.forEach(pair -> {
            Signature signature = pair.getRight();
            data1.registerUnchecked(pair.getLeft(), new PrimitiveValue(signature, data1.getOffset()));
        });

        return Signature.fun().applyGenericReturn(0, content.reference(data1)).applyGenericArgument(0, getParameters());
    }

    @Override
    public Collection<? extends Node> contents() {
        return Collections.singleton(content);
    }
}
