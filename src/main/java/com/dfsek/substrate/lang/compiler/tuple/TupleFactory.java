package com.dfsek.substrate.lang.compiler.tuple;

import com.dfsek.substrate.lang.compiler.type.DataType;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.internal.Tuple;
import com.dfsek.substrate.parser.DynamicClassLoader;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

public class TupleFactory {
    private final Map<Signature, Class<?>> generated = new HashMap<>();

    private final DynamicClassLoader classLoader;

    public TupleFactory(DynamicClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Class<?> generate(Signature args) {
        return generated.computeIfAbsent(args, ignore -> {
            ClassWriter writer = new ClassWriter(org.objectweb.asm.ClassWriter.COMPUTE_FRAMES + org.objectweb.asm.ClassWriter.COMPUTE_MAXS);
            String name = CompilerUtil.internalName(Tuple.class) + "IMPL_" + args.classDescriptor();

            writer.visit(V1_8,
                    ACC_PUBLIC,
                    name,
                    null,
                    "java/lang/Object",
                    new String[]{CompilerUtil.internalName(Tuple.class)});

            String constructorSig = "(" + args.internalDescriptor() + ")V";

            MethodVisitor constructor = writer.visitMethod(ACC_PUBLIC,
                    "<init>", // Constructor method name is <init>
                    constructorSig,
                    null,
                    null);

            constructor.visitCode();
            constructor.visitVarInsn(ALOAD, 0); // Put this reference on stack
            constructor.visitMethodInsn(INVOKESPECIAL, // Invoke Object super constructor
                    "java/lang/Object",
                    "<init>",
                    "()V",
                    false);

            int offset = 1;
            for (int i = 0; i < args.size(); i++) {
                String param = "param" + i;
                DataType argType = args.getType(i);

                writer.visitField(ACC_PRIVATE | ACC_FINAL,
                        param,
                        argType.descriptor(),
                        null,
                        null);
                constructor.visitVarInsn(ALOAD, 0);
                constructor.visitVarInsn(argType.loadInsn(), offset++);

                if (argType == DataType.NUM) { // double takes up 2 frames
                    offset++;
                }

                constructor.visitFieldInsn(PUTFIELD, name, param, argType.descriptor());


                MethodVisitor paramGetter = writer.visitMethod(ACC_PUBLIC | ACC_FINAL,
                        param, // Constructor method name is <init>
                        "()" + argType.descriptor(),
                        null,
                        null);
                paramGetter.visitCode();
                paramGetter.visitVarInsn(ALOAD, 0);
                paramGetter.visitFieldInsn(GETFIELD, name, param, argType.descriptor());
                paramGetter.visitInsn(argType.returnInsn());
                paramGetter.visitMaxs(0, 0);
            }

            constructor.visitInsn(RETURN); // Void return
            constructor.visitMaxs(0, 0); // Set stack and local variable size (bogus values; handled automatically by ASM)


            byte[] bytes = writer.toByteArray();
            Class<?> clazz = classLoader.defineClass(name.replace('/', '.'), bytes);
            CompilerUtil.dump(clazz, bytes);
            return clazz;
        });
    }
}
