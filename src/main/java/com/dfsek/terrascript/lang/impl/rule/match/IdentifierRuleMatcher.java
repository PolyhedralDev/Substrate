package com.dfsek.terrascript.lang.impl.rule.match;

import com.dfsek.terrascript.lang.Rule;
import com.dfsek.terrascript.lang.RuleMatcher;
import com.dfsek.terrascript.lang.impl.rule.variable.assignment.BooleanVariableAssignmentRule;
import com.dfsek.terrascript.lang.impl.rule.variable.assignment.NumberVariableAssignmentRule;
import com.dfsek.terrascript.lang.impl.rule.variable.assignment.StringVariableAssignmentRule;
import com.dfsek.terrascript.parser.TokenView;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Token;

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
