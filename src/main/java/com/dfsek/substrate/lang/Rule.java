package com.dfsek.substrate.lang;

import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Tokenizer;

/**
 * A parser rule.
 */
public interface Rule {
    Node assemble(Tokenizer tokenizer, Parser parser) throws ParseException;
}
