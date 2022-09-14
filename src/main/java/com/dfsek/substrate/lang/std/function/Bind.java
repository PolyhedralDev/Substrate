package com.dfsek.substrate.lang.std.function;

import com.dfsek.substrate.lang.compiler.api.Macro;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.Classes;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import io.vavr.control.Either;

public class Bind implements Macro {
    @Override
    public Signature arguments() {
        return Signature.io()
                .and(Signature.fun()
                        .applyGenericArgument(0, Signature.any())
                        .applyGenericReturn(0, Signature.io())
                );
    }

    @Override
    public Signature getArgumentSignature(Signature attempt) {
        Signature param = attempt.get(0).getGenericReturn(0);
        return Signature.io().applyGenericReturn(0, param)
                .and(Signature.fun()
                        .applyGenericArgument(0, param)
                        .applyGenericReturn(0, attempt.get(1).getGenericReturn(0))
                );
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
    public List<Either<CompileError, Op>> invoke(BuildData data, Signature args, List<ExpressionNode> argNodes, LinkedHashMap<String, Value> values) {
        String clazz;
        if (args.get(1).getGenericArguments(0).size() == 0) {
            clazz = Classes.IO_FUNCTION_UNIT;
        } else {
            clazz =  switch (args.get(1).getGenericArguments(0).getType(0)) {
                case NUM -> Classes.IO_FUNCTION_NUM;
                case INT -> Classes.IO_FUNCTION_INT;
                default -> Classes.IO_FUNCTION;
            };
        }

        return List.of(Op.aLoad(1))
                .appendAll(argNodes.flatMap(arg -> arg.simplify().apply(data, , values)))
                .append(Op.invokeStaticInterface(Classes.IO, "bind", "(L" + Classes.ENVIRONMENT + ";L" + Classes.IO + ";L" + clazz + ";)L" + Classes.IO + ";"));
    }
}
