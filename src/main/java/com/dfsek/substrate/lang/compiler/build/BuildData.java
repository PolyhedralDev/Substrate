package com.dfsek.substrate.lang.compiler.build;

import com.dfsek.substrate.lang.compiler.api.Macro;
import com.dfsek.substrate.lang.compiler.codegen.LambdaFactory;
import com.dfsek.substrate.lang.compiler.codegen.TupleFactory;
import com.dfsek.substrate.lang.compiler.codegen.ops.ClassBuilder;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.parser.DynamicClassLoader;
import io.vavr.collection.List;

import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipOutputStream;

public class BuildData {
    private final TupleFactory tupleFactory;
    private final LambdaFactory lambdaFactory;
    private final Map<String, Macro> macros;

    private final DynamicClassLoader classLoader;
    private final ClassBuilder classWriter;

    private final String name;
    private final int implArgsOffset;
    private int offset;

    public BuildData(DynamicClassLoader classLoader, ClassBuilder classWriter, ZipOutputStream zipOutputStream, List<Class<? extends Record>> tuples) {
        this.classLoader = classLoader;
        this.classWriter = classWriter;
        tupleFactory = new TupleFactory(classLoader, classWriter, zipOutputStream, tuples);
        lambdaFactory = new LambdaFactory(classLoader, tupleFactory, classWriter, zipOutputStream);
        this.name = classWriter.getName();
        this.offset = 3;
        this.implArgsOffset = 2;
        this.macros = new HashMap<>();
    }

    private BuildData(DynamicClassLoader classLoader,
                      ClassBuilder classWriter,
                      TupleFactory tupleFactory,
                      LambdaFactory lambdaFactory,
                      Map<String, Macro> macros,
                      String name, int offset, int implArgsOffset) {
        this.classLoader = classLoader;
        this.classWriter = classWriter;
        this.tupleFactory = tupleFactory;
        this.lambdaFactory = lambdaFactory;
        this.macros = macros;
        this.offset = offset;
        this.name = name;
        this.implArgsOffset = implArgsOffset;
    }

    public LambdaFactory lambdaFactory() {
        return lambdaFactory;
    }

    public TupleFactory tupleFactory() {
        return tupleFactory;
    }

    public String getClassName() {
        return name;
    }

    public int offsetInc(int offset) {
        int o = this.offset;
        this.offset += offset;
        return o;
    }

    public int getOffset() {
        return offset;
    }

    public void registerMacro(String id, Macro macro) {
        macros.put(id, macro);
    }

    public BuildData sub() {
        return new BuildData(classLoader,
                classWriter,
                tupleFactory,
                lambdaFactory,
                // new scope
                macros,  // but same JVM scope
                name, offset, implArgsOffset);
    }

    public BuildData sub(ClassBuilder classWriter) {
        return new BuildData(classLoader,
                classWriter,
                tupleFactory,
                lambdaFactory,
                // new scope
                macros,
                classWriter.getName(), 2, 1);
    }

    public int getImplArgsOffset() {
        return implArgsOffset;
    }
}
