package com.dfsek.terrascript.lang.impl.rule.match;

import com.dfsek.terrascript.lang.Rule;
import com.dfsek.terrascript.lang.RuleMatcher;
import com.dfsek.terrascript.lang.impl.rule.StatementRule;
import com.dfsek.terrascript.lang.impl.rule.literal.BooleanLiteralRule;
import com.dfsek.terrascript.lang.impl.rule.literal.NumberLiteralRule;
import com.dfsek.terrascript.lang.impl.rule.literal.StringLiteralRule;
import com.dfsek.terrascript.lang.impl.rule.looplike.IfStatementRule;
import com.dfsek.terrascript.lang.impl.rule.looplike.WhileLoopRule;
import com.dfsek.terrascript.lang.impl.rule.variable.declaration.BooleanVariableDeclarationRule;
import com.dfsek.terrascript.lang.impl.rule.variable.declaration.NumberVariableDeclarationRule;
import com.dfsek.terrascript.lang.impl.rule.variable.declaration.StringVariableDeclarationRule;
import com.dfsek.terrascript.parser.TokenView;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Token;

public class ScriptRuleMatcher implements RuleMatcher {
    @Override
    public Rule match(Token initial, TokenView view) throws ParseException {
        switch(initial.getType()) {
            case IF_STATEMENT:
                return new IfStatementRule();
            case WHILE_LOOP:
                return new WhileLoopRule();
            case STRING_VARIABLE:
                return new StringVariableDeclarationRule();
            case NUMBER_VARIABLE:
                return new NumberVariableDeclarationRule();
            case BOOLEAN_VARIABLE:
                return new BooleanVariableDeclarationRule();
        }
        if(view.peek(1).getType() == Token.Type.ASSIGNMENT) {
            return new IdentifierRuleMatcher().match(initial, view);
        }
        throw new ParseException("Unexpected token: " + initial, initial.getPosition());
    }
}
