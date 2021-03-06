package com.dfsek.substrate.lang.impl.rule.match;

import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.RuleMatcher;
import com.dfsek.substrate.lang.impl.rule.variable.assignment.BooleanVariableAssignmentRule;
import com.dfsek.substrate.lang.impl.rule.variable.assignment.NumberVariableAssignmentRule;
import com.dfsek.substrate.lang.impl.rule.variable.assignment.StringVariableAssignmentRule;
import com.dfsek.substrate.parser.TokenView;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;

public class IdentifierRuleMatcher implements RuleMatcher {
    @Override
    public Rule match(Token initial, TokenView view) throws ParseException {
        switch (view.peek(1).getType()) {
            case ASSIGNMENT:
                switch (view.peek(2).getType()) {
                    case NUMBER: return new NumberVariableAssignmentRule();
                    case STRING: return new StringVariableAssignmentRule();
                    case BOOLEAN: return new BooleanVariableAssignmentRule();
                }
        }
        throw new IllegalArgumentException();
    }
}
