package com.dfsek.terrascript.lang.impl.rule.literal;

import com.dfsek.terrascript.lang.internal.Operation;
import com.dfsek.terrascript.parser.Parser;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Tokenizer;

public class BooleanLiteralRule extends LiteralRule {
    @Override
    public Operation assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        return null;
    }
}
