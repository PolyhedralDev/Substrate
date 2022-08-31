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
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import io.vavr.control.Either;

public class FunctionInvocationNode extends ExpressionNode {
    private final List<ExpressionNode> arguments;
    private final ExpressionNode function;

    private FunctionInvocationNode(ExpressionNode function, List<ExpressionNode> arguments) {
        this.arguments = arguments;
        this.function = function;
    }

    public static Unchecked<FunctionInvocationNode> of(Unchecked<? extends ExpressionNode> function, List<Unchecked<? extends ExpressionNode>> arguments) {
        ExpressionNode checkedFunction = function.weak(Signature.fun());
        return Unchecked.of(new FunctionInvocationNode(checkedFunction, arguments.map(Unchecked::unchecked)));
    }

    @Override
    public List<Either<CompileError, Op>> apply(BuildData data, LinkedHashMap<String, Value> values) throws ParseException {
        Signature argSignature = CompilerUtil.expandArguments(arguments);

        if (!function.reference().getGenericArguments(0).equals(argSignature)) {
            return List.of(Op.error("Function argument mismatch, expected " + function.reference().getGenericArguments(0) + ", got " + argSignature, function.getPosition()));
        }

        return function.apply(data, values)
                .append(Op.aLoad(data.getImplArgsOffset()))
                .appendAll(arguments
                        .flatMap(arg -> arg.simplify().apply(data, values)
                                .appendAll(CompilerUtil.deconstructTuple(arg, data))))
                .append(data.lambdaFactory().invoke(argSignature, reference(), data));

    }

    @Override
    public Position getPosition() {
        return function.getPosition();
    }

    @Override
    public Signature reference() {
        return function.reference().getSimpleReturn();
    }

    @Override
    public Iterable<? extends Node> contents() {
        return arguments.append(function);
    }
}
