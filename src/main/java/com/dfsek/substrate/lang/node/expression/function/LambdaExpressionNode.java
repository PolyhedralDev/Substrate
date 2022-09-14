package com.dfsek.substrate.lang.node.expression.function;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.compiler.value.PrimitiveValue;
import com.dfsek.substrate.lang.compiler.value.ShadowValue;
import com.dfsek.substrate.lang.compiler.value.ThisReferenceValue;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.value.ValueReferenceNode;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.ParserScope;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.Tuple2;
import io.vavr.collection.*;
import io.vavr.control.Either;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.vavr.API.*;
import static io.vavr.Predicates.is;

public class LambdaExpressionNode extends ExpressionNode {
    private final ExpressionNode content;
    private final List<Tuple2<String, Signature>> types;
    private final Set<String> closureIDs;
    private final Position start;
    private final Signature parameters;

    private final Signature returnType;

    private final Signature ref;
    private final Set<String> argRefs;
    private String self;

    private LambdaExpressionNode(Unchecked<? extends ExpressionNode> content, List<Tuple2<String, Signature>> types, Position start, Signature returnType, Set<String> argRefs) {
        this.content = content.get(returnType);
        this.types = types;
        this.start = start;
        this.returnType = returnType;
        this.argRefs = argRefs;

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
    public List<Either<CompileError, Op>> apply(BuildData data, ParserScope scope) throws ParseException {
        Stream<Tuple2<String, Signature>> closureTypes = content
                .streamContents()
                .filter(node -> node instanceof ValueReferenceNode)
                .filter(node -> !((ValueReferenceNode) node).isLocal())
                .map(node -> (ValueReferenceNode) node)
                .flatMap(valueReferenceNode -> {
                    String id = valueReferenceNode.getId().getContent();
                    boolean isArg = argRefs.contains(id);
                    if (!isArg && !valueReferenceNode.isLambdaArgument() || !isArg && values.containsKey(id)) {
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
                                .apply(data, closureTypes
                                        .zipWithIndex()
                                        .map(closureMember -> new Tuple2<>(closureMember._1._1, (Value) new ShadowValue(closureMember._1._2, closureMember._2)))
                                        .appendAll(types.map(argument -> new Tuple2<>(argument._1, new PrimitiveValue(argument._2, argument._1, argument._2.frames()))))
                                        .toMap(Function.identity())
                                        .foldLeft(scope, (s, p) -> s.register(p._1, p._2))
                                        .merge(
                                                Match(self).of(
                                                        Case($(is(null)), HashMap.empty()),
                                                        Case($(), id -> HashMap.of(id, new ThisReferenceValue(reference())))
                                                )))
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
                                    return Op.getValue(values, data, pair._1, getPosition());
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
}
