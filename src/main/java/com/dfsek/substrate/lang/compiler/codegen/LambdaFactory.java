package com.dfsek.substrate.lang.compiler.codegen;

import com.dfsek.substrate.ImplementationArguments;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.codegen.ops.Access;
import com.dfsek.substrate.lang.compiler.codegen.ops.ClassBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.internal.Lambda;
import com.dfsek.substrate.parser.DynamicClassLoader;
import com.dfsek.substrate.util.Pair;
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
    private final Map<Signature, Map<Signature, Pair<ClassBuilder, AtomicInteger>>> generated = new HashMap<>();
    private final java.util.List<ClassBuilder> implementations = new ArrayList<>();

    private final DynamicClassLoader classLoader;
    private final TupleFactory tupleFactory;

    private static final String IMPL_ARG_CLASS_NAME = ImplementationArguments.class.getCanonicalName().replace('.', '/');

    private static final String LAMBDA_NAME = CompilerUtil.internalName(Lambda.class);

    private final ClassBuilder classBuilder;

    private final ZipOutputStream zipOutputStream;

    public LambdaFactory(DynamicClassLoader classLoader, TupleFactory tupleFactory, ClassBuilder classBuilder, ZipOutputStream zipOutputStream) {
        this.classLoader = classLoader;
        this.tupleFactory = tupleFactory;
        this.classBuilder = classBuilder;
        this.zipOutputStream = zipOutputStream;
    }

    public Pair<ClassBuilder, AtomicInteger> generate(Signature args, Signature returnType) {
        return generated.computeIfAbsent(args, ignore -> new HashMap<>()).computeIfAbsent(returnType, ignore -> {
            String endName = "Lambda" + args.classDescriptor() + "R" + returnType.classDescriptor();
            String name = classBuilder.getName() + "$" + endName;
            ClassBuilder builder = new ClassBuilder(name, true, LAMBDA_NAME);

            String ret = returnType.internalDescriptor();

            if (!returnType.isSimple()) {
                if (returnType.equals(Signature.empty())) ret = "V";
                else ret = "L" + CompilerUtil.internalName(tupleFactory.generate(returnType)) + ";";
            }

            builder.method("apply", "(L" + IMPL_ARG_CLASS_NAME + ";" + args.internalDescriptor() + ")" + ret, Access.PUBLIC, Access.ABSTRACT);

            classBuilder.inner(name, classBuilder.getName(), endName, Access.PRIVATE, Access.STATIC, Access.FINAL);

            return Pair.of(builder, new AtomicInteger(0));
        });
    }

    public Either<CompileError, Op> invoke(Signature args, Signature ret, BuildData data) {
        return Op.invokeInterface(generate(args, ret).getLeft().getName(),
                "apply",
                "(L" + IMPL_ARG_CLASS_NAME + ";" + args.internalDescriptor() + ")" + CompilerUtil.buildReturnType(data, ret));
    }

    public Tuple2<List<CompileError>, ClassBuilder> implement(Signature args, Signature returnType, Signature scope, Function1<ClassBuilder, List<Either<CompileError, Op>>> supplier) {
        Pair<ClassBuilder, AtomicInteger> pair = generate(args, returnType);

        String endName = "IM" + pair.getRight().getAndIncrement();
        String name = classBuilder.getName() + "$Lambda" + args.classDescriptor() + "R" + returnType.classDescriptor() + "$" + endName;

        pair.getLeft().inner(name, pair.getLeft().getName(), endName, Access.PRIVATE, Access.STATIC, Access.FINAL);

        ClassBuilder builder = new ClassBuilder(name, CompilerUtil.internalName(pair.getLeft().getName()));

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
            else ret = "L" + CompilerUtil.internalName(tupleFactory.generate(returnType)) + ";";
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

        impl.visitMaxs(0, 0);
        impl.visitEnd();

        implementations.add(builder);
        return new Tuple2<>(errors, builder);
    }

    void buildAll() {
        generated.forEach((sig, map) -> map.forEach((sig2, pair) -> pair.getLeft().build(classLoader, zipOutputStream)));
        implementations.forEach(impl -> impl.build(classLoader, zipOutputStream));
    }
}
