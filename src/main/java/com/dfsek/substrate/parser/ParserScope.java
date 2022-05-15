package com.dfsek.substrate.parser;

import com.dfsek.substrate.lang.compiler.type.Signature;
import io.vavr.control.Option;

import java.util.HashMap;
import java.util.Map;

public class ParserScope {
    private static final ParserScope NULL = new ParserScope() {
        @Override
        public void register(String val, Signature signature) {
            throw new IllegalArgumentException();
        }

        @Override
        public boolean contains(String val) {
            return false;
        }

        @Override
        public Option<Signature> get(String val) {
            return Option.none();
        }
    };
    private final Map<String, Signature> values = new HashMap<>();
    private final ParserScope parent;

    public ParserScope() {
        this.parent = NULL;
    }

    public ParserScope(ParserScope parent) {
        this.parent = parent;
    }

    public void register(String val, Signature signature) {
        if (values.containsKey(val) || parent.values.containsKey(val))
            throw new IllegalArgumentException("Value " + val + " already exists in this scope.");
        values.put(val, signature);
    }

    public boolean contains(String val) {
        return values.containsKey(val) || parent.contains(val);
    }

    public Option<Signature> get(String val) {
        if (values.containsKey(val)) return Option.of(values.get(val));
        return parent.get(val);
    }

    public ParserScope sub() {
        return new ParserScope(this);
    }
}
