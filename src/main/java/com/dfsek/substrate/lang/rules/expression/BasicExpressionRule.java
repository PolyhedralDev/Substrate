package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.node.expression.ValueReferenceNode;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.constant.BooleanNode;
import com.dfsek.substrate.lang.node.expression.constant.DecimalNode;
import com.dfsek.substrate.lang.node.expression.constant.IntegerNode;
import com.dfsek.substrate.lang.node.expression.constant.StringNode;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

public class BasicExpressionRule implements Rule {
    private static final BasicExpressionRule INSTANCE = new BasicExpressionRule();

    public static BasicExpressionRule getInstance() {
        return INSTANCE;
    }

    @Override
    public ExpressionNode assemble(Tokenizer tokenizer) throws ParseException {
        ParserUtil.checkType(tokenizer.peek(), Token.Type.IDENTIFIER, Token.Type.STRING, Token.Type.BOOLEAN, Token.Type.NUMBER, Token.Type.INT);
        if (tokenizer.peek().getType() == Token.Type.STRING) {
            return new StringNode(tokenizer.consume());
        } else if (tokenizer.peek().getType() == Token.Type.BOOLEAN) {
            return new BooleanNode(tokenizer.consume());
        } else if (tokenizer.peek().getType() == Token.Type.NUMBER) {
            return new DecimalNode(tokenizer.consume());
        } else if (tokenizer.peek().getType() == Token.Type.INT) {
            return new IntegerNode(tokenizer.consume());
        } else {
            ParserUtil.checkType(tokenizer.peek(), Token.Type.IDENTIFIER);
            return new ValueReferenceNode(tokenizer.consume());
        }
    }
}
