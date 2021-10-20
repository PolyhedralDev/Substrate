package com.dfsek.substrate.lang.compiler.build;

import com.dfsek.substrate.lang.compiler.lambda.LambdaFactory;
import com.dfsek.substrate.lang.compiler.tuple.TupleFactory;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.parser.DynamicClassLoader;
import com.dfsek.substrate.util.pair.ImmutablePair;
import org.objectweb.asm.ClassWriter;

import java.util.HashMap;
import java.util.Map;

public class BuildData {
    private final TupleFactory tupleFactory;
    private final LambdaFactory lambdaFactory;

    private final Map<String, Value> values;
    private final Map<ImmutablePair<BuildData, String>, Integer> valueOffsets;

    private final BuildData parent;
    private int offset = 2;

    private final DynamicClassLoader classLoader;

    private final ClassWriter classWriter;

    private final ValueInterceptor interceptor;

    public BuildData(DynamicClassLoader classLoader, ClassWriter classWriter) {
        this.classLoader = classLoader;
        this.classWriter = classWriter;
        tupleFactory = new TupleFactory(classLoader);
        lambdaFactory = new LambdaFactory(classLoader, tupleFactory);
        values = new HashMap<>();
        valueOffsets = new HashMap<>();
        parent = null;
        interceptor = (a, b) -> {
        };
    }

    public int getOffset() {
        return offset;
    }

    private BuildData(DynamicClassLoader classLoader,
                      ClassWriter classWriter,
                      TupleFactory tupleFactory,
                      LambdaFactory lambdaFactory,
                      Map<String, Value> values,
                      Map<ImmutablePair<BuildData, String>, Integer> valueOffsets,
                      BuildData parent,
                      ValueInterceptor interceptor) {
        this.classLoader = classLoader;
        this.classWriter = classWriter;
        this.tupleFactory = tupleFactory;
        this.lambdaFactory = lambdaFactory;
        this.values = values;
        this.valueOffsets = valueOffsets;
        this.parent = parent;
        this.interceptor = interceptor;
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
        valueOffsets.put(ImmutablePair.of(this, id), offset);
        interceptor.register(id, value);
    }

    protected Map<String, Value> getValues() {
        return values;
    }

    public void registerValue(String id, Value value, int frames) {
        registerValue(id, value);
        offset += frames;
    }

    public Value getValue(String id) {
        if (!values.containsKey(id)) throw new IllegalArgumentException("No such value \"" + id + "\"");
        return values.get(id);
    }

    public int offset(String id) {
        if (!values.containsKey(id)) throw new IllegalArgumentException("No such value \"" + id + "\"");

        BuildData test = this;
        while (!valueOffsets.containsKey(ImmutablePair.of(test, id))) test = test.parent;

        return valueOffsets.get(ImmutablePair.of(test, id));
    }

    public boolean valueExists(String id) {
        return values.containsKey(id);
    }

    public ClassWriter getClassWriter() {
        return classWriter;
    }

    public BuildData sub() {
        return new BuildData(classLoader,
                classWriter,
                tupleFactory,
                lambdaFactory,
                new HashMap<>(values), // new scope
                valueOffsets, // but same JVM scope
                this,
                (a, b) -> {
                });
    }

    public BuildData detach(ValueInterceptor interceptor) {
        return new BuildData(classLoader,
                classWriter,
                tupleFactory,
                lambdaFactory,
                new HashMap<>(values), // new scope
                new HashMap<>(), // *and* different JVM scope
                this,
                interceptor);
    }
}
