package com.dfsek.substrate.parser;

import com.dfsek.substrate.Script;
import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.compiler.api.Function;
import com.dfsek.substrate.lang.compiler.api.Macro;
import com.dfsek.substrate.lang.compiler.api.StringUtils;
import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.compiler.codegen.ScriptBuilder;
import com.dfsek.substrate.lang.std.function.Curry;
import com.dfsek.substrate.lang.std.function.ForEach;
import com.dfsek.substrate.lang.std.function.Println;
import com.dfsek.substrate.lang.std.function.StaticFunction;
import com.dfsek.substrate.lexer.Lexer;
import com.dfsek.substrate.parser.exception.ParseException;

import java.util.LinkedHashMap;
import java.util.Map;

public class Parser {
    private final Lexer lexer;
    private final Rule base;
    private final ScriptBuilder builder = new ScriptBuilder();
    private final ParserScope scope = new ParserScope();

    private final ParseData data = new ParseData();

    private static final Map<String, StaticFunction> STATIC_FUNCTIONS = new LinkedHashMap<>();

    static {
        try {
            STATIC_FUNCTIONS.put("pow", new StaticFunction(Math.class.getMethod("pow", double.class, double.class)));
            STATIC_FUNCTIONS.put("sqrt", new StaticFunction(Math.class.getMethod("sqrt", double.class)));
            STATIC_FUNCTIONS.put("cbrt", new StaticFunction(Math.class.getMethod("cbrt", double.class)));

            STATIC_FUNCTIONS.put("abs", new StaticFunction(Math.class.getMethod("abs", int.class)));

            STATIC_FUNCTIONS.put("sin", new StaticFunction(Math.class.getMethod("sin", double.class)));
            STATIC_FUNCTIONS.put("cos", new StaticFunction(Math.class.getMethod("cos", double.class)));
            STATIC_FUNCTIONS.put("tan", new StaticFunction(Math.class.getMethod("tan", double.class)));


            STATIC_FUNCTIONS.put("substring", new StaticFunction(StringUtils.class.getMethod("substring", String.class, int.class, int.class)));
            STATIC_FUNCTIONS.put("upperCase", new StaticFunction(StringUtils.class.getMethod("toUpperCase", String.class)));
            STATIC_FUNCTIONS.put("lowerCase", new StaticFunction(StringUtils.class.getMethod("toLowerCase", String.class)));
            STATIC_FUNCTIONS.put("startsWith", new StaticFunction(StringUtils.class.getMethod("startsWith", String.class, String.class)));
            STATIC_FUNCTIONS.put("endsWith", new StaticFunction(StringUtils.class.getMethod("endsWith", String.class, String.class)));
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    public Parser(String data, Rule base) throws ParseException {
        lexer = new Lexer(data);
        registerFunction("println", new Println());
        registerMacro("forEach", new ForEach());
        registerMacro("curry", new Curry());

        STATIC_FUNCTIONS.forEach(this::registerFunction);

        this.base = base;
    }

    public Script parse() throws ParseException {
        while (lexer.hasNext()) {
            builder.addOperation(base.assemble(lexer, data, scope));
        }
        return builder.build(data);
    }

    public void registerMacro(String id, Macro macro) {
        builder.registerMacro(id, macro);
        data.registerMacro(id, macro);
    }

    public void registerFunction(String id, Function function) {
        builder.registerFunction(id, function);
        scope.register(id, function.reference());
    }
}
