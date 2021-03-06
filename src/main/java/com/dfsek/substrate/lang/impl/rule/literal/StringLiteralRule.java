package com.dfsek.substrate.lang.impl.rule.literal;

import com.dfsek.substrate.lang.impl.operations.literal.StringLiteralOperation;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

public class StringLiteralRule extends LiteralRule {
    @Override
    public StringLiteralOperation assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        Token string = tokenizer.consume();
        ParserUtil.checkType(string, Token.Type.STRING);
        return new StringLiteralOperation(string.getContent(), string.getPosition());
    }
}
