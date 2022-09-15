package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.list.ListNode;
import com.dfsek.substrate.lexer.Lexer;
import com.dfsek.substrate.lexer.token.Token;
import com.dfsek.substrate.lexer.token.TokenType;
import com.dfsek.substrate.parser.scope.ParserScope;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.List;

public class ListRule {
    public static Unchecked<ListNode> assemble(Lexer lexer, ParseData data, ParserScope scope) throws ParseException {
        Token op = ParserUtil.checkType(lexer.consume(), TokenType.LIST_BEGIN);

        List<Unchecked<? extends ExpressionNode>> elements = List.empty();

        while (lexer.peek().getType() != TokenType.LIST_END) {
            elements = elements.append(ExpressionRule.assemble(lexer, data, scope));
            if (ParserUtil.checkType(lexer.peek(), TokenType.SEPARATOR, TokenType.LIST_END, TokenType.STATEMENT_END, TokenType.GROUP_BEGIN).getType() == TokenType.SEPARATOR) {
                lexer.consume(); // consume separator
            }
        }

        ParserUtil.checkType(lexer.consume(), TokenType.LIST_END);


        return ListNode.of(elements, op.getPosition());
    }
}
