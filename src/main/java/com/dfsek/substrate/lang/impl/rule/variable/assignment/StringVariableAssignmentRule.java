package com.dfsek.substrate.lang.impl.rule.variable.assignment;

import com.dfsek.substrate.lang.impl.operations.variable.assignment.StringVariableAssignmentOperation;
import com.dfsek.substrate.lang.impl.rule.ExpressionRule;
import com.dfsek.substrate.lang.internal.Operation;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

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
