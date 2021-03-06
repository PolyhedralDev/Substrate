package com.dfsek.substrate.lang.impl.rule.match;

import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.RuleMatcher;
import com.dfsek.substrate.lang.impl.rule.literal.BooleanLiteralRule;
import com.dfsek.substrate.lang.impl.rule.literal.NumberLiteralRule;
import com.dfsek.substrate.lang.impl.rule.literal.StringLiteralRule;
import com.dfsek.substrate.lang.impl.rule.variable.VariableReferenceRule;
import com.dfsek.substrate.lang.internal.Operation;
import com.dfsek.substrate.parser.TokenView;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;

public class LiteralRuleMatcher implements RuleMatcher {
    private final Operation.ReturnType type;

    public LiteralRuleMatcher() {
        this.type = null;
    }

    public LiteralRuleMatcher(Operation.ReturnType req) {
        this.type = req;
    }

    @Override
    public Rule match(Token initial, TokenView view) throws ParseException {
        switch(initial.getType()) {
            case STRING:
                return new StringLiteralRule();
            case BOOLEAN:
                return new BooleanLiteralRule();
            case NUMBER:
                return new NumberLiteralRule();
            case IDENTIFIER:
                if(view.peek(1).getType() != Token.Type.GROUP_BEGIN) {
                    return new VariableReferenceRule(type);
                }
            default:
                throw new ParseException("Unexpected token: " + initial, initial.getPosition());
        }
    }
}
