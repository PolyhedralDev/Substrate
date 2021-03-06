package com.dfsek.substrate.lang.impl.rule;

import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.impl.operations.IdOperation;
import com.dfsek.substrate.lang.impl.rule.literal.StringLiteralRule;
import com.dfsek.substrate.lang.internal.Operation;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

public class IdRule implements Rule {
    private static final StringLiteralRule STRING_LITERAL_RULE = new StringLiteralRule();
    @Override
    public Operation assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        Token id = tokenizer.consume();
        ParserUtil.checkType(id, Token.Type.ID);
        IdOperation operation = new IdOperation(STRING_LITERAL_RULE.assemble(tokenizer, parser).getLiteral(), id.getPosition());
        ParserUtil.checkType(tokenizer.consume(), Token.Type.STATEMENT_END);
        return operation;
    }
}
