package com.dfsek.substrate.parser;

import com.dfsek.substrate.Script;
import com.dfsek.substrate.lang.compiler.api.Function;
import com.dfsek.substrate.lang.compiler.api.Macro;
import com.dfsek.substrate.lang.compiler.api.MathUtils;
import com.dfsek.substrate.lang.compiler.api.StringUtils;
import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.compiler.codegen.ScriptBuilder;
import com.dfsek.substrate.lang.rules.BaseRule;
import com.dfsek.substrate.lang.std.function.ForEach;
import com.dfsek.substrate.lang.std.function.Println;
import com.dfsek.substrate.lang.std.function.StaticFunction;
import com.dfsek.substrate.lexer.Lexer;
import com.dfsek.substrate.parser.exception.ParseException;

import java.util.LinkedHashMap;
import java.util.Map;

public class Parser {
    private static final Map<String, StaticFunction> STATIC_FUNCTIONS = new LinkedHashMap<>();

    static {
        try {
            STATIC_FUNCTIONS.put("pow", new StaticFunction(Math.class.getMethod("pow", double.class, double.class)));
            STATIC_FUNCTIONS.put("intPow", new StaticFunction(MathUtils.class.getMethod("intPow", double.class, double.class)));
            STATIC_FUNCTIONS.put("pow2", new StaticFunction(MathUtils.class.getMethod("pow2", double.class)));

            STATIC_FUNCTIONS.put("sqrt", new StaticFunction(Math.class.getMethod("sqrt", double.class)));
            STATIC_FUNCTIONS.put("cbrt", new StaticFunction(Math.class.getMethod("cbrt", double.class)));

            STATIC_FUNCTIONS.put("exp", new StaticFunction(Math.class.getMethod("exp", double.class)));
            STATIC_FUNCTIONS.put("ln", new StaticFunction(Math.class.getMethod("log", double.class)));
            STATIC_FUNCTIONS.put("log", new StaticFunction(Math.class.getMethod("log10", double.class)));

            STATIC_FUNCTIONS.put("abs", new StaticFunction(Math.class.getMethod("abs", int.class)));

            STATIC_FUNCTIONS.put("sin", new StaticFunction(Math.class.getMethod("sin", double.class)));
            STATIC_FUNCTIONS.put("cos", new StaticFunction(Math.class.getMethod("cos", double.class)));
            STATIC_FUNCTIONS.put("tan", new StaticFunction(Math.class.getMethod("tan", double.class)));

            STATIC_FUNCTIONS.put("sinh", new StaticFunction(Math.class.getMethod("sinh", double.class)));
            STATIC_FUNCTIONS.put("cosh", new StaticFunction(Math.class.getMethod("cosh", double.class)));
            STATIC_FUNCTIONS.put("tanh", new StaticFunction(Math.class.getMethod("tanh", double.class)));

            STATIC_FUNCTIONS.put("asin", new StaticFunction(Math.class.getMethod("asin", double.class)));
            STATIC_FUNCTIONS.put("acos", new StaticFunction(Math.class.getMethod("acos", double.class)));
            STATIC_FUNCTIONS.put("atan", new StaticFunction(Math.class.getMethod("atan", double.class)));
            STATIC_FUNCTIONS.put("atan2", new StaticFunction(Math.class.getMethod("atan2", double.class, double.class)));

            STATIC_FUNCTIONS.put("rad", new StaticFunction(Math.class.getMethod("toRadians", double.class)));
            STATIC_FUNCTIONS.put("deg", new StaticFunction(Math.class.getMethod("toDegrees", double.class)));


            STATIC_FUNCTIONS.put("floor", new StaticFunction(MathUtils.class.getMethod("fastFloor", double.class)));
            STATIC_FUNCTIONS.put("ceil", new StaticFunction(MathUtils.class.getMethod("fastCeil", double.class)));

            STATIC_FUNCTIONS.put("max", new StaticFunction(Math.class.getMethod("max", double.class, double.class)));
            STATIC_FUNCTIONS.put("intMax", new StaticFunction(Math.class.getMethod("max", int.class, int.class)));
            STATIC_FUNCTIONS.put("min", new StaticFunction(Math.class.getMethod("min", double.class, double.class)));
            STATIC_FUNCTIONS.put("intMin", new StaticFunction(Math.class.getMethod("min", int.class, int.class)));


            STATIC_FUNCTIONS.put("substring", new StaticFunction(StringUtils.class.getMethod("substring", String.class, int.class, int.class)));
            STATIC_FUNCTIONS.put("upperCase", new StaticFunction(StringUtils.class.getMethod("toUpperCase", String.class)));
            STATIC_FUNCTIONS.put("lowerCase", new StaticFunction(StringUtils.class.getMethod("toLowerCase", String.class)));
            STATIC_FUNCTIONS.put("startsWith", new StaticFunction(StringUtils.class.getMethod("startsWith", String.class, String.class)));
            STATIC_FUNCTIONS.put("endsWith", new StaticFunction(StringUtils.class.getMethod("endsWith", String.class, String.class)));
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    private final Lexer lexer;
    private final ScriptBuilder builder = new ScriptBuilder();
    private final ParserScope scope = new ParserScope();
    private final ParseData data = new ParseData();

    public Parser(String data) throws ParseException {
        lexer = new Lexer(data);
        registerFunction("println", new Println());
        registerMacro("forEach", new ForEach());

        STATIC_FUNCTIONS.forEach(this::registerFunction);
    }

    public <P extends Record, R extends Record> Script<P, R> parse(Class<P> parameters, Class<R> ret) throws ParseException {
        while (lexer.hasNext()) {
            builder.addOperation(BaseRule.assemble(lexer, data, scope));
        }
        return builder.build(data, parameters, ret);
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
