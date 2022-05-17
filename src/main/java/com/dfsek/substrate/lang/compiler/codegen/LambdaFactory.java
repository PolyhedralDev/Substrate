package com.dfsek.substrate.lang.compiler.codegen;

import com.dfsek.substrate.environment.Environment;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.codegen.ops.Access;
import com.dfsek.substrate.lang.compiler.codegen.ops.ClassBuilder;
import com.dfsek.substrate.lang.compiler.type.DataType;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.internal.Lambda;
import com.dfsek.substrate.parser.DynamicClassLoader;
import io.vavr.Function1;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipOutputStream;

public class LambdaFactory implements Opcodes {
    private static final String IMPL_ARG_CLASS_NAME = Environment.class.getCanonicalName().replace('.', '/');
    private static final String LAMBDA_NAME = CompilerUtil.internalName(Lambda.class);
    private final Map<Signature, Map<Signature, Tuple2<ClassBuilder, AtomicInteger>>> generated = new HashMap<>();
    private final java.util.List<ClassBuilder> implementations = new ArrayList<>();
    private final DynamicClassLoader classLoader;
    private final TupleFactory tupleFactory;
    private final ClassBuilder classBuilder;

    private final ZipOutputStream zipOutputStream;

    public LambdaFactory(DynamicClassLoader classLoader, TupleFactory tupleFactory, ClassBuilder classBuilder, ZipOutputStream zipOutputStream) {
        this.classLoader = classLoader;
        this.tupleFactory = tupleFactory;
        this.classBuilder = classBuilder;
        this.zipOutputStream = zipOutputStream;
    }

    public Tuple2<ClassBuilder, AtomicInteger> generate(Signature args, Signature returnType) {
        return generated.computeIfAbsent(args, ignore -> new HashMap<>()).computeIfAbsent(returnType, ignore -> {
            String endName = "Lambda" + args.classDescriptor() + "R" + returnType.classDescriptor();
            String name = classBuilder.getName() + "$" + endName;

            String[] ifaces;



            if (args.size() == 1 && returnType.weakEquals(Signature.io())) {
                ifaces = new String[]{LAMBDA_NAME, switch (args.getType(0)) {
                    case INT -> Classes.IO_FUNCTION_INT;
                    case NUM -> Classes.IO_FUNCTION_NUM;
                    default -> Classes.IO_FUNCTION;
                }};
            } else {
                ifaces = new String[]{LAMBDA_NAME};
            }

            ClassBuilder builder = new ClassBuilder(name, true, ifaces);

            String ret = returnType.internalDescriptor();

            if (!returnType.isSimple()) {
                if (returnType.equals(Signature.empty())) ret = "V";
                else ret = "L" + CompilerUtil.internalName(tupleFactory.generate(returnType).clazz()) + ";";
            }
            String sig = "(L" + IMPL_ARG_CLASS_NAME + ";" + args.internalDescriptor() + ")" + ret;
            builder.method("apply", sig, Access.PUBLIC, Access.ABSTRACT);

            if (args.size() == 1 && returnType.weakEquals(Signature.io())) {
                DataType arg = args.getType(0);
                MethodVisitor apply = builder.method("apply", "(L" + Classes.ENVIRONMENT + ";" +
                        switch (arg) {
                            case INT -> "I";
                            case NUM -> "D";
                            default -> "L" + Classes.OBJECT + ";";
                        } + ")L" + Classes.OBJECT + ";", Access.PUBLIC);
                apply.visitIntInsn(ALOAD, 0);
                apply.visitIntInsn(ALOAD, 1);

                apply.visitIntInsn(arg.loadInsn(), 2);
                if(arg != DataType.NUM && arg != DataType.INT) {
                    String type = arg.descriptor().substring(1);
                    type = type.substring(0, type.length() - 1);
                    apply.visitTypeInsn(CHECKCAST, type);
                }


                apply.visitMethodInsn(INVOKEINTERFACE, name, "apply", sig, true);

                apply.visitInsn(ARETURN);
                apply.visitMaxs(0, 0);
            } else {
                ifaces = new String[]{LAMBDA_NAME};
            }

            classBuilder.inner(name, classBuilder.getName(), endName, Access.PRIVATE, Access.STATIC, Access.FINAL);

            return new Tuple2<>(builder, new AtomicInteger(0));
        });
    }

    public Either<CompileError, Op> invoke(Signature args, Signature ret, BuildData data) {
        return Op.invokeInterface(generate(args, ret)._1.getName(),
                "apply",
                "(L" + IMPL_ARG_CLASS_NAME + ";" + args.internalDescriptor() + ")" + CompilerUtil.buildReturnType(data, ret));
    }

    public Tuple2<List<CompileError>, ClassBuilder> implement(Signature args, Signature returnType, Signature scope, Function1<ClassBuilder, List<Either<CompileError, Op>>> supplier) {
        Tuple2<ClassBuilder, AtomicInteger> pair = generate(args, returnType);

        String endName = "IM" + pair._2.getAndIncrement();
        String name = classBuilder.getName() + "$Lambda" + args.classDescriptor() + "R" + returnType.classDescriptor() + "$" + endName;

        pair._1.inner(name, pair._1.getName(), endName, Access.PRIVATE, Access.STATIC, Access.FINAL);

        ClassBuilder builder = new ClassBuilder(name, CompilerUtil.internalName(pair._1.getName()));

        MethodVisitor constructor = builder.method("<init>",
                "(" + scope.internalDescriptor() + ")V", Access.PUBLIC);
        constructor.visitCode();

        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);

        int var = 1;
        for (int i = 0; i < scope.size(); i++) {
            builder.field("scope" + i, scope.getType(i).descriptor(), Access.PRIVATE, Access.FINAL);

            constructor.visitVarInsn(ALOAD, 0);
            constructor.visitVarInsn(scope.getType(i).loadInsn(), var);
            constructor.visitFieldInsn(PUTFIELD, name, "scope" + i, scope.getType(i).descriptor());
            var += scope.get(i).frames();
        }

        constructor.visitInsn(RETURN); // Void return
        constructor.visitMaxs(0, 0);
        constructor.visitEnd();

        String ret = returnType.internalDescriptor();

        if (!returnType.isSimple()) {
            if (returnType.equals(Signature.empty())) ret = "V";
            else ret = "L" + CompilerUtil.internalName(tupleFactory.generate(returnType).clazz()) + ";";
        }

        MethodVisitor impl = builder.method("apply", "(L" + IMPL_ARG_CLASS_NAME + ";" + args.internalDescriptor() + ")" + ret, Access.PUBLIC);
        impl.visitCode();
        List<CompileError> errors = supplier.apply(builder)
                .flatMap(either -> either.fold(
                        Option::of,
                        op -> {
                            op.apply(impl);
                            return Option.none();
                        }
                ));

        if (errors.size() == 0) {
            impl.visitMaxs(0, 0);
            impl.visitEnd();
        }

        implementations.add(builder);
        return new Tuple2<>(errors, builder);
    }

    void buildAll() {
        generated.forEach((sig, map) -> map.forEach((sig2, pair) -> pair._1.build(classLoader, zipOutputStream)));
        implementations.forEach(impl -> impl.build(classLoader, zipOutputStream));
    }
}
