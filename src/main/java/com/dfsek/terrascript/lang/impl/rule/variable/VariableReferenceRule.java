package com.dfsek.terrascript.lang.impl.rule.variable;

import com.dfsek.terrascript.lang.Rule;
import com.dfsek.terrascript.lang.impl.operations.variable.VariableReferenceOperation;
import com.dfsek.terrascript.lang.internal.Operation;
import com.dfsek.terrascript.parser.Parser;
import com.dfsek.terrascript.parser.ParserUtil;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Token;
import com.dfsek.terrascript.tokenizer.Tokenizer;

public class VariableReferenceRule implements Rule {
    private final Operation.ReturnType type;

    public VariableReferenceRule(Operation.ReturnType type) {
        this.type = type;
    }

    @Override
    public Operation assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        Token variable = tokenizer.consume();
        ParserUtil.checkType(variable, Token.Type.IDENTIFIER);
        return new VariableReferenceOperation(variable.getPosition(), variable.getContent(), type);
    }
}
