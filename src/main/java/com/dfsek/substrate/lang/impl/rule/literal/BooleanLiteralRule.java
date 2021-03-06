package com.dfsek.substrate.lang.impl.rule.literal;

import com.dfsek.substrate.lang.impl.operations.literal.BooleanLiteralOperation;
import com.dfsek.substrate.lang.internal.Operation;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

public class BooleanLiteralRule extends LiteralRule {
    @Override
    public Operation assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        Token bool = tokenizer.consume();
        ParserUtil.checkType(bool, Token.Type.BOOLEAN);
        return new BooleanLiteralOperation(Boolean.parseBoolean(bool.getContent()), bool.getPosition());
    }
}
