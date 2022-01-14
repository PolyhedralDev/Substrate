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
import com.dfsek.substrate.util.Lazy;
import com.dfsek.substrate.util.pair.Pair;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LambdaExpressionNode extends ExpressionNode {
    private final ExpressionNode content;
    private final List<Pair<String, Signature>> types;
    private final Position start;
    private final Signature parameters;

    private final Function<BuildData, Signature> closure;
    private final List<Pair<String, Function<BuildData, Signature>>> closureTypes;

    private String self;

    public LambdaExpressionNode(ExpressionNode content, List<Pair<String, Signature>> types, Position start) {
        this.content = content;
        this.types = types;
        this.start = start;
        Signature signature = Signature.empty();

        Set<String> closureIDs = new HashSet<>();
        for (Pair<String, Signature> type : types) {
            signature = signature.and(type.getRight());
            closureIDs.add(type.getLeft());
        }

        this.parameters = signature;


        this.closureTypes = new ArrayList<>();


        content.streamContents()
                .filter(node -> node instanceof ValueReferenceNode)
                .map(node -> (ValueReferenceNode) node)
                .forEach(valueReferenceNode -> {
                    System.out.println("Contains value ref: " + valueReferenceNode.getId());
                    String id = valueReferenceNode.getId().getContent();
                    if(!closureIDs.contains(id)) {
                        closureTypes.add(Pair.of(valueReferenceNode.getId().getContent(), valueReferenceNode::reference));
                        closureIDs.add(id);
                    }
                });


        this.closure = data -> {
            Signature closure = Signature.empty();
            for (Pair<String, Function<BuildData, Signature>> type : closureTypes) {
                closure = closure.and(type.getRight().apply(data));
            }
            return closure;
        };
    }

    public void setSelf(String self) {
        this.self = self;
    }

    @Override
    public void apply(MethodBuilder builder, BuildData data) throws ParseException {
        BuildData closureFinder = data.sub();

        types.forEach(pair -> {
            Signature signature = pair.getRight();
            closureFinder.registerUnchecked(pair.getLeft(), new PrimitiveValue(signature, closureFinder.getOffset()));
        });

        Signature closureSignature = closure.apply(closureFinder);
        System.out.println("Closure argument signature:" + closureSignature);

        Class<?> lambda = data.lambdaFactory().implement(parameters, content.reference(data), closureSignature, methodBuilder -> {
            BuildData delegate = data.sub(methodBuilder.classWriter());
            for (int i = 0; i < closureTypes.size(); i++) {
                Pair<String, Function<BuildData, Signature>> pair = closureTypes.get(i);
                delegate.registerUnchecked(pair.getLeft(), new ShadowValue(pair.getRight().apply(delegate), i));
            }
            for (Pair<String, Signature> argument : types) {
                delegate.registerUnchecked(argument.getLeft(), new PrimitiveValue(argument.getRight(), delegate.getOffset()));
                delegate.offsetInc(argument.getRight().frames());
            }
            content.apply(methodBuilder, delegate);
        });

        builder.newInsn(CompilerUtil.internalName(lambda))
                .dup();

        for (Pair<String, Function<BuildData, Signature>> pair : closureTypes) {
            data.getValue(pair.getLeft()).load(builder, data);
        }

        builder.invokeSpecial(CompilerUtil.internalName(lambda), "<init>", "(" + closureSignature.internalDescriptor() + ")V");
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
