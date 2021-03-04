package com.dfsek.terrascript.lang;

import com.dfsek.terrascript.ImplementationArguments;
import com.dfsek.terrascript.TerraScript;
import com.dfsek.terrascript.lang.impl.ScriptBuildData;
import com.dfsek.terrascript.lang.internal.Operation;
import com.dfsek.terrascript.parser.DynamicClassLoader;
import com.dfsek.terrascript.parser.exception.ParseException;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class ScriptBuilder {
    private final List<Operation> ops = new ArrayList<>();
    private static int builds = 0;

    private static final boolean DUMP = "true".equals(System.getProperty("terrascript.asm.dump"));
    private static final String INTERFACE_CLASS_NAME = TerraScript.class.getCanonicalName().replace('.', '/');
    private static final String IMPL_ARG_CLASS_NAME = ImplementationArguments.class.getCanonicalName().replace('.', '/');

    public void addOperation(Operation op) {
        ops.add(op);
    }

    public TerraScript build() throws ParseException {

        String implementationClassName = INTERFACE_CLASS_NAME + "IMPL_" + builds;

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);

        //functions.forEach((id, function) -> writer.visitField(ACC_PUBLIC, id, "L" + DynamicFunction.class.getCanonicalName().replace('.', '/') + ";", null, null));

        writer.visit(V1_8,
                ACC_PUBLIC,
                implementationClassName,
                null,
                "java/lang/Object",
                new String[]{INTERFACE_CLASS_NAME});

        MethodVisitor constructor = writer.visitMethod(ACC_PUBLIC,
                "<init>", // Constructor method name is <init>
                "()V",
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

        MethodVisitor absMethod = writer.visitMethod(ACC_PUBLIC,
                "execute", // Method name
                "(L" + IMPL_ARG_CLASS_NAME + ";)V", // Method descriptor (no args, return double)
                null,
                null);
        absMethod.visitCode();

        ScriptBuildData data = new ScriptBuildData(implementationClassName);
        for (Operation op : ops) {
            System.out.println(op);
            op.apply(absMethod, data);
        }

        absMethod.visitInsn(RETURN); // Return double at top of stack (operation leaves one double on stack)


        absMethod.visitMaxs(0, 0); // Set stack and local variable size (bogus values; handled automatically by ASM)

        byte[] bytes = writer.toByteArray();

        if(true) {
            File dump = new File("./dumps/TerraScriptIMPL_" + builds + ".class");
            dump.getParentFile().mkdirs();
            System.out.println("Dumping to " + dump.getAbsolutePath());
            try {
                IOUtils.write(bytes, new FileOutputStream(dump));
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        Class<?> clazz = new DynamicClassLoader().defineClass(implementationClassName.replace('/', '.'), writer.toByteArray());

        builds++;
        try {
            Object instance = clazz.newInstance();
            return (TerraScript) instance;
        } catch(InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
