package com.dfsek.substrate.lang.rules;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.rules.value.ValueAssignmentRule;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

public class StatementRule implements Rule {
    private static final StatementRule INSTANCE = new StatementRule();
    @Override
    public Node assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        Token current = tokenizer.peek();
        Node node;
        if(current.getType() == Token.Type.IDENTIFIER) { // We're declaring a value or invoking a function.
            Token next = tokenizer.peek(1);
            ParserUtil.checkType(next, Token.Type.GROUP_BEGIN, Token.Type.ASSIGNMENT);
            if(next.getType() == Token.Type.GROUP_BEGIN) { // Invoking a function.
                node = FunctionInvocationRule.getInstance().assemble(tokenizer, parser);
            } else {
                node = ValueAssignmentRule.getInstance().assemble(tokenizer, parser);
            }
        } else throw new IllegalArgumentException("not implemented yet");
        ParserUtil.checkType(tokenizer.consume(), Token.Type.STATEMENT_END); // Must finish with statement end token
        return node;
    }

    public static StatementRule getInstance() {
        return INSTANCE;
    }
}
