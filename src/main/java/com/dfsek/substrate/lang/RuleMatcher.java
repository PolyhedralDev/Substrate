package com.dfsek.substrate.lang;

import com.dfsek.substrate.parser.TokenView;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;

public interface RuleMatcher {
    Rule match(Token initial, TokenView view) throws ParseException;
}
