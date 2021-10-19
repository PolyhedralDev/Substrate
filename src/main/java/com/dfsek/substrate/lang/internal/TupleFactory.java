package com.dfsek.substrate.lang.internal;

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
    private static final Map<Integer, Class<?>> generated = new HashMap<>();

    public Class<?> generate(int args) {
        return generated.computeIfAbsent(args, ignore -> {
            ClassWriter writer = new ClassWriter(org.objectweb.asm.ClassWriter.COMPUTE_FRAMES + org.objectweb.asm.ClassWriter.COMPUTE_MAXS);
            String name = "com/dfsek/substrate/lang/internal/tuple/TupleIMPL_" + args;

            writer.visit(V1_8,
                    ACC_PUBLIC,
                    name,
                    null,
                    "java/lang/Object",
                    new String[0]);

            StringBuilder constructorSig = new StringBuilder("(");
            for (int i = 0; i < args; i++) {
                constructorSig.append("Ljava/lang/Object;");

                writer.visitField(ACC_PRIVATE | ACC_FINAL,
                        "param" + i,
                        "Ljava/lang/Object;",
                        null,
                        null);

            }
            constructorSig.append(")V");

            MethodVisitor constructor = writer.visitMethod(ACC_PUBLIC,
                    "<init>", // Constructor method name is <init>
                    constructorSig.toString(),
                    null,
                    null);

            constructor.visitCode();
            constructor.visitVarInsn(ALOAD, 0); // Put this reference on stack
            constructor.visitMethodInsn(INVOKESPECIAL, // Invoke Object super constructor
                    "java/lang/Object",
                    "<init>",
                    "()V",
                    false);
            constructor.visitInsn(RETURN); // Void return
            constructor.visitMaxs(0, 0); // Set stack and local variable size (bogus values; handled automatically by ASM)


            byte[] bytes = writer.toByteArray();

            if(true) {
                File dump = new File("./dumps/" + name + ".class");
                dump.getParentFile().mkdirs();
                System.out.println("Dumping to " + dump.getAbsolutePath());
                try {
                    IOUtils.write(bytes, new FileOutputStream(dump));
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }

            return new DynamicClassLoader().defineClass(name.replace('/', '.'), writer.toByteArray());
        });
    }
}
