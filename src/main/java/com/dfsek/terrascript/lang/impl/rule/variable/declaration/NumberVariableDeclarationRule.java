package com.dfsek.terrascript.lang.impl.rule.variable.declaration;

import com.dfsek.terrascript.lang.Rule;
import com.dfsek.terrascript.lang.RuleMatcher;
import com.dfsek.terrascript.lang.impl.operations.variable.declaration.NumberVariableDeclarationOperation;
import com.dfsek.terrascript.lang.impl.rule.variable.assignment.NumberVariableAssignmentRule;
import com.dfsek.terrascript.lang.internal.Operation;
import com.dfsek.terrascript.parser.Parser;
import com.dfsek.terrascript.parser.ParserUtil;
import com.dfsek.terrascript.parser.TokenView;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Token;
import com.dfsek.terrascript.tokenizer.Tokenizer;

public class NumberVariableDeclarationRule extends VariableDeclarationRule {
    @Override
    public Operation assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        ParserUtil.checkType(tokenizer.consume(), Token.Type.NUMBER_VARIABLE);
        ParserUtil.checkType(tokenizer.peek(), Token.Type.IDENTIFIER);
        Operation varDec = new NumberVariableDeclarationOperation(tokenizer.peek());
        parser.getBuilder().addOperation(parser.expect((initial, view) -> new NumberVariableAssignmentRule()));
        ParserUtil.checkType(tokenizer.consume(), Token.Type.STATEMENT_END);
        return varDec;
    }
}
