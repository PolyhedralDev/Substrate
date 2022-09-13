package com.dfsek.substrate.lang.rules;

import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.rules.expression.ExpressionRule;
import com.dfsek.substrate.lexer.Lexer;
import com.dfsek.substrate.parser.ParserScope;
import com.dfsek.substrate.parser.exception.ParseException;

public class BaseRule {
    public static Unchecked<? extends ExpressionNode> assemble(Lexer lexer, ParseData data, ParserScope scope) throws ParseException {
        return ExpressionRule.assemble(lexer, data, scope);
    }
}
