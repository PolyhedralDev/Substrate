package com.dfsek.substrate.lang.rules;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Tokenizer;

public class ValueAssignmentRule implements Rule {
    @Override
    public Node assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        return null;
    }
}
