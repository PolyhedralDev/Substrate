package com.dfsek.substrate.lang.compiler.build;

import com.dfsek.substrate.lang.compiler.api.Macro;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;

import java.util.*;
import java.util.function.Consumer;

public class ParseData {
    private final Map<String, Macro> macros = new HashMap<>();
    private final List<Consumer<BuildData>> assertions = new ArrayList<>();

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
        assertions.add(data -> ParserUtil.checkReferenceType(typed, expected));
        return typed;
    }

    @SafeVarargs
    public final <T extends ExpressionNode> T assertEqual(T typed, T... others) throws ParseException {
        assertions.add(data -> Arrays.stream(others).forEach(node -> ParserUtil.checkReferenceType(typed, node.reference())));
        return typed;
    }

    public List<Consumer<BuildData>> getAssertions() {
        return assertions;
    }
}
