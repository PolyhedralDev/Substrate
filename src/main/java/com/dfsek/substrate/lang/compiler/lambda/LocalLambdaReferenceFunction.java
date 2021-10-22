package com.dfsek.substrate.lang.compiler.lambda;

import com.dfsek.substrate.lang.compiler.EphemeralValue;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.compiler.value.Function;
import com.dfsek.substrate.lang.internal.Tuple;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.util.List;

public class LocalLambdaReferenceFunction implements Function {
    private final Signature args;
    private final Signature returnType;
    private final String id;

    private final List<String> internalParameters;

    public LocalLambdaReferenceFunction(Signature args, Signature returnType, String id, List<String> internalParameters) {
        this.args = args;
        this.returnType = returnType;
        this.id = id;
        this.internalParameters = internalParameters;
    }

    @Override
    public Signature arguments() {
        return args;
    }

    @Override
    public void preArgsPrep(MethodVisitor visitor, BuildData data) {
        visitor.visitVarInsn(ALOAD, data.offset(id));
    }

    @Override
    public void invoke(MethodVisitor visitor, BuildData data) {
        String ret = returnType.internalDescriptor();

        if (!returnType.isSimple()) {
            if (returnType.equals(Signature.empty())) ret = "V";
            else ret = "L" + CompilerUtil.internalName(data.tupleFactory().generate(returnType)) + ";";
        }

        visitor.visitMethodInsn(INVOKEINTERFACE,
                CompilerUtil.internalName(data.lambdaFactory().generate(args, returnType)),
                "apply",
                "(" + args.internalDescriptor() + ")" + ret,
                true);
        if (!returnType.isSimple() && !returnType.equals(Signature.empty())) { // tuple deconstruction
            String internal = "$$i" + args.internalDescriptor() + "$" + ret;
            data.registerValue(internal, new EphemeralValue(returnType));
            int offset = data.offset(internal);
            visitor.visitVarInsn(ASTORE, offset);

            for (int i = 0; i < returnType.size(); i++) {
                visitor.visitVarInsn(ALOAD, offset);

                visitor.visitMethodInsn(INVOKEVIRTUAL,
                        CompilerUtil.internalName(Tuple.class) + "IMPL_" + returnType.classDescriptor(),
                        "param" + i,
                        "()" + returnType.getType(i).descriptor(),
                        false);
            }
        }
    }

    public List<String> getInternalParameters() {
        return internalParameters;
    }

    @Override
    public Signature returnType() {
        return returnType;
    }

    @Override
    public boolean ephemeral() {
        return false;
    }
}
