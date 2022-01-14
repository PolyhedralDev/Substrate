package com.dfsek.substrate.lang.node.expression.function;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;

import java.util.List;

public class FunctionInvocationNode extends ExpressionNode {
    private final List<ExpressionNode> arguments;
    private final ExpressionNode function;

    public FunctionInvocationNode(ExpressionNode function, List<ExpressionNode> arguments) {
        this.arguments = arguments;
        this.function = function;
    }

    @Override
    public void apply(MethodBuilder builder, BuildData data) throws ParseException {
        ParserUtil.checkWeakReferenceType(function, data, Signature.fun());

        Signature argSignature = CompilerUtil.expandArguments(data, arguments);

        if (!function.reference(data).getGenericArguments(0).equals(argSignature)) {
            throw new ParseException("Function argument mismatch, expected " + function.reference(data).getGenericArguments(0) + ", got " + argSignature, function.getPosition());
        }

        function.apply(builder, data);

        arguments.forEach(arg -> {
            arg.apply(builder, data);
            CompilerUtil.deconstructTuple(arg, data, builder);
        });


        Signature ref = reference(data);
        data.lambdaFactory().invoke(argSignature, ref.expandTuple(), data, builder);
    }

    @Override
    public Position getPosition() {
        return function.getPosition();
    }

    @Override
    public Signature reference(BuildData data) {
        return function.reference(data).getSimpleReturn();
    }
}
