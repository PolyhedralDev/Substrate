package com.dfsek.terrascript.lang.impl.rule.variable.assignment;

import com.dfsek.terrascript.lang.impl.operations.variable.assignment.NumberVariableAssignmentOperation;
import com.dfsek.terrascript.lang.impl.rule.ExpressionRuleMatcher;
import com.dfsek.terrascript.lang.internal.Operation;
import com.dfsek.terrascript.parser.Parser;
import com.dfsek.terrascript.parser.ParserUtil;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Token;
import com.dfsek.terrascript.tokenizer.Tokenizer;

public class NumberVariableAssignmentRule extends VariableAssignmentRule {
    @Override
    public Operation assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        Token id = tokenizer.consume();
        ParserUtil.checkType(id, Token.Type.IDENTIFIER);
        ParserUtil.checkType(tokenizer.consume(), Token.Type.ASSIGNMENT);
        return new NumberVariableAssignmentOperation(parser.expect(new ExpressionRuleMatcher()));
    }
}
