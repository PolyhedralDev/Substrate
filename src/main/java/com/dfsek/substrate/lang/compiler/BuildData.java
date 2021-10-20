package com.dfsek.substrate.lang.compiler;

import java.util.HashMap;
import java.util.Map;

public class BuildData {
    private final TupleFactory tupleFactory = new TupleFactory();
    private final LambdaFactory lambdaFactory = new LambdaFactory();

    private final Map<String, Value> values = new HashMap<>();

    public LambdaFactory lambdaFactory() {
        return lambdaFactory;
    }

    public TupleFactory tupleFactory() {
        return tupleFactory;
    }

    public void registerValue(String id, Value value) {
        if(values.containsKey(id)) throw new IllegalArgumentException("Value with identifier \"" + id + "\" already registered.");
        values.put(id, value);
    }

    public Value getValue(String id) {
        if(!values.containsKey(id)) throw new IllegalArgumentException("No such value \"" + id + "\"");
        return values.get(id);
    }

    public boolean valueExists(String id) {
        return values.containsKey(id);
    }
}
