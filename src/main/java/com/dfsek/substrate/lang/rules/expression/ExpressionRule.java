package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.rules.FunctionInvocationRule;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

public class ExpressionRule implements Rule {
    private static final ExpressionRule INSTANCE = new ExpressionRule();
    @Override
    public ExpressionNode assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        Token test = tokenizer.peek();
        if(test.isConstant() || test.isIdentifier()) { // simple expression
            if(tokenizer.peek(1).getType() == Token.Type.GROUP_BEGIN) {
                return FunctionInvocationRule.getInstance().assemble(tokenizer, parser);
            } else {
                return BasicExpressionRule.getInstance().assemble(tokenizer, parser);
            }
        }
        // Tuple or lambda expression
        if(tokenizer.peek(1).isIdentifier()) { // lambda
            return LambdaExpressionRule.getInstance().assemble(tokenizer, parser);
        }
        return TupleRule.getInstance().assemble(tokenizer, parser);
    }

    public static ExpressionRule getInstance() {
        return INSTANCE;
    }
}
