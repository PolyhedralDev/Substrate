package com.dfsek.substrate.lang.node.expression.function;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.value.ValueReferenceNode;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.Tuple2;
import io.vavr.collection.*;
import io.vavr.control.Either;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LambdaExpressionNode extends ExpressionNode {
    private final ExpressionNode content;
    private final Set<String> closureIDs;
    private final Position start;
    private final Signature parameters;

    private final Signature returnType;

    private final Signature ref;
    private final Set<String> argRefs;
    private final List<Tuple2<String, Signature>> types;
    private String self;

    private LambdaExpressionNode(Unchecked<? extends ExpressionNode> content, List<Tuple2<String, Signature>> types, Position start, Signature returnType, Set<String> argRefs) {
        this.content = content.get(returnType);
        this.start = start;
        this.returnType = returnType;
        this.argRefs = argRefs;
        this.types = types;

        Tuple2<HashSet<String>, Signature> parameterData = types
                .foldLeft(
                        new Tuple2<>(HashSet.empty(),
                                Signature.empty()), (t, type) -> new Tuple2<>(t._1.add(type._1), t._2.and(type._2))
                );
        this.closureIDs = parameterData._1;
        this.parameters = parameterData._2;

        this.ref = Signature.fun().applyGenericReturn(0, returnType).applyGenericArgument(0, parameters);
    }

    public static Unchecked<LambdaExpressionNode> of(Unchecked<? extends ExpressionNode> content, List<Tuple2<String, Signature>> types, Position start, Signature returnType, Set<String> argRefs) {
        return Unchecked.of(new LambdaExpressionNode(content, types, start, returnType, argRefs));
    }


    public void setSelf(String self) {
        this.self = self;
    }

    @Override
    public List<Either<CompileError, Op>> apply(BuildData data, LinkedHashMap<String, Value> valueMap) throws ParseException {
        Stream<Tuple2<String, Signature>> closureTypes = content
                .streamContents()
                .filter(ValueReferenceNode.class::isInstance)
                .filter(node -> !((ValueReferenceNode) node).isLocal())
                .map(ValueReferenceNode.class::cast)
                .flatMap(valueReferenceNode -> {
                    String id = valueReferenceNode.getId().getContent();
                    boolean isArg = argRefs.contains(id);
                    if (!isArg && !valueReferenceNode.isLambdaArgument()) {
                        if (!closureIDs.contains(id) && !id.equals(self)) {
                            closureIDs.add(id);
                            return List.of(new Tuple2<>(valueReferenceNode.getId().getContent(),
                                    valueReferenceNode.getId().getContent().equals(self) ? Signature.empty() : valueReferenceNode.reference())
                            );
                        }
                    }
                    return List.empty();
                });

        Signature closure = closureTypes.foldRight(Signature.empty(), (pair, signature) -> pair._2.and(signature));
        return data
                .lambdaFactory()
                .implement(parameters, reference().getSimpleReturn(), closure, clazz ->
                        content
                                .simplify()
                                .apply(data, valueMap)
                                .append(returnType.retInsn()
                                        .mapLeft(m -> Op.errorUnwrapped(m, getPosition()))
                                        .map(Op::insnUnwrapped)))
                .apply((errors, clazz) -> errors
                        .map((Function<CompileError, Either<CompileError, Op>>) Either::left)
                        .append(Op.newInsn(clazz.getName()))
                        .append(Op.dup())
                        .appendAll(closureTypes
                                .toStream()
                                .flatMap(pair -> {
                                    if (pair._1.equals(self))
                                        return List.empty(); // dont load self into closure.
                                    return Op.getValue(valueMap, data, pair._1, getPosition());
                                })
                                .collect(Collectors.toList()))
                        .append(Op.invokeSpecial(CompilerUtil.internalName(clazz.getName()),
                                "<init>",
                                "(" + closure.internalDescriptor() + ")V"))
                );
    }

    @Override
    public Position getPosition() {
        return start;
    }

    @Override
    public Signature reference() {
        return ref;
    }

    @Override
    public Collection<? extends Node> contents() {
        return Collections.singleton(content);
    }

    @Override
    public String toString() {
        StringBuilder lambda = new StringBuilder("(");

        types.forEach(t -> lambda.append(t._1).append(": ").append(t._2.toString().toLowerCase()));
        lambda.append("): ").append(returnType.toString().toLowerCase()).append(" -> ").append(content.toString());

        return lambda.toString();
    }
}
