package com.dfsek.terrascript.lang;

import com.dfsek.terrascript.parser.TokenView;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Token;
import com.dfsek.terrascript.tokenizer.Tokenizer;

public interface RuleMatcher {
    Rule match(Token initial, TokenView view) throws ParseException;
}
