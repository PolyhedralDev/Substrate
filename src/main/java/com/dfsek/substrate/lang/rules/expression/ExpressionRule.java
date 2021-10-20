package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.node.ValueReferenceNode;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.constant.BooleanNode;
import com.dfsek.substrate.lang.node.expression.constant.DecimalNode;
import com.dfsek.substrate.lang.node.expression.constant.StringNode;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

public class ExpressionRule implements Rule {
    @Override
    public ExpressionNode assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        Token test = tokenizer.peek();
        if(test.getType() == Token.Type.STRING) {
            return new StringNode(tokenizer.consume());
        } else if(test.getType() == Token.Type.BOOLEAN) {
            return new BooleanNode(tokenizer.consume());
        } else if(test.getType() == Token.Type.NUMBER) {
            return new DecimalNode(tokenizer.consume());
        } else if(test.getType() == Token.Type.IDENTIFIER) {
            return new ValueReferenceNode(tokenizer.consume());
        }
        throw new IllegalArgumentException("todo");
    }
}
