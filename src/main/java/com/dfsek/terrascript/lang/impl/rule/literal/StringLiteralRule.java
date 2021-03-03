package com.dfsek.terrascript.lang.impl.rule.literal;

import com.dfsek.terrascript.lang.Rule;
import com.dfsek.terrascript.lang.impl.operations.literal.StringLiteralOperation;
import com.dfsek.terrascript.lang.internal.Operation;
import com.dfsek.terrascript.parser.ParserUtil;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Token;
import com.dfsek.terrascript.tokenizer.Tokenizer;

public class StringLiteralRule implements Rule {
    @Override
    public StringLiteralOperation assemble(Tokenizer tokenizer) throws ParseException {
        ParserUtil.checkType(tokenizer.peek(), Token.Type.STRING);
        return new StringLiteralOperation(tokenizer.consume().getContent());
    }
}
