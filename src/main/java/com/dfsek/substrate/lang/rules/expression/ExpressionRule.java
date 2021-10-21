package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.binary.AdditionNode;
import com.dfsek.substrate.lang.rules.FunctionInvocationRule;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

public class ExpressionRule implements Rule {
    private static final ExpressionRule INSTANCE = new ExpressionRule();

    public static ExpressionRule getInstance() {
        return INSTANCE;
    }

    @Override
    public ExpressionNode assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        Token test = tokenizer.peek();

        ExpressionNode node;

        if (test.isConstant() || test.isIdentifier()) { // simple expression
            if (tokenizer.peek(1).getType() == Token.Type.GROUP_BEGIN) {
                node = FunctionInvocationRule.getInstance().assemble(tokenizer, parser);
            } else {
                node = BasicExpressionRule.getInstance().assemble(tokenizer, parser);
            }
        } else if(test.isType()) {
            node = CastRule.getInstance().assemble(tokenizer, parser);
        } else if (tokenizer.peek(1).isIdentifier() && tokenizer.peek(2).getType() == Token.Type.TYPE) { // lambda or function
            node = LambdaExpressionRule.getInstance().assemble(tokenizer, parser);
        } else {
            node = TupleRule.getInstance().assemble(tokenizer, parser);
        }

        if(tokenizer.peek().isBinaryOperator()) {
            ExpressionNode left = node;
            Token op = tokenizer.consume();
            ExpressionNode right = assemble(tokenizer, parser);
            if(op.getType() == Token.Type.ADDITION_OPERATOR) {
                node = new AdditionNode(left, right, op);
            }
        }

        return node;
    }
}
