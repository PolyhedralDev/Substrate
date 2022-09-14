package com.dfsek.substrate.parser;

import com.dfsek.substrate.lang.compiler.value.Value;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;


public class ParserScope {
    private final Map<String, Value> values;

    private final int localWidth;

    public ParserScope(Map<String, Value> values, int localWidth) {
        this.values = values;
        this.localWidth = localWidth;
    }

    public ParserScope() {
        this(HashMap.empty(), 3);
    }

    public ParserScope register(String val, Value signature) {
        if (values.containsKey(val))
            throw new IllegalArgumentException("Value " + val + " already exists in this scope.");
        return new ParserScope(values.put(val, signature), localWidth + signature.getLVWidth());
    }

    public Option<Value> get(String val) {
        return values.get(val);
    }

    public int getLocalWidth() {
        return localWidth;
    }
}
