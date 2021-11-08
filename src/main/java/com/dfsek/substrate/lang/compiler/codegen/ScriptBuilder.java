package com.dfsek.substrate.lang.compiler.codegen;

import com.dfsek.substrate.ImplementationArguments;
import com.dfsek.substrate.Script;
import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.DataType;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.lang.compiler.value.function.Function;
import com.dfsek.substrate.lang.internal.Tuple;
import com.dfsek.substrate.parser.DynamicClassLoader;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.util.pair.Pair;
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
    private static final String INTERFACE_CLASS_NAME = CompilerUtil.internalName(Script.class);
    private static final String IMPL_ARG_CLASS_NAME = CompilerUtil.internalName(ImplementationArguments.class);
    private static int builds = 0;
    private final List<Node> ops = new ArrayList<>();

    private final List<Consumer<BuildData>> macros = new ArrayList<>();

    private final List<Pair<String, Function>> functions = new ArrayList<>();

    public void addOperation(Node op) {
        ops.add(op);
    }

    public Script build() throws ParseException {

        String implementationClassName = INTERFACE_CLASS_NAME + "IMPL_" + builds;

        ClassWriter writer = CompilerUtil.generateClass(implementationClassName, false, true, INTERFACE_CLASS_NAME);

        DynamicClassLoader classLoader = new DynamicClassLoader();
        BuildData data = new BuildData(classLoader, writer, implementationClassName);

        // prepare functions.

        MethodVisitor clinit = writer.visitMethod(ACC_PUBLIC | ACC_STATIC,
                "<clinit>",
                "()V",
                null,
                null);
        clinit.visitCode();
        for (int i = 0; i < functions.size(); i++) {
            Function function = functions.get(i).getRight();

            BuildData separate = data.detach((id, d) -> {},
                    d -> data.lambdaFactory().name(function.arguments(), function.reference(d).getSimpleReturn()), function.arguments().frames());
            Signature ref = function.reference(separate);


            Class<?> delegate = data.lambdaFactory().implement(function.arguments(), ref.getSimpleReturn(), Signature.empty(), (method, clazz) -> {
                function.prepare(method);
                Signature args = function.arguments();
                int frame = 1;
                for (int arg = 0; arg < args.size(); arg++) {
                    method.visitVarInsn(args.getType(arg).loadInsn(), frame);
                    frame += (args.getType(arg) == DataType.NUM) ? 2 : 1;
                }
                function.invoke(method, separate, args);
                method.visitInsn(RETURN);
            });

            writer.visitField(ACC_PUBLIC | ACC_FINAL | ACC_STATIC,
                    "fun" + i,
                    "L" + CompilerUtil.internalName(delegate) + ";",
                    null,
                    null);
            clinit.visitTypeInsn(NEW, CompilerUtil.internalName(delegate));
            clinit.visitInsn(DUP);
            clinit.visitMethodInsn(INVOKESPECIAL,
                    CompilerUtil.internalName(delegate),
                    "<init>",
                    "()V",
                    false);
            clinit.visitFieldInsn(PUTSTATIC,
                    implementationClassName,
                    "fun" + i,
                    "L" + CompilerUtil.internalName(delegate) + ";");

            int finalI = i;
            data.registerValue(functions.get(i).getLeft(), new Value() {
                @Override
                public Signature reference() {
                    return function.reference(data);
                }

                @Override
                public void load(MethodVisitor visitor, BuildData data) {
                    visitor.visitFieldInsn(GETSTATIC,
                            implementationClassName,
                            "fun" + finalI,
                            "L" + CompilerUtil.internalName(delegate) + ";");
                }
            });
        }
        clinit.visitInsn(RETURN);
        clinit.visitMaxs(0, 0); // bogus
        clinit.visitEnd();


        MethodVisitor absMethod = writer.visitMethod(ACC_PUBLIC,
                "execute", // Method name
                "(L" + IMPL_ARG_CLASS_NAME + ";)V", // Method descriptor (no args, return double)
                null,
                null);
        absMethod.visitCode();


        macros.forEach(buildDataConsumer -> buildDataConsumer.accept(data));
        ops.forEach(op -> op.apply(absMethod, data));

        absMethod.visitInsn(RETURN);

        absMethod.visitMaxs(0, 0); // Set stack and local variable size (bogus values; handled automatically by ASM)

        absMethod.visitEnd();

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

    public void registerFunction(String id, Function function) {
        functions.add(Pair.of(id, function));
    }

    public void registerMacro(Consumer<BuildData> buildDataConsumer) {
        macros.add(buildDataConsumer);
    }
}
