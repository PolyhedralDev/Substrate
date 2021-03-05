package com.dfsek.terrascript.lang.impl.rule.variable.declaration;

import com.dfsek.terrascript.lang.impl.operations.variable.declaration.BooleanVariableDeclarationOperation;
import com.dfsek.terrascript.lang.impl.operations.variable.declaration.StringVariableDeclarationOperation;
import com.dfsek.terrascript.lang.impl.rule.match.ExpressionRuleMatcher;
import com.dfsek.terrascript.lang.impl.rule.variable.assignment.BooleanVariableAssignmentRule;
import com.dfsek.terrascript.lang.internal.Operation;
import com.dfsek.terrascript.parser.Parser;
import com.dfsek.terrascript.parser.ParserUtil;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Token;
import com.dfsek.terrascript.tokenizer.Tokenizer;

public class BooleanVariableDeclarationRule extends VariableDeclarationRule{
    @Override
    public Operation assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        ParserUtil.checkType(tokenizer.consume(), Token.Type.BOOLEAN_VARIABLE);

        Token identifier = tokenizer.peek();
        ParserUtil.checkType(identifier, Token.Type.IDENTIFIER);
        Operation value = null;
        if(tokenizer.peek(1).getType() == Token.Type.ASSIGNMENT) {
            value = new BooleanVariableAssignmentRule().assemble(tokenizer, parser);
        } else tokenizer.consume();
        return new BooleanVariableDeclarationOperation(identifier, identifier.getPosition(), value);
    }
}
