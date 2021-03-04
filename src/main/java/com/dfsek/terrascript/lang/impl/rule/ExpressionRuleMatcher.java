package com.dfsek.terrascript.lang.impl.rule;

import com.dfsek.terrascript.lang.Rule;
import com.dfsek.terrascript.lang.RuleMatcher;
import com.dfsek.terrascript.lang.impl.rule.literal.BooleanLiteralRule;
import com.dfsek.terrascript.lang.impl.rule.literal.NumberLiteralRule;
import com.dfsek.terrascript.lang.impl.rule.literal.StringLiteralRule;
import com.dfsek.terrascript.parser.TokenView;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Token;

public class ExpressionRuleMatcher implements RuleMatcher {
    @Override
    public Rule match(Token initial, TokenView view) throws ParseException {
        switch (initial.getType()) {
            case STRING: return new StringLiteralRule();
            case BOOLEAN: return new BooleanLiteralRule();
            case NUMBER: return new NumberLiteralRule();
            default:throw new ParseException("Unexpected token: " + initial, initial.getPosition());
        }
    }
}
