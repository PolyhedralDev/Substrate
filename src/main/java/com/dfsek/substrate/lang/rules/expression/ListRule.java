package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.list.ListNode;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.List;

public class ListRule implements Rule {
    private static final ListRule INSTANCE = new ListRule();
    @Override
    public ExpressionNode assemble(Tokenizer tokenizer, ParseData data) throws ParseException {
        Token op = ParserUtil.checkType(tokenizer.consume(), Token.Type.LIST_BEGIN);

        List<ExpressionNode> elements = new ArrayList<>();

        while(tokenizer.peek().getType() != Token.Type.LIST_END) {
            elements.add(ExpressionRule.getInstance().assemble(tokenizer, data));
            if (ParserUtil.checkType(tokenizer.peek(), Token.Type.SEPARATOR, Token.Type.LIST_END, Token.Type.STATEMENT_END, Token.Type.GROUP_BEGIN).getType() == Token.Type.SEPARATOR) {
                tokenizer.consume(); // consume separator
            }
        }

        ParserUtil.checkType(tokenizer.consume(), Token.Type.LIST_END);


        return new ListNode(elements, op.getPosition());
    }

    public static ListRule getInstance() {
        return INSTANCE;
    }
}
