package com.dfsek.substrate.lang.rules;

import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.internal.Operation;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Tokenizer;

public class BlockRule implements Rule {
    @Override
    public Operation assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        return null;
    }
}
