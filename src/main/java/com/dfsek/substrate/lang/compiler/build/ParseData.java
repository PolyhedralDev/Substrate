package com.dfsek.substrate.lang.compiler.build;

import com.dfsek.substrate.lang.compiler.api.Macro;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.Tuple2;

import java.util.*;
import java.util.function.Consumer;

public class ParseData<P extends Record, R extends Record> {
    private final Map<String, Macro> macros = new HashMap<>();
    private final List<Consumer<BuildData>> assertions = new ArrayList<>();

    private final Signature returnType;
    private final io.vavr.collection.List<Tuple2<String, Signature>> args;

    private final Class<P> parameters;
    private final Class<R> ret;

    public ParseData(Class<P> parameters, Class<R> ret) {
        this.parameters = parameters;
        this.ret = ret;

        this.returnType = Signature.fromRecord(ret);
        this.args = io.vavr.collection.List
                .of(parameters.getRecordComponents())
                .map(r -> new Tuple2<>(r.getName(), Signature.fromClass(r.getType())))
                .toList();
    }

    public Macro getMacro(String id) {
        if (!macros.containsKey(id)) {
            throw new IllegalArgumentException("No such macro \"" + id + "\": " + macros);
        }
        return macros.get(id);
    }

    public io.vavr.collection.List<Tuple2<String, Signature>> getParameters() {
        return args;
    }

    public Signature getReturnType() {
        return returnType;
    }

    public Class<P> getParameterClass() {
        return parameters;
    }

    public Class<R> getReturnClass() {
        return ret;
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
