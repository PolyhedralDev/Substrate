package com.dfsek.substrate.lang.rules;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.node.ValueAssignmentNode;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

public class ValueAssignmentRule implements Rule {
    private final ExpressionRule expressionRule = new ExpressionRule();
    @Override
    public Node assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        Token id = ParserUtil.checkType(tokenizer.consume(), Token.Type.IDENTIFIER);
        ParserUtil.checkType(tokenizer.consume(), Token.Type.ASSIGNMENT); // next token should be =
        ExpressionNode value = expressionRule.assemble(tokenizer, parser);
        return new ValueAssignmentNode(id, value);
    }
}
