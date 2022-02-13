package com.dfsek.substrate.lang.compiler.build;

import com.dfsek.substrate.lang.compiler.api.Macro;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Typed;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Positioned;
import com.dfsek.substrate.util.pair.Pair;

import java.util.*;

public class ParseData {
    private final Map<String, Macro> macros = new HashMap<>();
    private final List<Pair<ExpressionNode, Set<Signature>>> assertions = new ArrayList<>();

    public Macro getMacro(String id) {
        if (!macros.containsKey(id)) {
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

    public <T extends ExpressionNode> T checkType(T typed, Signature... expected) throws ParseException {
        assertions.add(Pair.of(typed, Set.of(expected)));
        return typed;
    }

    public List<Pair<ExpressionNode, Set<Signature>>> getAssertions() {
        return assertions;
    }
}
