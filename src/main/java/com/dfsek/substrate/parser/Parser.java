package com.dfsek.substrate.parser;

import com.dfsek.substrate.Script;
import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.compiler.build.ScriptBuilder;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Tokenizer;

public class Parser {
    private final Tokenizer tokenizer;
    private final Rule base;
    private final ScriptBuilder builder = new ScriptBuilder();

    public Parser(String data, Rule base) throws ParseException {
        tokenizer = new Tokenizer(data);
        this.base = base;
    }

    public Script parse() throws ParseException {
        while (tokenizer.hasNext()) {
            builder.addOperation(base.assemble(tokenizer, this));
        }
        return builder.build();
    }
}
