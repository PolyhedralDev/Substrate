package com.dfsek.substrate.lang.node.expression.function;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.List;
import io.vavr.control.Either;

public class FunctionInvocationNode extends ExpressionNode {
    private final List<ExpressionNode> arguments;
    private final ExpressionNode function;

    public FunctionInvocationNode(ExpressionNode function, List<ExpressionNode> arguments) {
        this.arguments = arguments;
        this.function = function;
    }

    @Override
    public List<Either<CompileError, Op>> apply(BuildData data) throws ParseException {
        ParserUtil.checkWeakReferenceType(function, Signature.fun());

        Signature argSignature = CompilerUtil.expandArguments(arguments);

        if (!function.reference().getGenericArguments(0).equals(argSignature)) {
            throw new ParseException("Function argument mismatch, expected " + function.reference().getGenericArguments(0) + ", got " + argSignature, function.getPosition());
        }

        return function.apply(data)
                .append(Op.aLoad(data.getImplArgsOffset()))
                .appendAll(arguments
                        .flatMap(arg -> arg.simplify().apply(data)
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
