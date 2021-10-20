package com.dfsek.substrate.lang.compiler;

import com.dfsek.substrate.parser.DynamicClassLoader;
import com.dfsek.substrate.util.ReflectionUtil;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

public class LambdaFactory {
    private final Map<Signature, Map<Signature, Class<?>>> generated = new HashMap<>();

    private final DynamicClassLoader classLoader;
    private final TupleFactory tupleFactory;

    public LambdaFactory(DynamicClassLoader classLoader, TupleFactory tupleFactory) {
        this.classLoader = classLoader;
        this.tupleFactory = tupleFactory;
    }

    public Class<?> generate(Signature args, Signature returnType) {
        return generated.computeIfAbsent(args, ignore -> new HashMap<>()).computeIfAbsent(returnType, ignore -> {
            ClassWriter writer = new ClassWriter(org.objectweb.asm.ClassWriter.COMPUTE_FRAMES + org.objectweb.asm.ClassWriter.COMPUTE_MAXS);
            String name = "com/dfsek/substrate/lang/internal/tuple/LambdaIMPL" + args.classDescriptor() + "$" + returnType.classDescriptor();

            writer.visit(V1_8,
                    ACC_PUBLIC | ACC_ABSTRACT | ACC_INTERFACE,
                    name,
                    null,
                    "java/lang/Object",
                    new String[]{"com/dfsek/substrate/lang/internal/Lambda"});


            String ret = returnType.internalDescriptor();

            if(!returnType.isSimple()) {
                ret = "L" + ReflectionUtil.internalName(tupleFactory.generate(returnType)) + ";";
            }

            MethodVisitor apply = writer.visitMethod(ACC_PUBLIC | ACC_ABSTRACT,
                    "apply",
                    "(" + args.internalDescriptor() + ")" + ret,
                    null,
                    null);
            apply.visitEnd();


            byte[] bytes = writer.toByteArray();

            if(true) {
                File dump = new File("./dumps/" + name + ".class");
                dump.getParentFile().mkdirs();
                System.out.println("Dumping to " + dump.getAbsolutePath());
                try {
                    IOUtils.write(bytes, new FileOutputStream(dump));
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }

            return classLoader.defineClass(name.replace('/', '.'), writer.toByteArray());
        });
    }
}
