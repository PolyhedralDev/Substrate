package com.dfsek.substrate.lang.node.expression.function;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.compiler.value.PrimitiveValue;
import com.dfsek.substrate.lang.compiler.value.ShadowValue;
import com.dfsek.substrate.lang.compiler.value.ThisReferenceValue;
import com.dfsek.substrate.lang.node.expression.BlockNode;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.value.ValueReferenceNode;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.util.pair.Pair;

import java.util.*;
import java.util.function.Function;

public class LambdaExpressionNode extends ExpressionNode {
    private final ExpressionNode content;
    private final List<Pair<String, Signature>> types;
    private final Position start;
    private final Signature parameters;

    private final Function<BuildData, Signature> closure;
    private final List<Pair<String, Function<BuildData, Signature>>> closureTypes;

    private final Signature returnType;

    private String self;

    public LambdaExpressionNode(ExpressionNode content, List<Pair<String, Signature>> types, Position start, Signature returnType) {
        this.content = content;
        this.types = types;
        this.start = start;
        this.returnType = returnType;
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
                .filter(node -> !((ValueReferenceNode) node).isLambdaArgument())
                .filter(node -> !((ValueReferenceNode) node).isLocal())
                .map(node -> (ValueReferenceNode) node)
                .forEach(valueReferenceNode -> {
                    String id = valueReferenceNode.getId().getContent();
                    if (!closureIDs.contains(id) && !id.equals(self)) {
                        closureTypes.add(Pair.of(valueReferenceNode.getId().getContent(), data -> {
                            if (!valueReferenceNode.getId().getContent().equals(self)) {
                                return valueReferenceNode.reference(data);
                            }
                            return Signature.empty();
                        }));
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

        System.out.println("Return type " + returnType);
        System.out.println("args " + parameters);
    }

    public void setSelf(String self) {
        this.self = self;
    }


    @Override
    public void apply(MethodBuilder builder, BuildData data) throws ParseException {
        BuildData closureFinder = data.sub();

        System.out.println("CONTENT:");
        content.streamContents().forEach(System.out::println);

        for (Pair<String, Signature> type : types) {
            Signature signature = type.getRight();
            closureFinder.registerUnchecked(type.getLeft(), new PrimitiveValue(signature, closureFinder.getOffset()));
        }

        Signature closureSignature = closure.apply(closureFinder);

        String lambda = data.lambdaFactory().implement(parameters, reference(data).getSimpleReturn(), closureSignature, methodBuilder -> {
            BuildData delegate = data.sub(methodBuilder.classWriter());

            for (int i = 0; i < closureTypes.size(); i++) {
                Pair<String, Function<BuildData, Signature>> pair = closureTypes.get(i);
                delegate.registerUnchecked(pair.getLeft(), new ShadowValue(pair.getRight().apply(delegate), i));
            }
            for (Pair<String, Signature> argument : types) {
                delegate.registerUnchecked(argument.getLeft(), new PrimitiveValue(argument.getRight(), delegate.getOffset()));
                delegate.offsetInc(argument.getRight().frames());
            }

            if (self != null) {
                delegate.registerUnchecked(self, new ThisReferenceValue(reference(data)));
            }

            System.out.println("CONTENT REF: " + content.reference(delegate));
            ParserUtil.checkReferenceType(content, delegate, returnType).simplify().apply(methodBuilder, delegate);
            if (!(content instanceof BlockNode)) {
                if (returnType.isSimple()) {
                    methodBuilder.insn(returnType.getType(0).returnInsn());
                } else if (returnType.size() > 1) {
                    methodBuilder.refReturn();
                }
            }
        }).getName();

        builder.newInsn(lambda)
                .dup();

        for (Pair<String, Function<BuildData, Signature>> pair : closureTypes) {
            if (pair.getLeft().equals(self)) continue; // dont load self into closure.
            data.getValue(pair.getLeft()).load(builder, data);
        }

        builder.invokeSpecial(CompilerUtil.internalName(lambda),
                "<init>",
                "(" + closureSignature.internalDescriptor() + ")V");
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
        return Signature.fun().applyGenericReturn(0, returnType).applyGenericArgument(0, getParameters());
    }

    @Override
    public Collection<? extends Node> contents() {
        return Collections.singleton(content);
    }
}
