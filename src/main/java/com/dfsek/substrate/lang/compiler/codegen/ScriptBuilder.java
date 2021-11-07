package com.dfsek.substrate.lang.compiler.codegen;

import com.dfsek.substrate.ImplementationArguments;
import com.dfsek.substrate.Script;
import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.internal.Tuple;
import com.dfsek.substrate.parser.DynamicClassLoader;
import com.dfsek.substrate.parser.exception.ParseException;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.objectweb.asm.Opcodes.*;

public class ScriptBuilder {
    private static final boolean DUMP = "true".equals(System.getProperty("terrascript.asm.dump"));
    private static final String INTERFACE_CLASS_NAME = Script.class.getCanonicalName().replace('.', '/');
    private static final String IMPL_ARG_CLASS_NAME = ImplementationArguments.class.getCanonicalName().replace('.', '/');
    private static int builds = 0;
    private final List<Node> ops = new ArrayList<>();

    private final List<Consumer<BuildData>> macros = new ArrayList<>();

    public void addOperation(Node op) {
        ops.add(op);
    }

    public Script build() throws ParseException {

        String implementationClassName = INTERFACE_CLASS_NAME + "IMPL_" + builds;

        ClassWriter writer = CompilerUtil.generateClass(implementationClassName, false, true, INTERFACE_CLASS_NAME);

        MethodVisitor absMethod = writer.visitMethod(ACC_PUBLIC,
                "execute", // Method name
                "(L" + IMPL_ARG_CLASS_NAME + ";)V", // Method descriptor (no args, return double)
                null,
                null);
        absMethod.visitCode();

        Label begin = new Label();

        DynamicClassLoader classLoader = new DynamicClassLoader();

        BuildData data = new BuildData(classLoader, writer, implementationClassName);
        macros.forEach(buildDataConsumer -> buildDataConsumer.accept(data));
        ops.forEach(op -> op.apply(absMethod, data));

        Label end = new Label();

        absMethod.visitInsn(RETURN);

        absMethod.visitMaxs(0, 0); // Set stack and local variable size (bogus values; handled automatically by ASM)

        byte[] bytes = writer.toByteArray();

        Class<?> clazz = classLoader.defineClass(implementationClassName.replace('/', '.'), bytes);

        CompilerUtil.dump(clazz, bytes);
        builds++;
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            return (Script) instance;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerMacro(Consumer<BuildData> buildDataConsumer) {
        macros.add(buildDataConsumer);
    }
}
