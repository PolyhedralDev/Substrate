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
import com.dfsek.substrate.parser.ParserScope;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.lexer.token.Token;
import com.dfsek.substrate.lexer.token.TokenType;
import com.dfsek.substrate.lexer.Lexer;

public class BasicExpressionRule implements Rule {
    private static final BasicExpressionRule INSTANCE = new BasicExpressionRule();

    public static BasicExpressionRule getInstance() {
        return INSTANCE;
    }

    @Override
    public ExpressionNode assemble(Lexer lexer, ParseData data, ParserScope scope) throws ParseException {
        ParserUtil.checkType(lexer.peek(), TokenType.IDENTIFIER, TokenType.STRING, TokenType.BOOLEAN, TokenType.NUMBER, TokenType.INT);
        if (lexer.peek().getType() == TokenType.STRING) {
            Token token = lexer.consume();
            return new StringNode(token.getContent(), token.getPosition());
        } else if (lexer.peek().getType() == TokenType.BOOLEAN) {
            Token token = lexer.consume();
            return new BooleanNode(Boolean.parseBoolean(token.getContent()), token.getPosition());
        } else if (lexer.peek().getType() == TokenType.NUMBER) {
            Token token = lexer.consume();
            return new DecimalNode(Double.parseDouble(token.getContent()), token.getPosition());
        } else if (lexer.peek().getType() == TokenType.INT) {
            Token token = lexer.consume();
            return new IntegerNode(Integer.parseInt(token.getContent()), token.getPosition());
        } else {
            ParserUtil.checkType(lexer.peek(), TokenType.IDENTIFIER);
            if (lexer.peek(1).getType() == TokenType.ASSIGNMENT) {
                return ValueAssignmentRule.getInstance().assemble(lexer, data, scope);
            }
            Token id = lexer.consume();
            if (!scope.contains(id.getContent())) {
                throw new ParseException("No such value: " + id.getContent(), id.getPosition());
            }
            return new ValueReferenceNode(id, scope.get(id.getContent()));
        }
    }
}
