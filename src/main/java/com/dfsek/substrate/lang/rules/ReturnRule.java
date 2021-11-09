package com.dfsek.substrate.lang.rules;

import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.node.expression.ReturnNode;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.rules.expression.ExpressionRule;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

public class ReturnRule implements Rule {
    private static final ReturnRule INSTANCE = new ReturnRule();

    public static ReturnRule getInstance() {
        return INSTANCE;
    }

    @Override
    public ReturnNode assemble(Tokenizer tokenizer, ParseData data) throws ParseException {
        Token r = ParserUtil.checkType(tokenizer.consume(), Token.Type.RETURN);

        ReturnNode node;
        if (tokenizer.peek().getType() == Token.Type.STATEMENT_END) {
            node = new ReturnNode(r.getPosition(), null);
        } else {
            ExpressionNode expressionNode = ExpressionRule.getInstance().assemble(tokenizer, data);
            node = new ReturnNode(r.getPosition(), expressionNode);
        }

        ParserUtil.checkType(tokenizer.consume(), Token.Type.STATEMENT_END);
        return node;
    }
}
