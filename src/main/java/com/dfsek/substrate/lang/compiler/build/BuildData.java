package com.dfsek.substrate.lang.compiler.build;

import com.dfsek.substrate.lang.compiler.api.Macro;
import com.dfsek.substrate.lang.compiler.codegen.LambdaFactory;
import com.dfsek.substrate.lang.compiler.codegen.TupleFactory;
import com.dfsek.substrate.lang.compiler.codegen.ops.ClassBuilder;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.parser.DynamicClassLoader;
import io.vavr.Tuple2;
import io.vavr.collection.List;

import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipOutputStream;

public class BuildData {
    private final TupleFactory tupleFactory;
    private final LambdaFactory lambdaFactory;

    private final Map<String, Value> values;
    private final Map<String, Macro> macros;
    private final Map<Tuple2<BuildData, String>, Integer> valueOffsets;

    private final BuildData parent;
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
        values = new HashMap<>();
        valueOffsets = new HashMap<>();
        parent = null;
        this.offset = 3;
        this.implArgsOffset = 2;
        this.macros = new HashMap<>();
    }

    private BuildData(DynamicClassLoader classLoader,
                      ClassBuilder classWriter,
                      TupleFactory tupleFactory,
                      LambdaFactory lambdaFactory,
                      Map<String, Value> values,
                      Map<String, Macro> macros, Map<Tuple2<BuildData, String>, Integer> valueOffsets,
                      BuildData parent,
                      String name, int offset, int implArgsOffset) {
        this.classLoader = classLoader;
        this.classWriter = classWriter;
        this.tupleFactory = tupleFactory;
        this.lambdaFactory = lambdaFactory;
        this.values = values;
        this.macros = macros;
        this.valueOffsets = valueOffsets;
        this.parent = parent;
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

    public void registerValue(String id, Value value) {
        if (values.containsKey(id))
            throw new IllegalArgumentException("Value with identifier \"" + id + "\" already registered.");
        values.put(id, value);
        valueOffsets.put(new Tuple2<>(this, id), offset);
    }

    public void registerUnchecked(String id, Value value) {
        values.put(id, value);
        valueOffsets.put(new Tuple2<>(this, id), offset);
    }

    public String getClassName() {
        return name;
    }

    public void registerValue(String id, Value value, int frames) {
        registerValue(id, value);
        offset += frames;
    }

    public int offsetInc(int offset) {
        int o = this.offset;
        this.offset += offset;
        return o;
    }

    public int getOffset() {
        return offset;
    }

    public Value getValue(String id) {
        if (!values.containsKey(id)) {
            throw new IllegalArgumentException("No such value \"" + id + "\": " + values);
        }
        return values.get(id);
    }

    public void registerMacro(String id, Macro macro) {
        macros.put(id, macro);
    }

    public int offset(String id) {
        if (!values.containsKey(id)) throw new IllegalArgumentException("No such value \"" + id + "\"");

        BuildData test = this;
        while (!valueOffsets.containsKey(new Tuple2<>(test, id))) test = test.parent;

        return valueOffsets.get(new Tuple2<>(test, id));
    }

    public boolean valueExists(String id) {
        return values.containsKey(id) || macros.containsKey(id);
    }

    public BuildData sub() {
        return new BuildData(classLoader,
                classWriter,
                tupleFactory,
                lambdaFactory,
                new HashMap<>(values), // new scope
                macros, valueOffsets, // but same JVM scope
                this,
                name, offset, implArgsOffset);
    }

    public BuildData sub(ClassBuilder classWriter) {
        return new BuildData(classLoader,
                classWriter,
                tupleFactory,
                lambdaFactory,
                new HashMap<>(values), // new scope
                macros,
                new HashMap<>(),
                this,
                classWriter.getName(), 2, 1);
    }

    public int getImplArgsOffset() {
        return implArgsOffset;
    }
}
