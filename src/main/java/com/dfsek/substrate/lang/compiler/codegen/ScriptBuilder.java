package com.dfsek.substrate.lang.compiler.codegen;

import com.dfsek.substrate.Environment;
import com.dfsek.substrate.Script;
import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.api.Function;
import com.dfsek.substrate.lang.compiler.api.Macro;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.codegen.ops.Access;
import com.dfsek.substrate.lang.compiler.codegen.ops.ClassBuilder;
import com.dfsek.substrate.lang.compiler.type.DataType;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.compiler.value.FunctionValue;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.DynamicClassLoader;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Either;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipOutputStream;

public class ScriptBuilder implements Opcodes {
    private static int builds = 0;
    private final Map<String, Macro> macros = new HashMap<>();
    private List<Node> ops = List.empty();
    private List<Tuple2<String, Function>> functions = List.empty();

    public void addOperation(Node op) {
        this.ops = ops.append(op); // todo: bad
    }

    public <P extends Record, R extends Record> Script<P, R> build(ParseData parseData, Class<P> parameters, Class<R> ret) throws ParseException {
        DynamicClassLoader classLoader = new DynamicClassLoader();
        ZipOutputStream zipOutputStream;
        if ("true".equals(System.getProperty("substrate.Dump"))) {
            try {
                File out = new File(".substrate/dumps/" + builds + ".jar");
                System.out.println("Dumping to " + out.getAbsolutePath());
                if (out.exists()) out.delete();
                out.getParentFile().mkdirs();
                out.createNewFile();
                zipOutputStream = new ZipOutputStream(new FileOutputStream(out));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        } else {
            zipOutputStream = null;
        }
        String implementationClassName = Classes.SCRIPT + "IMPL_" + builds;

        ClassBuilder builder = new ClassBuilder(CompilerUtil.internalName(implementationClassName), Classes.SCRIPT).defaultConstructor();

        BuildData data = new BuildData(classLoader, builder, zipOutputStream);

        // prepare functions.

        MethodVisitor staticInitializer = builder.method("<clinit>", "()V", Access.PUBLIC, Access.STATIC);
        staticInitializer.visitCode();

        for (Tuple2<String, Function> stringFunctionPair : functions) {
            Function function = stringFunctionPair._2;
            String functionName = "wrap$" + stringFunctionPair._1;

            data.registerValue(stringFunctionPair._1, new FunctionValue(function, implementationClassName, functionName, () -> {
                BuildData separate = data.sub();
                Signature ref = function.reference();

                String delegate = data.lambdaFactory().implement(function.arguments(), ref.getSimpleReturn(), Signature.empty(), clazz -> {
                    List<Either<CompileError, Op>> ops = function.prepare();
                    Signature args = function.arguments();
                    int frame = 2;
                    for (int arg = 0; arg < args.size(); arg++) {
                        ops = ops.append(Op.varInsn(args.getType(arg).loadInsn(), frame));
                        frame += (args.getType(arg) == DataType.NUM) ? 2 : 1;
                    }
                    Signature functionReturn = function.reference().getGenericReturn(0);
                    return ops.appendAll(function.invoke(separate, args))
                            .append(functionReturn.retInsn()
                                    .mapLeft(m -> Op.errorUnwrapped(m, Position.getNull()))
                                    .map(Op::insnUnwrapped));
                })._2.getName();

                builder.field(functionName,
                        "L" + delegate + ";",
                        Access.PUBLIC, Access.STATIC, Access.STATIC);

                staticInitializer.visitTypeInsn(NEW, delegate);
                staticInitializer.visitInsn(DUP);
                staticInitializer.visitMethodInsn(INVOKESPECIAL, delegate, "<init>", "()V", false);
                staticInitializer.visitFieldInsn(PUTSTATIC, implementationClassName, functionName, "L" + delegate + ";");

                return delegate;
            }));
        }


        MethodVisitor absMethod = builder.method("execute", "(L" + Classes.RECORD + ";L" + Classes.ENVIRONMENT + ";)L" + Classes.RECORD + ";", Access.PUBLIC);
        absMethod.visitCode();
        macros.forEach(data::registerMacro);

        List<CompileError> errors = ops
                .flatMap(node -> node
                        .apply(data)
                        .flatMap(result -> result
                                .fold(List::of, op -> {
                                    op.apply(absMethod);
                                    return List.empty();
                                })));
        absMethod.visitInsn(ARETURN);
        absMethod.visitMaxs(0, 0);
        absMethod.visitEnd();


        if (!errors.isEmpty()) {
            throw new IllegalStateException(errors.toString()); // todo: actual exception
        }


        parseData
                .getAssertions()
                .forEach(consumer -> consumer.accept(data)); // perform assertions

        data.lambdaFactory().buildAll();

        staticInitializer.visitInsn(RETURN);
        staticInitializer.visitMaxs(0, 0);
        staticInitializer.visitEnd();

        Class<?> clazz = builder.build(classLoader, zipOutputStream);

        if (zipOutputStream != null) {
            try {
                zipOutputStream.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        builds++;
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            return (Script) instance;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerFunction(String id, Function function) {
        functions = functions.append(new Tuple2<>(id, function)); // todo: bad
    }

    public void registerMacro(String id, Macro macro) {
        macros.put(id, macro);
    }

}
