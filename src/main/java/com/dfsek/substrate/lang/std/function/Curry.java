package com.dfsek.substrate.lang.std.function;

import com.dfsek.substrate.lang.compiler.api.Macro;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.BlockNode;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.ReturnNode;
import com.dfsek.substrate.lang.node.expression.function.FunctionInvocationNode;
import com.dfsek.substrate.lang.node.expression.function.LambdaExpressionNode;
import com.dfsek.substrate.lang.node.expression.value.ValueReferenceNode;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Curry implements Macro {
    @Override
    public Signature arguments() {
        return Signature.fun();
    }

    @Override
    public boolean argsMatch(Signature attempt) {
        return arguments().weakEquals(attempt);
    }

    @Override
    public void invoke(MethodBuilder visitor, BuildData data, Signature args, List<ExpressionNode> argNodes) {
        Signature functionArgs = args.getGenericArguments(0);
        generateClosure(0, functionArgs, reference(args).getSimpleReturn(), argNodes.get(0))
                .simplify().apply(visitor, data);
    }

    private ExpressionNode generateClosure(int captureTo, Signature args, Signature ret, ExpressionNode function) {

        return captureTo >= args.size()
                ? generateInvocation(args, function)
                : new LambdaExpressionNode(
                generateClosure(captureTo + 1, args, ret.getGenericReturn(0), function),
                Collections.singletonList(Pair.of(
                                "closure$" + captureTo,
                                args.get(captureTo)
                        )
                ), Position.getNull(), ret.getGenericReturn(0));
    }

    private ExpressionNode generateInvocation(Signature args, ExpressionNode function) {
        List<ExpressionNode> nodes = new ArrayList<>();

        for (int i = 0; i < args.size(); i++) {
            ValueReferenceNode node = new ValueReferenceNode("closure$" + i, args.get(i));
            node.setLambdaArgument(true);
            nodes.add(node);
        }

        ReturnNode returnNode = new ReturnNode(Position.getNull(), new FunctionInvocationNode(function, nodes));

        return new BlockNode(Collections.singletonList(returnNode), Collections.singletonList(returnNode), Position.getNull());
    }

    @Override
    public Signature reference(Signature arguments) {
        if (!arguments.isSimple() || !arguments.weakEquals(Signature.fun())) {
            throw new IllegalArgumentException("Invalid signature");
        }
        Signature argumentFunctionArguments = arguments.getGenericArguments(0);
        Signature argumentFunctionReturn = arguments.getGenericReturn(0);

        Signature fun = null;

        for (int i = argumentFunctionArguments.size() - 1; i >= 0; i--) {
            fun = Signature.fun()
                    .applyGenericArgument(0, argumentFunctionArguments.get(i))
                    .applyGenericReturn(0, fun == null ? argumentFunctionReturn : fun);
        }

        return Signature.fun().applyGenericArgument(0, arguments).applyGenericReturn(0, fun);
    }
}
