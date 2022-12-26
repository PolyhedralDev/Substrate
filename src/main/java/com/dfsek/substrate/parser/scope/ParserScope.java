package com.dfsek.substrate.parser.scope;

import com.dfsek.substrate.lang.compiler.value.Value;
import io.vavr.collection.HashMap;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;

import java.util.function.UnaryOperator;


public class ParserScope {
    private final LinkedHashMap<String, Value> values;

    private final int localWidth;

    private final UnaryOperator<Value> mapper;

    protected ParserScope(LinkedHashMap<String, Value> values, int localWidth, UnaryOperator<Value> mapper) {
        this.values = values;
        this.localWidth = localWidth;
        this.mapper = mapper;
    }

    public ParserScope() {
        this(LinkedHashMap.empty(), 3, UnaryOperator.identity());
    }

    public ParserScope register(String val, Value signature) {
        if (values.containsKey(val))
            throw new IllegalArgumentException("Value " + val + " already exists in this scope.");
        return new ParserScope(values.put(val, signature), localWidth + signature.getLVWidth(), mapper);
    }

    public Option<Value> get(String val) {
        return values.get(val);
    }

    public int getLocalWidth() {
        return localWidth;
    }

    public ParserScope lambda(int offset, UnaryOperator<Value> mapper) {
        return new ParserScope(values, offset, mapper);
    }

    public LinkedHashMap<String, Value> getValues() {
        return values;
    }
}
