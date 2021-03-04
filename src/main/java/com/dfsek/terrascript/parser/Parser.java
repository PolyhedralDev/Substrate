package com.dfsek.terrascript.parser;

import com.dfsek.terrascript.TerraScript;
import com.dfsek.terrascript.lang.RuleMatcher;
import com.dfsek.terrascript.lang.ScriptBuilder;
import com.dfsek.terrascript.lang.internal.Operation;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Token;
import com.dfsek.terrascript.tokenizer.Tokenizer;

import java.util.HashMap;
import java.util.Map;

public class Parser {
    private final Map<Token.Type, RuleMatcher> ruleMatcherMap = new HashMap<>();
    private final Tokenizer tokenizer;
    private RuleMatcher start;
    private final ScriptBuilder builder = new ScriptBuilder();

    public Parser(String data) throws ParseException {
        tokenizer = new Tokenizer(data);
    }

    public TerraScript parse() throws ParseException {
        if(start != null) {
            builder.addOperation(expect(start));
        }
        while(tokenizer.hasNext()) {
            Token current = tokenizer.peek();
            System.out.println(current);
            if(!ruleMatcherMap.containsKey(current.getType())) throw new ParseException("Unexpected token: " + current, current.getPosition());
            builder.addOperation(expect(ruleMatcherMap.get(current.getType())));
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

    public ScriptBuilder getBuilder() {
        return builder;
    }
}
