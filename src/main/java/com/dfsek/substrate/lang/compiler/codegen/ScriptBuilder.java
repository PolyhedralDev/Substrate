package com.dfsek.substrate.lang.compiler.codegen;

import com.dfsek.substrate.ImplementationArguments;
import com.dfsek.substrate.Script;
import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.api.Macro;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.ClassBuilder;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.DataType;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.compiler.value.FunctionValue;
import com.dfsek.substrate.lang.compiler.api.Function;
import com.dfsek.substrate.parser.DynamicClassLoader;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.util.pair.Pair;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

public class ScriptBuilder {
    private static final boolean DUMP = "true".equals(System.getProperty("terrascript.asm.dump"));
    private static final String INTERFACE_CLASS_NAME = CompilerUtil.internalName(Script.class);
    private static final String IMPL_ARG_CLASS_NAME = CompilerUtil.internalName(ImplementationArguments.class);
    private static int builds = 0;
    private final List<Node> ops = new ArrayList<>();

    private final Map<String, Macro> macros = new HashMap<>();

    private final List<Pair<String, Function>> functions = new ArrayList<>();

    public void addOperation(Node op) {
        ops.add(op);
    }

    public Script build() throws ParseException {
        DynamicClassLoader classLoader = new DynamicClassLoader();

        String implementationClassName = INTERFACE_CLASS_NAME + "IMPL_" + builds;

        ClassBuilder builder = new ClassBuilder(CompilerUtil.internalName(implementationClassName), INTERFACE_CLASS_NAME).defaultConstructor();

        BuildData data = new BuildData(classLoader, builder, implementationClassName);

        // prepare functions.

        MethodBuilder staticInitializer = builder.method("<clinit>", "()V")
                .access(MethodBuilder.Access.PUBLIC)
                .access(MethodBuilder.Access.STATIC);


        for (int i = 0; i < functions.size(); i++) {
            Function function = functions.get(i).getRight();

            BuildData separate = data.detach((id, d) -> {
                    },
                    d -> data.lambdaFactory().name(function.arguments(), function.reference(d).getSimpleReturn()), function.arguments().frames());
            Signature ref = function.reference(separate);


            Class<?> delegate = data.lambdaFactory().implement(function.arguments(), ref.getSimpleReturn(), Signature.empty(), (method) -> {
                function.prepare(method);
                Signature args = function.arguments();
                int frame = 1;
                for (int arg = 0; arg < args.size(); arg++) {
                    method.varInsn(args.getType(arg).loadInsn(), frame);
                    frame += (args.getType(arg) == DataType.NUM) ? 2 : 1;
                }
                function.invoke(method, separate, args);
                method.voidReturn();
            });

            builder.field("fun" + i,
                    "L" + CompilerUtil.internalName(delegate) + ";",
                    MethodBuilder.Access.PRIVATE, MethodBuilder.Access.STATIC, MethodBuilder.Access.STATIC);

            staticInitializer.newInsn(CompilerUtil.internalName(delegate))
                    .dup()
                    .invokeSpecial(CompilerUtil.internalName(delegate), "<init>", "()V")
                    .putStatic(implementationClassName, "fun" + i, "L" + CompilerUtil.internalName(delegate) + ";");

            data.registerValue(functions.get(i).getLeft(), new FunctionValue(function, data, implementationClassName, i, delegate));
        }

        staticInitializer.voidReturn();


        MethodBuilder absMethod = builder.method("execute", "(L" + IMPL_ARG_CLASS_NAME + ";)V").access(MethodBuilder.Access.PUBLIC);

        macros.forEach(data::registerMacro);

        ops.forEach(op -> op.apply(absMethod, data));


        Class<?> clazz = builder.build(classLoader);

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

    public void registerMacro(String id, Macro macro) {
        macros.put(id, macro);
    }

}
