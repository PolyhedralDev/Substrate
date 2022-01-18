package com.dfsek.substrate.lang.compiler.codegen;

import com.dfsek.substrate.ImplementationArguments;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.ClassBuilder;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.internal.Lambda;
import com.dfsek.substrate.parser.DynamicClassLoader;
import com.dfsek.substrate.util.pair.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.zip.ZipOutputStream;

public class LambdaFactory {
    private final Map<Signature, Map<Signature, Pair<ClassBuilder, AtomicInteger>>> generated = new HashMap<>();
    private final List<ClassBuilder> implementations = new ArrayList<>();

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

            builder.method("apply", "(L" + IMPL_ARG_CLASS_NAME + ";" + args.internalDescriptor() + ")" + ret)
                    .access(MethodBuilder.Access.PUBLIC)
                    .access(MethodBuilder.Access.ABSTRACT);

            classBuilder.inner(name, classBuilder.getName(), endName, MethodBuilder.Access.PUBLIC, MethodBuilder.Access.STATIC, MethodBuilder.Access.FINAL);

            return Pair.of(builder, new AtomicInteger(0));
        });
    }

    public void invoke(Signature args, Signature ret, BuildData data, MethodBuilder visitor) {

        visitor.invokeInterface(generate(args, ret).getLeft().getName(),
                "apply",
                "(L" + IMPL_ARG_CLASS_NAME + ";" + args.internalDescriptor() + ")" + CompilerUtil.buildReturnType(data, ret));
    }

    public ClassBuilder implement(Signature args, Signature returnType, Signature scope, Consumer<MethodBuilder> consumer) {
        Pair<ClassBuilder, AtomicInteger> pair = generate(args, returnType);

        String endName = "IM" + pair.getRight().getAndIncrement();
        String name = classBuilder.getName() + "$Lambda" + args.classDescriptor() + "R" + returnType.classDescriptor() + "$" + endName;

        pair.getLeft().inner(name, pair.getLeft().getName(), endName, MethodBuilder.Access.PUBLIC, MethodBuilder.Access.STATIC, MethodBuilder.Access.FINAL);

        ClassBuilder builder = new ClassBuilder(name, CompilerUtil.internalName(pair.getLeft().getName()));

        MethodBuilder constructor = builder.method("<init>",
                        "(" + scope.internalDescriptor() + ")V")
                .access(MethodBuilder.Access.PUBLIC);


        constructor.aLoad(0)
                .invokeSpecial("java/lang/Object", "<init>", "()V");

        for (int i = 0; i < scope.size(); i++) {
            builder.field("scope" + i, scope.getType(i).descriptor(), MethodBuilder.Access.PRIVATE, MethodBuilder.Access.FINAL);

            constructor.aLoad(0)
                    .varInsn(scope.getType(i).loadInsn(), i + 1)
                    .putField(name, "scope" + i, scope.getType(i).descriptor());
        }

        constructor.voidReturn(); // Void return

        String ret = returnType.internalDescriptor();

        if (!returnType.isSimple()) {
            if (returnType.equals(Signature.empty())) ret = "V";
            else ret = "L" + CompilerUtil.internalName(tupleFactory.generate(returnType)) + ";";
        }

        consumer.accept(builder.method("apply", "(L" + IMPL_ARG_CLASS_NAME + ";" + args.internalDescriptor() + ")" + ret).access(MethodBuilder.Access.PUBLIC));

        implementations.add(builder);
        return builder;
    }

    void buildAll() {
        generated.forEach((sig, map) -> map.forEach((sig2, pair) -> pair.getLeft().build(classLoader, zipOutputStream)));
        implementations.forEach(impl -> impl.build(classLoader, zipOutputStream));
    }
}
