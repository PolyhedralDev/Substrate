package com.dfsek.substrate.lang.compiler.codegen;

import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.codegen.ops.Access;
import com.dfsek.substrate.lang.compiler.codegen.ops.ClassBuilder;
import com.dfsek.substrate.lang.compiler.type.DataType;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.internal.Tuple;
import com.dfsek.substrate.parser.DynamicClassLoader;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Stream;
import io.vavr.control.Either;
import org.objectweb.asm.*;

import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import static org.objectweb.asm.Opcodes.*;

/**
 * Generates records
 */
public class TupleFactory {
    private static final String TUPLE_NAME = CompilerUtil.internalName(Tuple.class);
    private final Map<Signature, IntrinsifiedTuple> generated = new HashMap<>();
    private final DynamicClassLoader classLoader;
    private final ClassBuilder classBuilder;
    private final ZipOutputStream zipOutputStream;

    public TupleFactory(DynamicClassLoader classLoader, ClassBuilder classBuilder, ZipOutputStream zipOutputStream, List<Class<? extends Record>> tuples) {
        this.classLoader = classLoader;
        this.classBuilder = classBuilder;
        this.zipOutputStream = zipOutputStream;
        tuples.map(TupleFactory::intrinsify).forEach(tuple -> generated.put(tuple.signature(), tuple));
    }

    private static IntrinsifiedTuple intrinsify(Class<? extends Record> record) {
        return new IntrinsifiedTuple(
                Signature.fromRecord(record),
                record,
                Stream.ofAll(Arrays.stream(record.getRecordComponents())).map(RecordComponent::getName).toList()
        );
    }

    public Either<CompileError, Op> get(Signature args, int index) {
        IntrinsifiedTuple generate = generate(args);
        return Op.invokeVirtual(CompilerUtil.internalName(generate.clazz()),
                "param" + index,
                "()" + args.getType(index).descriptor());
    }

    public List<Either<CompileError, Op>> construct(Signature argSignature, List<Either<CompileError, Op>> args) {
        Class<?> tuple = generate(argSignature).clazz();
        String tupleName = CompilerUtil.internalName(tuple);
        return List.of(Op.newInsn(tupleName))
                .appendAll(args)
                .append(Op.invokeSpecial(tupleName, "<init>", "(" + argSignature.internalDescriptor() + ")V"));
    }

    public IntrinsifiedTuple generate(Signature args) {
        return generated.computeIfAbsent(args, ignore -> {
            String endName = "TupleIM_" + args.classDescriptor();
            String name = classBuilder.getName() + "$" + endName;
            classBuilder.inner(name, classBuilder.getName(), endName, Access.PRIVATE, Access.STATIC, Access.FINAL);

            ClassWriter writer = CompilerUtil.generateClass(name, Classes.RECORD, false, TUPLE_NAME);


            String constructorSig = "(" + args.internalDescriptor() + ")V";

            MethodVisitor constructor = writer.visitMethod(ACC_PUBLIC,
                    "<init>", // Constructor method name is <init>
                    constructorSig,
                    null,
                    null);

            constructor.visitCode();
            constructor.visitVarInsn(ALOAD, 0); // Put this reference on stack
            constructor.visitMethodInsn(INVOKESPECIAL, // Invoke Object super constructor
                    "java/lang/Object",
                    "<init>",
                    "()V",
                    false);

            List<Tuple2<String, String>> params = List.empty();

            int offset = 1;
            for (int i = 0; i < args.size(); i++) {
                String param = "param" + i;
                DataType argType = args.getType(i);
                params = params.append(new Tuple2<>(param, argType.descriptor()));

                writer.visitRecordComponent(param, argType.descriptor(), null).visitEnd();

                writer.visitField(ACC_PRIVATE | ACC_FINAL,
                        param,
                        argType.descriptor(),
                        null,
                        null);
                constructor.visitVarInsn(ALOAD, 0);
                constructor.visitVarInsn(argType.loadInsn(), offset++);

                if (argType == DataType.NUM) { // double takes up 2 frames
                    offset++;
                }

                constructor.visitFieldInsn(PUTFIELD, name, param, argType.descriptor());


                MethodVisitor paramGetter = writer.visitMethod(ACC_PUBLIC | ACC_FINAL,
                        param, // Constructor method name is <init>
                        "()" + argType.descriptor(),
                        null,
                        null);
                paramGetter.visitCode();
                paramGetter.visitVarInsn(ALOAD, 0);
                paramGetter.visitFieldInsn(GETFIELD, name, param, argType.descriptor());
                paramGetter.visitInsn(argType.returnInsn());
                paramGetter.visitMaxs(0, 0);
            }

            generateRecordBoilerplate(writer, name, params);


            constructor.visitInsn(RETURN); // Void return
            constructor.visitMaxs(0, 0); // Set stack and local variable size (bogus values; handled automatically by ASM)


            byte[] bytes = writer.toByteArray();
            // noinspection unchecked
            Class<? extends Record> clazz = (Class<? extends Record>) classLoader.defineClass(name.replace('/', '.'), bytes);
            CompilerUtil.dump(name, bytes, zipOutputStream);
            return new IntrinsifiedTuple(args, clazz, params.map(Tuple2::_1));
        });
    }

