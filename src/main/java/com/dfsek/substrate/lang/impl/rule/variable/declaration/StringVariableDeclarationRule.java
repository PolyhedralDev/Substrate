package com.dfsek.substrate.lang.impl.rule.variable.declaration;

import com.dfsek.substrate.lang.impl.operations.variable.declaration.StringVariableDeclarationOperation;
import com.dfsek.substrate.lang.impl.rule.variable.assignment.StringVariableAssignmentRule;
import com.dfsek.substrate.lang.internal.Operation;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

public class StringVariableDeclarationRule extends VariableDeclarationRule {
    @Override
    public Operation assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        ParserUtil.checkType(tokenizer.consume(), Token.Type.STRING_VARIABLE);
        Token identifier = tokenizer.peek();
        ParserUtil.checkType(identifier, Token.Type.IDENTIFIER);

        Operation value = null;
        if(tokenizer.peek(1).getType() == Token.Type.ASSIGNMENT) {
            value = new StringVariableAssignmentRule().assemble(tokenizer, parser);
        } else tokenizer.consume();
        return new StringVariableDeclarationOperation(identifier, identifier.getPosition(), value);
    }
}
