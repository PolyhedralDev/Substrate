package com.dfsek.substrate.parser;

import com.dfsek.substrate.Script;
import com.dfsek.substrate.lang.RuleMatcher;
import com.dfsek.substrate.lang.ScriptBuilder;
import com.dfsek.substrate.lang.internal.Operation;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

import java.util.HashMap;
import java.util.Map;

public class Parser {
    private final Map<Token.Type, RuleMatcher> ruleMatcherMap = new HashMap<>();
    private final Tokenizer tokenizer;
    private RuleMatcher start;
    private RuleMatcher def;
    private final ScriptBuilder builder = new ScriptBuilder();

    public Parser(String data) throws ParseException {
        tokenizer = new Tokenizer(data);
    }

    public Script parse() throws ParseException {
        if(start != null) {
            builder.addOperation(expect(start));
        }
        while(tokenizer.hasNext()) {
            Token current = tokenizer.peek();
            if(!ruleMatcherMap.containsKey(current.getType())) {
                builder.addOperation(expect(def));
            } else {
                builder.addOperation(expect(ruleMatcherMap.get(current.getType())));
            }
        }
        return builder.build();
    }

    public void addRule(Token.Type token, RuleMatcher matcher) {
        ruleMatcherMap.put(token, matcher);
    }

    public void expectStart(RuleMatcher start) {
        this.start = start;
    }

    public Operation expect(RuleMatcher matcher) throws ParseException {
        Token current = tokenizer.peek();
        return matcher.match(current, new TokenView(tokenizer)).assemble(tokenizer, this);
    }

    public void expectDefault(RuleMatcher def) {
        this.def = def;
    }

    public ScriptBuilder getBuilder() {
        return builder;
    }
}
