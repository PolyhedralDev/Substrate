package com.dfsek.substrate.lang.rules;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.rules.value.ValueAssignmentRule;
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
    public Node assemble(Tokenizer tokenizer) throws ParseException {
        Node node;
        ParserUtil.checkType(tokenizer.peek(), Token.Type.IDENTIFIER);
        // We're declaring a value or invoking a function.
        Token next = tokenizer.peek(1);
        ParserUtil.checkType(next, Token.Type.GROUP_BEGIN, Token.Type.ASSIGNMENT);
        if (next.getType() == Token.Type.GROUP_BEGIN) { // Invoking a function.
            node = FunctionInvocationRule.getInstance().assemble(tokenizer);
        } else {
            node = ValueAssignmentRule.getInstance().assemble(tokenizer);
        }

        ParserUtil.checkType(tokenizer.consume(), Token.Type.STATEMENT_END); // Must finish with statement end token
        return node;
    }
}
