package com.dfsek.terrascript.lang.impl.rule.variable.assignment;

import com.dfsek.terrascript.lang.impl.operations.variable.assignment.StringVariableAssignmentOperation;
import com.dfsek.terrascript.lang.impl.rule.ExpressionRule;
import com.dfsek.terrascript.lang.internal.Operation;
import com.dfsek.terrascript.parser.Parser;
import com.dfsek.terrascript.parser.ParserUtil;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Token;
import com.dfsek.terrascript.tokenizer.Tokenizer;

public class StringVariableAssignmentRule extends VariableAssignmentRule {
    @Override
    public Operation assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        Token id = tokenizer.consume();
        ParserUtil.checkType(id, Token.Type.IDENTIFIER);

        Token assignment = tokenizer.consume();

        ParserUtil.checkType(assignment, Token.Type.ASSIGNMENT);
        return new StringVariableAssignmentOperation(new ExpressionRule().assemble(tokenizer, parser), id.getContent(), assignment.getPosition());
    }
}
