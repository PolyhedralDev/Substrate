package com.dfsek.terrascript.lang.impl.rule;

import com.dfsek.terrascript.lang.Rule;
import com.dfsek.terrascript.lang.impl.operations.IdOperation;
import com.dfsek.terrascript.lang.impl.rule.literal.StringLiteralRule;
import com.dfsek.terrascript.lang.internal.Operation;
import com.dfsek.terrascript.parser.Parser;
import com.dfsek.terrascript.parser.ParserUtil;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Token;
import com.dfsek.terrascript.tokenizer.Tokenizer;

public class IdRule implements Rule {
    private static final StringLiteralRule STRING_LITERAL_RULE = new StringLiteralRule();
    @Override
    public Operation assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        ParserUtil.checkType(tokenizer.consume(), Token.Type.ID);
        IdOperation operation = new IdOperation(STRING_LITERAL_RULE.assemble(tokenizer, parser).getLiteral());
        ParserUtil.checkType(tokenizer.consume(), Token.Type.STATEMENT_END);
        return operation;
    }
}
