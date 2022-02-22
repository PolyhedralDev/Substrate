package com.dfsek.substrate.lang;

import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.parser.ParserScope;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.lexer.Lexer;

/**
 * A parser rule.
 */
public interface Rule {
    Node assemble(Lexer lexer, ParseData data, ParserScope scope) throws ParseException;
}
