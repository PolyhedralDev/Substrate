package com.dfsek.substrate.lang.node.expression.function;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.compiler.value.PrimitiveValue;
import com.dfsek.substrate.lang.compiler.value.ShadowValue;
import com.dfsek.substrate.lang.compiler.value.ThisReferenceValue;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.value.ValueReferenceNode;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.util.Pair;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Either;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LambdaExpressionNode extends ExpressionNode {
    private final ExpressionNode content;
    private final List<Pair<String, Signature>> types;
    private final Set<String> closureIDs;
    private final Position start;
    private final Signature parameters;

    private final Signature returnType;

    private final Signature ref;

    private String self;

    private final Set<String> argRefs = new HashSet<>();

    public LambdaExpressionNode(ExpressionNode content, List<Pair<String, Signature>> types, Position start, Signature returnType) {
        this.content = content;
        this.types = types;
        this.start = start;
        this.returnType = returnType;
        Signature signature = Signature.empty();

        this.closureIDs = new HashSet<>();
        for (Pair<String, Signature> type : types) {
            signature = signature.and(type.getRight());
            closureIDs.add(type.getLeft());
        }

        this.parameters = signature;

        this.ref = Signature.fun().applyGenericReturn(0, returnType).applyGenericArgument(0, parameters);
    }

    public void setSelf(String self) {
        this.self = self;
    }

    @Override
    public List<Either<CompileError, Op>> apply(BuildData data) throws ParseException {
        List<Tuple2<String, Signature>> closureTypes = content
                .streamContents()
                .filter(node -> node instanceof ValueReferenceNode)
                .filter(node -> !((ValueReferenceNode) node).isLocal())
                .map(node -> (ValueReferenceNode) node)
                .flatMap(valueReferenceNode -> {
                    String id = valueReferenceNode.getId().getContent();
                    boolean isArg = argRefs.contains(id);
                    if (!isArg && !valueReferenceNode.isLambdaArgument() || !isArg && data.valueExists(id)) {
                        if (!closureIDs.contains(id) && !id.equals(self)) {
                            closureIDs.add(id);
                            return List.of(new Tuple2<>(valueReferenceNode.getId().getContent(),
                                    valueReferenceNode.getId().getContent().equals(self) ? Signature.empty() : valueReferenceNode.reference())
                            );
                        }
                    }
                    return List.empty();
                })
                .toList();

        Signature closure = closureTypes.foldRight(Signature.empty(), (pair, signature) -> pair._2.and(signature));

        return data.lambdaFactory().implement(parameters, reference().getSimpleReturn(), closure, clazz -> {
                    BuildData delegate = data.sub(clazz);

                    closureTypes.zipWithIndex()
                            .map(closureMember -> new Tuple2<>(closureMember._1._1, (Value) new ShadowValue(closureMember._1._2, closureMember._2)))
                            .appendAll(types.map(argument -> new Tuple2<>(argument.getLeft(), new PrimitiveValue(argument.getRight(), delegate.offsetInc(argument.getRight().frames())))))
                            .forEach(v -> delegate.registerUnchecked(v._1, v._2));

                    if (self != null) {
                        delegate.registerUnchecked(self, new ThisReferenceValue(reference()));
                    }

                    return ParserUtil.checkReferenceType(content, returnType).simplify().apply(delegate)
                            .append(returnType.retInsn()
                                    .mapLeft(m -> Op.errorUnwrapped(m, getPosition()))
                                    .map(Op::insnUnwrapped));
                })
                .apply((errors, clazz) -> errors
                        .map((Function<CompileError, Either<CompileError, Op>>) Either::left)
                        .append(Op.newInsn(clazz.getName()))
                        .append(Op.dup())
                        .appendAll(closureTypes
                                .toStream()
                                .flatMap(pair -> {
                                    if (pair._1.equals(self))
                                        return List.empty(); // dont load self into closure.
                                    return data.getValue(pair._1).load(data);
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

    public void addArgumentReference(String node) {
        argRefs.add(node);
    }

    @Override
    public Signature reference() {
        return ref;
    }

    @Override
    public Collection<? extends Node> contents() {
        return Collections.singleton(content);
    }
}
