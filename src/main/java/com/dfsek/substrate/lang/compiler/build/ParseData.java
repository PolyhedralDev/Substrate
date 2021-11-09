package com.dfsek.substrate.lang.compiler.build;

import com.dfsek.substrate.lang.compiler.api.Macro;
import com.dfsek.substrate.util.pair.Pair;

import java.util.HashMap;
import java.util.Map;

public class ParseData {
    private final Map<String, Macro> macros = new HashMap<>();

    public Macro getMacro(String id) {
        if(!macros.containsKey(id)) {
            throw new IllegalArgumentException("No such macro \"" + id + "\": " + macros);
        }
        return macros.get(id);
    }

    public boolean hasMacro(String id) {
        return macros.containsKey(id);
    }

    public void registerMacro(String id, Macro macro) {
        if (macros.containsKey(id))
            throw new IllegalArgumentException("Value with identifier \"" + id + "\" already registered.");
        macros.put(id, macro);
    }
}
