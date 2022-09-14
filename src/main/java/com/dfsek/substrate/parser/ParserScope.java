package com.dfsek.substrate.parser;

import com.dfsek.substrate.lang.compiler.type.Signature;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;


public class ParserScope {
    private final Map<String, Signature> values;

    public ParserScope(Map<String, Signature> values) {
        this.values = values;
    }

    public ParserScope() {
        this(HashMap.empty());
    }

    public ParserScope register(String val, Signature signature) {
        if (values.containsKey(val))
            throw new IllegalArgumentException("Value " + val + " already exists in this scope.");
        return new ParserScope(values.put(val, signature));
    }

    public boolean contains(String val) {
        return values.containsKey(val);
    }

    public Option<Signature> get(String val) {
        return values.get(val);
    }
}