    private static void generateRecordBoilerplate(ClassWriter writer, String name, List<Tuple2<String, String>> params) { // record intrinsic crap
        Object[] bootstrapArgs = List.of(Type.getType("L" + name + ";"),
                        params.foldLeft("", (a, b) -> a + b._1)
                        )
                .appendAll(params.map(t -> new Handle(
                        H_GETFIELD,
                        name,
                        t._1,
                        t._2,
                        false
                ))).toJavaArray();

        writer.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC | ACC_FINAL | ACC_STATIC);

        MethodVisitor toString = writer.visitMethod(ACC_PUBLIC | ACC_FINAL, "toString", "()Ljava/lang/String;", null, null);
        toString.visitCode();
        toString.visitVarInsn(ALOAD, 0);
        toString.visitInvokeDynamicInsn(
                "toString",
                "(L" + name + ";)Ljava/lang/String;",
                new Handle(
                        Opcodes.H_INVOKESTATIC,
                        "java/lang/runtime/ObjectMethods",
                        "bootstrap",
                        "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/TypeDescriptor;Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/invoke/MethodHandle;)Ljava/lang/Object;",
                        false
                ),
                bootstrapArgs
        );

        toString.visitInsn(ARETURN);
        toString.visitMaxs(0, 0);
        toString.visitEnd();

        MethodVisitor hashCode = writer.visitMethod(ACC_PUBLIC | ACC_FINAL, "hashCode", "()I", null, null);
        hashCode.visitCode();
        hashCode.visitVarInsn(ALOAD, 0);
        hashCode.visitInvokeDynamicInsn(
                "hashCode",
                "(L" + name + ";)I",
                new Handle(
                        Opcodes.H_INVOKESTATIC,
                        "java/lang/runtime/ObjectMethods",
                        "bootstrap",
                        "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/TypeDescriptor;Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/invoke/MethodHandle;)Ljava/lang/Object;",
                        false
                ),
                bootstrapArgs
        );
        hashCode.visitInsn(IRETURN);
        hashCode.visitMaxs(0, 0);
        hashCode.visitEnd();

        MethodVisitor equals = writer.visitMethod(ACC_PUBLIC | ACC_FINAL, "equals", "(Ljava/lang/Object;)Z", null, null);
        equals.visitCode();
        equals.visitVarInsn(ALOAD, 0);
        equals.visitVarInsn(ALOAD, 1);
        equals.visitInvokeDynamicInsn(
                "equals",
                "(L" + name + ";Ljava/lang/Object;)Z",
                new Handle(
                        Opcodes.H_INVOKESTATIC,
                        "java/lang/runtime/ObjectMethods",
                        "bootstrap",
                        "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/TypeDescriptor;Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/invoke/MethodHandle;)Ljava/lang/Object;",
                        false),
                bootstrapArgs
        );
        equals.visitInsn(IRETURN);
        equals.visitMaxs(0, 0);
        equals.visitEnd();

    }

    public record IntrinsifiedTuple(Signature signature, Class<? extends Record> clazz, List<String> params) {

    }
}
