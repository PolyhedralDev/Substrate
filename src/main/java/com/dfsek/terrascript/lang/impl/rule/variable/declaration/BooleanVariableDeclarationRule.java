package com.dfsek.terrascript.lang.impl.rule.variable.declaration;

import com.dfsek.terrascript.lang.impl.operations.variable.declaration.BooleanVariableDeclarationOperation;
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
        ParserUtil.checkType(tokenizer.peek(), Token.Type.IDENTIFIER);
        //parser.getBuilder().addOperation(parser.expect((initial, view) -> new BooleanVariableAssignmentRule()));
        //ParserUtil.checkType(tokenizer.consume(), Token.Type.STATEMENT_END);
        return new BooleanVariableDeclarationOperation(tokenizer.peek());
    }
}
