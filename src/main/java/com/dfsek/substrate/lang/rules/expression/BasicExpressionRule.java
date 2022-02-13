package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.constant.BooleanNode;
import com.dfsek.substrate.lang.node.expression.constant.DecimalNode;
import com.dfsek.substrate.lang.node.expression.constant.IntegerNode;
import com.dfsek.substrate.lang.node.expression.constant.StringNode;
import com.dfsek.substrate.lang.node.expression.value.ValueReferenceNode;
import com.dfsek.substrate.lang.rules.value.ValueAssignmentRule;
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
    public ExpressionNode assemble(Tokenizer tokenizer, ParseData data) throws ParseException {
        ParserUtil.checkType(tokenizer.peek(), Token.Type.IDENTIFIER, Token.Type.STRING, Token.Type.BOOLEAN, Token.Type.NUMBER, Token.Type.INT);
        if (tokenizer.peek().getType() == Token.Type.STRING) {
            Token token = tokenizer.consume();
            return new StringNode(token.getContent(), token.getPosition());
        } else if (tokenizer.peek().getType() == Token.Type.BOOLEAN) {
            Token token = tokenizer.consume();
            return new BooleanNode(Boolean.parseBoolean(token.getContent()), token.getPosition());
        } else if (tokenizer.peek().getType() == Token.Type.NUMBER) {
            Token token = tokenizer.consume();
            return new DecimalNode(Double.parseDouble(token.getContent()), token.getPosition());
        } else if (tokenizer.peek().getType() == Token.Type.INT) {
            Token token = tokenizer.consume();
            return new IntegerNode(Integer.parseInt(token.getContent()), token.getPosition());
        } else {
            ParserUtil.checkType(tokenizer.peek(), Token.Type.IDENTIFIER);
            if (tokenizer.peek(1).getType() == Token.Type.ASSIGNMENT) {
                return ValueAssignmentRule.getInstance().assemble(tokenizer, data);
            }
            return new ValueReferenceNode(tokenizer.consume());
        }
    }
}
