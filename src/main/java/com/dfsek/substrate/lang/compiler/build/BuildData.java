package com.dfsek.substrate.lang.compiler.build;

import com.dfsek.substrate.lang.compiler.codegen.LambdaFactory;
import com.dfsek.substrate.lang.compiler.codegen.TupleFactory;
import com.dfsek.substrate.lang.compiler.codegen.ops.ClassBuilder;
import com.dfsek.substrate.parser.DynamicClassLoader;
import io.vavr.collection.List;

import java.util.zip.ZipOutputStream;

public class BuildData {
    private final TupleFactory tupleFactory;
    private final LambdaFactory lambdaFactory;

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

    public int getImplArgsOffset() {
        return implArgsOffset;
    }
}
