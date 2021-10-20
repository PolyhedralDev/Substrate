package com.dfsek.substrate.lang.rules;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.node.FunctionInvocationNode;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.List;

public class FunctionInvocationRule implements Rule {
    private final ExpressionRule expressionRule = new ExpressionRule();
    @Override
    public Node assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        Token id = ParserUtil.checkType(tokenizer.consume(), Token.Type.IDENTIFIER);
        ParserUtil.checkType(tokenizer.consume(), Token.Type.GROUP_BEGIN);

        List<ExpressionNode> args = new ArrayList<>();
        while (tokenizer.peek().getType() != Token.Type.GROUP_END) {
            args.add(expressionRule.assemble(tokenizer, parser));
            if(ParserUtil.checkType(tokenizer.peek(), Token.Type.SEPARATOR, Token.Type.GROUP_END).getType() == Token.Type.SEPARATOR) {
                tokenizer.consume(); // consume separator
            }
        }
        ParserUtil.checkType(tokenizer.consume(), Token.Type.GROUP_END);

        return new FunctionInvocationNode(id, args);
    }
}
