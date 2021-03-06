package com.dfsek.substrate.lang.impl.rule.variable;

import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.impl.operations.variable.VariableReferenceOperation;
import com.dfsek.substrate.lang.internal.Operation;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

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
