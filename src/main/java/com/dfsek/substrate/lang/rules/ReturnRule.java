package com.dfsek.substrate.lang.rules;

import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.ReturnNode;
import com.dfsek.substrate.lang.rules.expression.ExpressionRule;
import com.dfsek.substrate.lexer.Lexer;
import com.dfsek.substrate.lexer.token.Token;
import com.dfsek.substrate.lexer.token.TokenType;
import com.dfsek.substrate.parser.ParserScope;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;

public class ReturnRule {
    public static ReturnNode assemble(Lexer lexer, ParseData data, ParserScope scope) throws ParseException {
        Token r = ParserUtil.checkType(lexer.consume(), TokenType.RETURN);

        ReturnNode node;
        if (lexer.peek().getType() == TokenType.STATEMENT_END) {
            node = new ReturnNode(r.getPosition(), null);
        } else {
            ExpressionNode expressionNode = ExpressionRule.assemble(lexer, data, scope);
            node = new ReturnNode(r.getPosition(), expressionNode);
        }

        ParserUtil.checkType(lexer.consume(), TokenType.STATEMENT_END);
        return node;
    }
}
