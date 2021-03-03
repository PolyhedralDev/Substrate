package com.dfsek.terrascript.lang;

import com.dfsek.terrascript.lang.internal.Operation;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Tokenizer;

/**
 * A parser rule.
 */
public interface Rule {
    Operation assemble(Tokenizer tokenizer) throws ParseException;
}
