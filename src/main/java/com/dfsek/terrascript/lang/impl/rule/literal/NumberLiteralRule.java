package com.dfsek.terrascript.lang.impl.rule.literal;

import com.dfsek.terrascript.lang.impl.operations.literal.NumberLiteralOperation;
import com.dfsek.terrascript.lang.internal.Operation;
import com.dfsek.terrascript.parser.Parser;
import com.dfsek.terrascript.parser.ParserUtil;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Token;
import com.dfsek.terrascript.tokenizer.Tokenizer;

public class NumberLiteralRule extends LiteralRule {
    @Override
    public Operation assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        ParserUtil.checkType(tokenizer.peek(), Token.Type.NUMBER);
        return new NumberLiteralOperation(Double.parseDouble(tokenizer.consume().getContent()));
    }
}
