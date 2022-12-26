package com.dfsek.substrate.parser;

import com.dfsek.substrate.Script;
import com.dfsek.substrate.lang.compiler.api.MathUtils;
import com.dfsek.substrate.lang.compiler.api.StringUtils;
import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.compiler.codegen.ScriptBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.value.RecordValue;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.rules.BaseRule;
import com.dfsek.substrate.lang.std.function.StaticFunction;
import com.dfsek.substrate.lexer.Lexer;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.parser.scope.ParserScope;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;

public class Parser<P extends Record, R extends Record> {
    private static final Map<String, StaticFunction> STATIC_FUNCTIONS;

    static {
        try {
            STATIC_FUNCTIONS = LinkedHashMap.of("pow", new StaticFunction(Math.class.getMethod("pow", double.class, double.class)))
                    .put("intPow", new StaticFunction(MathUtils.class.getMethod("intPow", double.class, double.class)))
                    .put("pow2", new StaticFunction(MathUtils.class.getMethod("pow2", double.class)))

                    .put("sqrt", new StaticFunction(Math.class.getMethod("sqrt", double.class)))
                    .put("cbrt", new StaticFunction(Math.class.getMethod("cbrt", double.class)))

                    .put("exp", new StaticFunction(Math.class.getMethod("exp", double.class)))
                    .put("ln", new StaticFunction(Math.class.getMethod("log", double.class)))
                    .put("log", new StaticFunction(Math.class.getMethod("log10", double.class)))

                    .put("absInt", new StaticFunction(Math.class.getMethod("abs", int.class)))
                    .put("absNum", new StaticFunction(Math.class.getMethod("abs", double.class)))

                    .put("sin", new StaticFunction(Math.class.getMethod("sin", double.class)))
                    .put("cos", new StaticFunction(Math.class.getMethod("cos", double.class)))
                    .put("tan", new StaticFunction(Math.class.getMethod("tan", double.class)))

                    .put("sinh", new StaticFunction(Math.class.getMethod("sinh", double.class)))
                    .put("cosh", new StaticFunction(Math.class.getMethod("cosh", double.class)))
                    .put("tanh", new StaticFunction(Math.class.getMethod("tanh", double.class)))

                    .put("asin", new StaticFunction(Math.class.getMethod("asin", double.class)))
                    .put("acos", new StaticFunction(Math.class.getMethod("acos", double.class)))
                    .put("atan", new StaticFunction(Math.class.getMethod("atan", double.class)))
                    .put("atan2", new StaticFunction(Math.class.getMethod("atan2", double.class, double.class)))

                    .put("rad", new StaticFunction(Math.class.getMethod("toRadians", double.class)))
                    .put("deg", new StaticFunction(Math.class.getMethod("toDegrees", double.class)))


                    .put("floor", new StaticFunction(MathUtils.class.getMethod("fastFloor", double.class)))
                    .put("ceil", new StaticFunction(MathUtils.class.getMethod("fastCeil", double.class)))

                    .put("max", new StaticFunction(Math.class.getMethod("max", double.class, double.class)))
                    .put("intMax", new StaticFunction(Math.class.getMethod("max", int.class, int.class)))
                    .put("min", new StaticFunction(Math.class.getMethod("min", double.class, double.class)))
                    .put("intMin", new StaticFunction(Math.class.getMethod("min", int.class, int.class)))

                    .put("substring", new StaticFunction(StringUtils.class.getMethod("substring", String.class, int.class, int.class)))
                    .put("upperCase", new StaticFunction(StringUtils.class.getMethod("toUpperCase", String.class)))
                    .put("lowerCase", new StaticFunction(StringUtils.class.getMethod("toLowerCase", String.class)))
                    .put("startsWith", new StaticFunction(StringUtils.class.getMethod("startsWith", String.class, String.class)))
                    .put("endsWith", new StaticFunction(StringUtils.class.getMethod("endsWith", String.class, String.class)));

        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    private final Class<P> parameters;
    private final Class<R> ret;

    public Parser(Class<P> parameters, Class<R> ret) throws ParseException {
        this.parameters = parameters;
        this.ret = ret;
    }

    public Script<P, R> parse(String data) throws ParseException {
        ParseData parseData = new ParseData(parameters, ret);
        ParserScope scope = List.of(parameters.getRecordComponents())
                .zipWithIndex()
                .foldLeft(new ParserScope(), ((parserScope, recordComponent) -> parserScope.register(recordComponent._1.getName(), new RecordValue(Signature.fromType(recordComponent._1.getType()), parameters, recordComponent._2))));
        Lexer lexer = new Lexer(data);
        ExpressionNode node = BaseRule.assemble(lexer, parseData, scope).get(Signature.fromRecord(ret));
        System.out.println(node);
        return ScriptBuilder.build(parseData, node, List.empty(), scope);
    }
}
