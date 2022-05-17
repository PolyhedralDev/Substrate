package com.dfsek.substrate.lang.std.function;

import com.dfsek.substrate.lang.compiler.api.Macro;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.Classes;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import io.vavr.collection.List;
import io.vavr.control.Either;

public class Bind implements Macro {
    @Override
    public Signature arguments() {
        return Signature.any()
                .and(Signature.fun()
                        .applyGenericArgument(0, Signature.any())
                        .applyGenericReturn(0, Signature.io())
                );
    }

    @Override
    public boolean argsMatch(Signature attempt) {
        return attempt.weakEquals(arguments());
    }

    @Override
    public Signature reference(Signature arguments) {
        return Signature.fun()
                .applyGenericReturn(0,
                        Signature.io()
                                .applyGenericReturn(0,
                                        arguments
                                                .getGenericReturn(1)
                                                .getGenericReturn(0))
                )
                .applyGenericArgument(0, arguments);
    }

    @Override
    public List<Either<CompileError, Op>> invoke(BuildData data, Signature args, List<ExpressionNode> argNodes) {
        return argNodes.flatMap(arg -> arg.simplify().apply(data))
                .append(Op.invokeStaticInterface(Classes.IO, "bind", "(L" + Classes.IO + ";L" + Classes.FUNCTION + ";)L" + Classes.IO + ";"));
    }
}
