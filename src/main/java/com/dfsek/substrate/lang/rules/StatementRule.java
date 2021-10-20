package com.dfsek.substrate.lang.rules;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

public class StatementRule implements Rule {
    private final ValueAssignmentRule valueAssignmentRule = new ValueAssignmentRule();
    private final FunctionInvocationRule functionInvocationRule = new FunctionInvocationRule();
    @Override
    public Node assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        Token current = tokenizer.peek();
        if(current.getType() == Token.Type.IDENTIFIER) { // We're declaring a value or invoking a function.
            Token identifier = tokenizer.consume();
            Token next = tokenizer.peek();
            ParserUtil.checkType(next, Token.Type.GROUP_BEGIN, Token.Type.ASSIGNMENT);
            if(next.getType() == Token.Type.GROUP_BEGIN) { // Invoking a function.

            } else {

            }
        }
        return null;
    }
}
