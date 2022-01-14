package com.dfsek.substrate.lang.compiler.build;

import com.dfsek.substrate.lang.compiler.api.Macro;
import com.dfsek.substrate.lang.compiler.codegen.LambdaFactory;
import com.dfsek.substrate.lang.compiler.codegen.TupleFactory;
import com.dfsek.substrate.lang.compiler.codegen.ops.ClassBuilder;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.parser.DynamicClassLoader;
import com.dfsek.substrate.util.Lazy;
import com.dfsek.substrate.util.pair.Pair;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class BuildData {
    private final TupleFactory tupleFactory;
    private final LambdaFactory lambdaFactory;

    private final Map<String, Value> values;
    private final Map<String, Macro> macros;
    private final Map<Pair<BuildData, String>, Integer> valueOffsets;

    private final BuildData parent;
    private final DynamicClassLoader classLoader;
    private final ClassBuilder classWriter;
    private final ValueInterceptor interceptor;

    private final String name;
    private int offset;

    private final int implArgsOffset;

    public BuildData(DynamicClassLoader classLoader, ClassBuilder classWriter) {
        this.classLoader = classLoader;
        this.classWriter = classWriter;
        tupleFactory = new TupleFactory(classLoader);
        lambdaFactory = new LambdaFactory(classLoader, tupleFactory);
        this.name = classWriter.getName();
        values = new HashMap<>();
        valueOffsets = new HashMap<>();
        parent = null;
        interceptor = (a, b) -> {
        };
        this.offset = 2;
        this.implArgsOffset = 1;
        this.macros = new HashMap<>();
    }

    private BuildData(DynamicClassLoader classLoader,
                      ClassBuilder classWriter,
                      TupleFactory tupleFactory,
                      LambdaFactory lambdaFactory,
                      Map<String, Value> values,
                      Map<String, Macro> macros, Map<Pair<BuildData, String>, Integer> valueOffsets,
                      BuildData parent,
                      ValueInterceptor interceptor,
                      String name, int offset, int implArgsOffset) {
        this.classLoader = classLoader;
        this.classWriter = classWriter;
        this.tupleFactory = tupleFactory;
        this.lambdaFactory = lambdaFactory;
        this.values = values;
        this.macros = macros;
        this.valueOffsets = valueOffsets;
        this.parent = parent;
        this.interceptor = interceptor;
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
        valueOffsets.put(Pair.of(this, id), offset);
    }

    public void registerUnchecked(String id, Value value) {
        values.put(id, value);
        valueOffsets.put(Pair.of(this, id), offset);
    }

    public String getClassName() {
        return name;
    }

    public void registerValue(String id, Value value, int frames) {
        registerValue(id, value);
        offset += frames;
    }

    public void offsetInc(int offset) {
        this.offset += offset;
    }

    public int getOffset() {
        return offset;
    }

    public boolean hasOffset(String id) {
        if (!values.containsKey(id)) throw new IllegalArgumentException("No such value \"" + id + "\"");

        BuildData test = this;
        while (!valueOffsets.containsKey(Pair.of(test, id))) {
            if (test == null) return false;
            test = test.parent;
        }

        return true;
    }

    public Value getValue(String id) {
        interceptor.fetch(id, this);
        if (!values.containsKey(id)) {
            throw new IllegalArgumentException("No such value \"" + id + "\": " + values);
        }
        return values.get(id);
    }

    public void registerMacro(String id, Macro macro) {
        macros.put(id, macro);
    }

    public int offset(String id) {
        interceptor.fetch(id, this);
        if (!values.containsKey(id)) throw new IllegalArgumentException("No such value \"" + id + "\"");

        BuildData test = this;
        while (!valueOffsets.containsKey(Pair.of(test, id))) test = test.parent;

        return valueOffsets.get(Pair.of(test, id));
    }

    public boolean valueExists(String id) {
        interceptor.fetch(id, this);
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
                interceptor,
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
                interceptor,
                classWriter.getName(), 0, 1);
    }

    public void loadImplementationArguments(MethodBuilder visitor) {
        visitor.aLoad(implArgsOffset);
    }
}
