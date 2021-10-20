package com.dfsek.substrate.lang.compiler;

import com.dfsek.substrate.parser.DynamicClassLoader;
import org.objectweb.asm.ClassWriter;

import java.util.HashMap;
import java.util.Map;

public class BuildData {
    private final TupleFactory tupleFactory;
    private final LambdaFactory lambdaFactory;

    private final Map<String, Value> values = new HashMap<>();
    private final Map<String, Integer> valueOffsets = new HashMap<>();
    private int offset = 2;

    private final DynamicClassLoader classLoader;

    private final ClassWriter classWriter;

    public BuildData(DynamicClassLoader classLoader, ClassWriter classWriter) {
        this.classLoader = classLoader;
        this.classWriter = classWriter;
        tupleFactory = new TupleFactory(classLoader);
        lambdaFactory = new LambdaFactory(classLoader, tupleFactory);
    }

    public LambdaFactory lambdaFactory() {
        return lambdaFactory;
    }

    public TupleFactory tupleFactory() {
        return tupleFactory;
    }

    public void registerValue(String id, Value value) {
        if(values.containsKey(id)) throw new IllegalArgumentException("Value with identifier \"" + id + "\" already registered.");
        values.put(id, value);
        valueOffsets.put(id, offset);
    }

    protected Map<String, Value> getValues() {
        return values;
    }

    public void registerValue(String id, Value value, int frames) {
        registerValue(id, value);
        offset+=frames;
    }

    public Value getValue(String id) {
        if(!values.containsKey(id)) throw new IllegalArgumentException("No such value \"" + id + "\"");
        return values.get(id);
    }

    public int offset(String id) {
        if(!values.containsKey(id)) throw new IllegalArgumentException("No such value \"" + id + "\"");
        return valueOffsets.get(id);
    }

    public boolean valueExists(String id) {
        return values.containsKey(id);
    }

    public ClassWriter getClassWriter() {
        return classWriter;
    }
}
