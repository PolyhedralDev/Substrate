package com.dfsek.substrate.lang.impl.rule.variable.declaration;

import com.dfsek.substrate.lang.impl.operations.variable.declaration.NumberVariableDeclarationOperation;
import com.dfsek.substrate.lang.impl.rule.variable.assignment.NumberVariableAssignmentRule;
import com.dfsek.substrate.lang.internal.Operation;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

public class NumberVariableDeclarationRule extends VariableDeclarationRule {
    @Override
    public Operation assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        ParserUtil.checkType(tokenizer.consume(), Token.Type.NUMBER_VARIABLE);
        Token identifier = tokenizer.peek();
        ParserUtil.checkType(identifier, Token.Type.IDENTIFIER);
        Operation value = null;
        if(tokenizer.peek(1).getType() == Token.Type.ASSIGNMENT) {
            value = new NumberVariableAssignmentRule().assemble(tokenizer, parser);
        } else tokenizer.consume();
        return new NumberVariableDeclarationOperation(identifier, identifier.getPosition(), value);
    }
}
