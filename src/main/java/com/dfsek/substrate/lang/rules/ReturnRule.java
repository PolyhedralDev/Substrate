package com.dfsek.substrate.lang.rules;

import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.ReturnNode;
import com.dfsek.substrate.lang.rules.expression.ExpressionRule;
import com.dfsek.substrate.parser.ParserScope;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.lexer.token.Token;
import com.dfsek.substrate.lexer.token.TokenType;
import com.dfsek.substrate.lexer.Lexer;

public class ReturnRule implements Rule {
    private static final ReturnRule INSTANCE = new ReturnRule();

    public static ReturnRule getInstance() {
        return INSTANCE;
    }

    @Override
    public ReturnNode assemble(Lexer lexer, ParseData data, ParserScope scope) throws ParseException {
        Token r = ParserUtil.checkType(lexer.consume(), TokenType.RETURN);

        ReturnNode node;
        if (lexer.peek().getType() == TokenType.STATEMENT_END) {
            node = new ReturnNode(r.getPosition(), null);
        } else {
            ExpressionNode expressionNode = ExpressionRule.getInstance().assemble(lexer, data, scope);
            node = new ReturnNode(r.getPosition(), expressionNode);
        }

        ParserUtil.checkType(lexer.consume(), TokenType.STATEMENT_END);
        return node;
    }
}
