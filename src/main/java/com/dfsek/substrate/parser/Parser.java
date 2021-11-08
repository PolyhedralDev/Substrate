package com.dfsek.substrate.parser;

import com.dfsek.substrate.Script;
import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ScriptBuilder;
import com.dfsek.substrate.lang.compiler.value.function.Function;
import com.dfsek.substrate.lang.std.function.ForEach;
import com.dfsek.substrate.lang.std.function.Println;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Tokenizer;

import java.util.function.Consumer;

public class Parser {
    private final Tokenizer tokenizer;
    private final Rule base;
    private final ScriptBuilder builder = new ScriptBuilder();

    public Parser(String data, Rule base) throws ParseException {
        tokenizer = new Tokenizer(data);
        builder.registerFunction("println", new Println());
        //builder.registerFunction(new ForEach());
        this.base = base;
    }

    public Script parse() throws ParseException {
        while (tokenizer.hasNext()) {
            builder.addOperation(base.assemble(tokenizer));
        }
        return builder.build();
    }

    public void registerMacro(Consumer<BuildData> buildDataConsumer) {
        builder.registerMacro(buildDataConsumer);
    }

    public void registerFunction(String id, Function function) {
        builder.registerFunction(id, function);
    }
}
