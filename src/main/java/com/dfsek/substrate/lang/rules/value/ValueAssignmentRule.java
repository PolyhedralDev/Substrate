package com.dfsek.substrate.lang.rules.value;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.node.ValueAssignmentNode;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.rules.expression.ExpressionRule;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

public class ValueAssignmentRule implements Rule {
    private static final ValueAssignmentRule INSTANCE = new ValueAssignmentRule();

    public static ValueAssignmentRule getInstance() {
        return INSTANCE;
    }

    @Override
    public Node assemble(Tokenizer tokenizer) throws ParseException {
        Token id = ParserUtil.checkType(tokenizer.consume(), Token.Type.IDENTIFIER);
        ParserUtil.checkType(tokenizer.consume(), Token.Type.ASSIGNMENT); // next token should be =
        ExpressionNode value = ExpressionRule.getInstance().assemble(tokenizer);
        return new ValueAssignmentNode(id, value);
    }
}
