package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.list.ListNode;
import com.dfsek.substrate.parser.ParserScope;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.lexer.token.Token;
import com.dfsek.substrate.lexer.token.TokenType;
import com.dfsek.substrate.lexer.Lexer;

import java.util.ArrayList;
import java.util.List;

public class ListRule implements Rule {
    private static final ListRule INSTANCE = new ListRule();

    @Override
    public ExpressionNode assemble(Lexer lexer, ParseData data, ParserScope scope) throws ParseException {
        Token op = ParserUtil.checkType(lexer.consume(), TokenType.LIST_BEGIN);

        List<ExpressionNode> elements = new ArrayList<>();

        while (lexer.peek().getType() != TokenType.LIST_END) {
            elements.add(ExpressionRule.getInstance().assemble(lexer, data, scope));
            if (ParserUtil.checkType(lexer.peek(), TokenType.SEPARATOR, TokenType.LIST_END, TokenType.STATEMENT_END, TokenType.GROUP_BEGIN).getType() == TokenType.SEPARATOR) {
                lexer.consume(); // consume separator
            }
        }

        ParserUtil.checkType(lexer.consume(), TokenType.LIST_END);


        return new ListNode(elements, op.getPosition());
    }

    public static ListRule getInstance() {
        return INSTANCE;
    }
}
