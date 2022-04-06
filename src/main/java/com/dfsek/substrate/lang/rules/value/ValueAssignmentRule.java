package com.dfsek.substrate.lang.rules.value;

import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.value.ValueAssignmentNode;
import com.dfsek.substrate.lang.rules.expression.ExpressionRule;
import com.dfsek.substrate.lexer.Lexer;
import com.dfsek.substrate.lexer.token.Token;
import com.dfsek.substrate.lexer.token.TokenType;
import com.dfsek.substrate.parser.ParserScope;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;

public class ValueAssignmentRule {
    public static ValueAssignmentNode assemble(Lexer lexer, ParseData data, ParserScope scope) throws ParseException {
        Token id = ParserUtil.checkType(lexer.consume(), TokenType.IDENTIFIER);
        ParserUtil.checkType(lexer.consume(), TokenType.ASSIGNMENT); // next token should be =
        ExpressionNode value = ExpressionRule.assemble(lexer, data, scope, id.getContent());
        if (scope.contains(id.getContent())) {
            throw new ParseException("Value \"" + id.getContent() + "\" already exists in this scope.", id.getPosition());
        }
        scope.register(id.getContent(), value.reference());
        return new ValueAssignmentNode(id, value);
    }
}
