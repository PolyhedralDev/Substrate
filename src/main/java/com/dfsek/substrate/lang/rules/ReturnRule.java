package com.dfsek.substrate.lang.rules;

import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.node.ReturnNode;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.rules.expression.ExpressionRule;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

public class ReturnRule implements Rule {
    private static final ReturnRule INSTANCE = new ReturnRule();
    @Override
    public ReturnNode assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        Token r = ParserUtil.checkType(tokenizer.consume(), Token.Type.RETURN);

        ReturnNode node;
        if(tokenizer.peek().getType() == Token.Type.STATEMENT_END) {
            node = new ReturnNode(r.getPosition(), null);
        } else {
            ExpressionNode expressionNode = ExpressionRule.getInstance().assemble(tokenizer, parser);
            node = new ReturnNode(r.getPosition(), expressionNode);
        }

        ParserUtil.checkType(tokenizer.consume(), Token.Type.STATEMENT_END);
        return node;
    }

    public static ReturnRule getInstance() {
        return INSTANCE;
    }
}
