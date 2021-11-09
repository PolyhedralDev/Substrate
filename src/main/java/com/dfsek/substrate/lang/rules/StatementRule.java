package com.dfsek.substrate.lang.rules;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.node.StatementNode;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.rules.expression.ExpressionRule;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

public class StatementRule implements Rule {
    private static final StatementRule INSTANCE = new StatementRule();

    public static StatementRule getInstance() {
        return INSTANCE;
    }

    @Override
    public Node assemble(Tokenizer tokenizer, ParseData data) throws ParseException {
        ExpressionNode node = ExpressionRule.getInstance().assemble(tokenizer, data);
        Token end = ParserUtil.checkType(tokenizer.consume(), Token.Type.STATEMENT_END); // Must finish with statement end token
        return new StatementNode(end.getPosition(), node);
    }
}
