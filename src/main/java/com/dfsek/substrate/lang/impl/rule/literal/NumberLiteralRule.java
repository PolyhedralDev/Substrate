package com.dfsek.substrate.lang.impl.rule.literal;

import com.dfsek.substrate.lang.impl.operations.literal.NumberLiteralOperation;
import com.dfsek.substrate.lang.internal.Operation;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

public class NumberLiteralRule extends LiteralRule {
    @Override
    public Operation assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        Token num = tokenizer.consume();
        ParserUtil.checkType(num, Token.Type.NUMBER);
        return new NumberLiteralOperation(Double.parseDouble(num.getContent()), num.getPosition());
    }
}
