package com.dfsek.substrate.lang.compiler.util;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.lambda.LocalLambdaReferenceFunction;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.lang.node.ValueReferenceNode;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.LambdaExpressionNode;
import com.dfsek.substrate.parser.exception.ParseException;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public final class CompilerUtil implements Opcodes {
    public static String internalName(Class<?> clazz) {
        return clazz.getCanonicalName().replace('.', '/');
    }

    public static void dump(Class<?> clazz, byte[] bytes) {
        File dump = new File("./.substrate/dumps/" + internalName(clazz) + ".class");
        dump.getParentFile().mkdirs();
        System.out.println("Dumping to " + dump.getAbsolutePath());
        try {
            IOUtils.write(bytes, new FileOutputStream(dump));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void pushInt(int i, MethodVisitor visitor) {
        if (i == -1) {
            visitor.visitInsn(ICONST_M1);
        } else if (i == 0) {
            visitor.visitInsn(ICONST_0);
        } else if (i == 1) {
            visitor.visitInsn(ICONST_1);
        } else if (i == 2) {
            visitor.visitInsn(ICONST_2);
        } else if (i == 3) {
            visitor.visitInsn(ICONST_3);
        } else if (i == 4) {
            visitor.visitInsn(ICONST_4);
        } else if (i == 5) {
            visitor.visitInsn(ICONST_5);
        } else if (i >= -128 && i < 128) {
            visitor.visitIntInsn(BIPUSH, i); // byte
        } else if (i >= -32768 && i < 32768) {
            visitor.visitIntInsn(SIPUSH, i); // short
        } else {
            visitor.visitLdcInsn(i); // constant pool
        }
    }

    public static void invokeLambda(ExpressionNode lambdaContainer, MethodVisitor visitor, BuildData data) {
        if(!lambdaContainer.referenceType(data).weakEquals(Signature.fun())) {
            throw new ParseException("Expected lambda, got " + lambdaContainer.referenceType(data), lambdaContainer.getPosition());
        }

        List<String> internalParameters;

        if(lambdaContainer instanceof LambdaExpressionNode) {
            internalParameters = ((LambdaExpressionNode) lambdaContainer).internalParameters();
        } else if(lambdaContainer instanceof ValueReferenceNode) {
            internalParameters = ((LocalLambdaReferenceFunction) data.getValue(((ValueReferenceNode) lambdaContainer).getID())).getInternalParameters();
        } else {
            throw new IllegalArgumentException("Cannot extract lambda from node: " + lambdaContainer);
        }

        Signature returnType = lambdaContainer.referenceType(data).getSimpleReturn();
        String ret = returnType.internalDescriptor();

        Signature args = lambdaContainer.referenceType(data).getGenericArguments(0);

        if (!returnType.isSimple()) {
            if (returnType.equals(Signature.empty())) ret = "V";
            else ret = "L" + CompilerUtil.internalName(data.tupleFactory().generate(returnType)) + ";";
        }

        for (String internalParameter : internalParameters) {
            Value internal = data.getValue(internalParameter);
            visitor.visitVarInsn(internal.reference().getType(0).loadInsn(), data.offset(internalParameter));
        }

        visitor.visitMethodInsn(INVOKEINTERFACE,
                CompilerUtil.internalName(data.lambdaFactory().generate(args, returnType)),
                "apply",
                "(" + args.internalDescriptor() + ")" + ret,
                true);
    }
}
