package com.dfsek.substrate.lang.std.function;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.compiler.value.Function;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.util.List;

public class ForEach implements Function {
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
    public void invoke(MethodVisitor visitor, BuildData data, Signature args, List<ExpressionNode> argExpressions) {
        Signature type = args.getGenericReturn(0);
        data.offsetInc(1);
        int lambdaLV = data.getOffset();
        visitor.visitVarInsn(ASTORE, lambdaLV); // store lambda in LV

        visitor.visitInsn(DUP); // duplicate array ref
        data.offsetInc(1);
        int arrayLV = data.getOffset();
        visitor.visitVarInsn(ASTORE, arrayLV); // store array in LV

        visitor.visitInsn(ARRAYLENGTH);
        data.offsetInc(1);
        int arrayLengthLV = data.getOffset();
        visitor.visitVarInsn(ISTORE, arrayLengthLV); // store array length in LV

        visitor.visitInsn(ICONST_0);
        data.offsetInc(1);
        int iLV = data.getOffset();
        visitor.visitVarInsn(ISTORE, iLV); // store iterator value in LV

        Label start = new Label();
        Label end = new Label();

        visitor.visitLabel(start);

        visitor.visitVarInsn(ILOAD, iLV);
        visitor.visitVarInsn(ILOAD, arrayLengthLV);
        visitor.visitJumpInsn(IF_ICMPGE, end); // jump to end if i >= length

        visitor.visitVarInsn(ALOAD, lambdaLV); // load lambda

        visitor.visitVarInsn(ALOAD, arrayLV); // get value from array
        visitor.visitVarInsn(ILOAD, iLV);
        if(type.isSimple()) {
            visitor.visitInsn(type.getType(0).arrayLoadInsn());
        } else {
            visitor.visitInsn(AALOAD);
        }

        CompilerUtil.invokeLambda(argExpressions.get(1), visitor, data);

        visitor.visitIincInsn(iLV, 1);
        visitor.visitJumpInsn(GOTO, start);

        visitor.visitLabel(end);
    }

    @Override
    public Signature reference() {
        return Signature.fun().applyGenericReturn(0, Signature.empty()).applyGenericArgument(0, arguments());
    }
}
