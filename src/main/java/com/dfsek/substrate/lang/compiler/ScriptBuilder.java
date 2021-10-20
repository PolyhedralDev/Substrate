package com.dfsek.substrate.lang.compiler;

import com.dfsek.substrate.ImplementationArguments;
import com.dfsek.substrate.Script;
import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.std.function.Println;
import com.dfsek.substrate.lang.std.function.PrintlnTest;
import com.dfsek.substrate.parser.DynamicClassLoader;
import com.dfsek.substrate.parser.exception.ParseException;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class ScriptBuilder {
    private final List<Node> ops = new ArrayList<>();
    private static int builds = 0;

    private static final boolean DUMP = "true".equals(System.getProperty("terrascript.asm.dump"));
    private static final String INTERFACE_CLASS_NAME = Script.class.getCanonicalName().replace('.', '/');
    private static final String IMPL_ARG_CLASS_NAME = ImplementationArguments.class.getCanonicalName().replace('.', '/');

    public void addOperation(Node op) {
        ops.add(op);
    }

    public Script build() throws ParseException {

        String implementationClassName = INTERFACE_CLASS_NAME + "IMPL_" + builds;

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);

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


        Label begin = new Label();

        DynamicClassLoader classLoader = new DynamicClassLoader();

        BuildData data = new BuildData(classLoader, writer);
        data.registerValue("println", new Println());
        data.registerValue("test", new PrintlnTest());
        ops.forEach(op -> op.apply(absMethod, data));

        Label end = new Label();

        data.getValues().forEach((id, value) -> {
            if(value.ephemeral()) return;
            String descriptor;
            if(value.type().isSimple()) {
                descriptor = value.type().getType(0).descriptor();
            } else {
                descriptor = "Lcom/dfsek/substrate/lang/internal/Tuple;";
            }
            absMethod.visitLocalVariable(id, descriptor, null, begin, end, data.offset(id));
        });

        absMethod.visitInsn(RETURN); // Return double at top of stack (operation leaves one double on stack)


        absMethod.visitMaxs(0, 0); // Set stack and local variable size (bogus values; handled automatically by ASM)

        byte[] bytes = writer.toByteArray();

        if(true) {
            File dump = new File("./dumps/ScriptIMPL_" + builds + ".class");
            dump.getParentFile().mkdirs();
            System.out.println("Dumping to " + dump.getAbsolutePath());
            try {
                IOUtils.write(bytes, new FileOutputStream(dump));
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        Class<?> clazz = classLoader.defineClass(implementationClassName.replace('/', '.'), writer.toByteArray());

        builds++;
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            return (Script) instance;
        } catch(InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
