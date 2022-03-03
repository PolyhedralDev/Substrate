package com.dfsek.substrate.lang.std.function;

import com.dfsek.substrate.lang.compiler.api.Macro;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import io.vavr.collection.List;
import io.vavr.control.Either;
import org.objectweb.asm.Label;

public class ForEach implements Macro {
    @Override
    public Signature arguments() {
        return Signature.list()
                .and(Signature.fun()
                        .applyGenericReturn(0, Signature.empty()));
    }

    @Override
    public boolean argsMatch(Signature attempt) {
        return arguments().weakEquals(attempt) &&
                attempt.getGenericReturn(0).equals(attempt.getGenericArguments(1));
    }

    @Override
    public List<Either<CompileError, Op>> invoke(BuildData data, Signature args, List<ExpressionNode> argNodes) {
        Signature type = args.getGenericReturn(0);
        data.offsetInc(1);
        int lambdaLV = data.getOffset();

        data.offsetInc(1);
        int arrayLV = data.getOffset();

        data.offsetInc(1);
        int arrayLengthLV = data.getOffset();

        data.offsetInc(1);
        int iLV = data.getOffset();

        Label start = new Label();
        Label end = new Label();

        return argNodes.flatMap(arg -> arg.simplify().apply(data))

                .append(Op.aStore(lambdaLV)) // store lambda in LV

                .append(Op.dup()) // duplicate array ref

                .append(Op.aStore(arrayLV)) // store array in LV
                .append(Op.arrayLength())

                .append(Op.iStore(arrayLengthLV))

                .append(Op.pushInt(0))
                .append(Op.iStore(iLV))

                .append(Op.label(start))
                .append(Op.iLoad(iLV))
                .append(Op.iLoad(arrayLengthLV)) // store array length in LV

                .append(Op.ifICmpGE(end)) // jump to end if i >= length
                .append(Op.aLoad(lambdaLV))// load lambda
                .append(Op.aLoad(data.getImplArgsOffset()))
                .append(Op.aLoad(arrayLV)) // get value from array
                .append(Op.iLoad(iLV)) // store iterator value in LV
                .append(type.arrayLoadInsn().bimap(
                        s -> Op.errorUnwrapped(s, argNodes.get(0).getPosition()),
                        Op::insnUnwrapped
                ))
                .append(data.lambdaFactory().invoke(args.getGenericReturn(0), Signature.empty(), data))
                .append(Op.iinc(iLV, 1))
                .append(Op.goTo(start))
                .append(Op.label(end));

    }

    @Override
    public Signature reference(Signature arguments) {
        return Signature.fun().applyGenericReturn(0, Signature.empty()).applyGenericArgument(0, arguments());
    }
}
