package com.dfsek.substrate.lang.std.function;

import com.dfsek.substrate.lang.compiler.api.Macro;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
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
    public void invoke(MethodBuilder visitor, BuildData data, Signature args) {
        Signature type = args.getGenericReturn(0);
        data.offsetInc(1);
        int lambdaLV = data.getOffset();
        visitor.aStore(lambdaLV) // store lambda in LV

                .dup(); // duplicate array ref
        data.offsetInc(1);
        int arrayLV = data.getOffset();
        visitor.aStore(arrayLV) // store array in LV

                .arrayLength();

        data.offsetInc(1);
        int arrayLengthLV = data.getOffset();
        visitor.iStore(arrayLengthLV) // store array length in LV

                .pushInt(0);
        data.offsetInc(1);
        int iLV = data.getOffset();
        visitor.iStore(iLV); // store iterator value in LV

        Label start = new Label();
        Label end = new Label();

        visitor.label(start)

                .iLoad(iLV)
                .iLoad(arrayLengthLV)
                .ifICmpGE(end) // jump to end if i >= length

                .aLoad(lambdaLV) // load lambda

                .aLoad(arrayLV) // get value from array
                .iLoad(iLV);
        if (type.isSimple()) {
            visitor.insn(type.getType(0).arrayLoadInsn());
        } else {
            visitor.aaload();
        }

        data.lambdaFactory().invoke(args.getGenericReturn(0), Signature.empty(), data, visitor);

        visitor.iinc(iLV, 1)
                .goTo(start)

                .label(end);
    }

    @Override
    public Signature reference(BuildData data) {
        return Signature.fun().applyGenericReturn(0, Signature.empty()).applyGenericArgument(0, arguments());
    }
}
