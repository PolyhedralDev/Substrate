package com.dfsek.substrate.lang.compiler.codegen;

import com.dfsek.substrate.Script;
import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.api.Function;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.codegen.ops.Access;
import com.dfsek.substrate.lang.compiler.codegen.ops.ClassBuilder;
import com.dfsek.substrate.lang.compiler.type.DataType;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.compiler.value.FunctionValue;
import com.dfsek.substrate.lang.compiler.value.RecordValue;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.DynamicClassLoader;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Array;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import io.vavr.control.Either;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.zip.ZipOutputStream;

public class ScriptBuilder implements Opcodes {
    private static int builds = 0;

    public static  <P extends Record, R extends Record> Script<P, R> build(ParseData parseData, Node node, List<Tuple2<String, Function>> functions) throws ParseException {
        DynamicClassLoader classLoader = new DynamicClassLoader();

        ZipOutputStream zipOutputStream = createOutputStream();

        String implementationClassName = Classes.SCRIPT + "IMPL_" + builds;

        ClassBuilder builder = new ClassBuilder(CompilerUtil.internalName(implementationClassName), Classes.SCRIPT).defaultConstructor();

        BuildData data = new BuildData(classLoader, builder, zipOutputStream, List.of(parseData.getParameterClass(), parseData.getReturnClass()));

        RecordComponent[] recordComponents = parseData.getParameterClass().getRecordComponents();

        MethodVisitor staticInitializer = builder.method("<clinit>", "()V", Access.PUBLIC, Access.STATIC);
        staticInitializer.visitCode();

        LinkedHashMap<String, Value> values = buildInitialiser(parseData, implementationClassName, builder, data, recordComponents, staticInitializer, functions);


        MethodVisitor absMethod = builder.method("execute", "(L" + Classes.RECORD + ";L" + Classes.ENVIRONMENT + ";)L" + Classes.RECORD + ";", Access.PUBLIC);
        absMethod.visitCode();

        List<CompileError> errors = node
                .apply(data)
                .flatMap(result -> result
                        .fold(List::of, op -> {
                            op.apply(absMethod);
                            return List.empty();
                        }));
        if (!errors.isEmpty()) {
            throw new ParseException(errors.foldLeft(errors.size() + " error(s) present in script:\n\t", (s, e) -> {
                e.dumpStack();
                return s + e.message() + ": " + e.getPosition() + "\n\t";
            }), errors.last().getPosition());
        }

        absMethod.visitMaxs(0, 0);
        absMethod.visitEnd();


        parseData
                .getAssertions()
                .forEach(consumer -> consumer.accept(data)); // perform assertions

        data.lambdaFactory().buildAll();

        staticInitializer.visitInsn(RETURN);
        staticInitializer.visitMaxs(0, 0);
        staticInitializer.visitEnd();

        return dumpClass(classLoader, zipOutputStream, builder);
    }

    private static ZipOutputStream createOutputStream() {
        ZipOutputStream zipOutputStream;
        if ("true".equals(System.getProperty("substrate.Dump"))) {
            try {
                File out = new File(".substrate/dumps/" + builds + ".jar");
                System.out.println("Dumping to " + out.getAbsolutePath());

                if (out.exists()) {
                    // noinspection ResultOfMethodCallIgnored
                    out.delete();
                }
                // noinspection ResultOfMethodCallIgnored
                out.getParentFile().mkdirs();
                // noinspection ResultOfMethodCallIgnored
                out.createNewFile();

                zipOutputStream = new ZipOutputStream(new FileOutputStream(out));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        } else {
            zipOutputStream = null;
        }
        return zipOutputStream;
    }

    private static LinkedHashMap<String, Value> buildInitialiser(ParseData parseData, String implementationClassName, ClassBuilder builder, BuildData data, RecordComponent[] recordComponents, MethodVisitor staticInitializer, List<Tuple2<String, Function>> functions) {
        return (LinkedHashMap<String, Value>) Array.of(recordComponents)
                .zipWithIndex()
                .toLinkedMap(tuple2 -> new Tuple2<>(tuple2._1.getName(), (Value) new RecordValue(Signature.fromType(tuple2._1.getType()), parseData.getParameterClass(), tuple2._2)))
                .merge(functions.toMap(stringFunctionPair -> {
                    Function function = stringFunctionPair._2;
                    String functionName = "wrap$" + stringFunctionPair._1;

                    return Tuple.of(stringFunctionPair._1, new FunctionValue(function, implementationClassName, functionName, () -> {
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
                            return ops.appendAll(function.invoke(data, args))
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
                }));
    }

    private static <P extends Record, R extends Record> Script<P, R> dumpClass(DynamicClassLoader classLoader, ZipOutputStream zipOutputStream, ClassBuilder builder) {
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
            //noinspection unchecked
            return (Script<P, R>) instance;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
