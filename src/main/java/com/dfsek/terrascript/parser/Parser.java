package com.dfsek.terrascript.parser;

import com.dfsek.terrascript.TerraScript;
import com.dfsek.terrascript.lang.Rule;
import com.dfsek.terrascript.lang.RuleMatcher;
import com.dfsek.terrascript.lang.ScriptBuilder;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Token;
import com.dfsek.terrascript.tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    private final Map<Token.Type, RuleMatcher> ruleMatcherMap = new HashMap<>();
    private final Tokenizer tokenizer;

    public Parser(String data) throws ParseException {
        tokenizer = new Tokenizer(data);
    }

    public TerraScript parse() throws ParseException {
        ScriptBuilder builder = new ScriptBuilder();
        while(tokenizer.hasNext()) {
            Token current = tokenizer.peek();
            builder.addOperation(ruleMatcherMap.get(current.getType()).match(current, new TokenView(tokenizer)).assemble(tokenizer));
        }
        return builder.build();
    }

    public void addRule(Token.Type token, RuleMatcher matcher) {
        ruleMatcherMap.put(token, matcher);
    }
}
