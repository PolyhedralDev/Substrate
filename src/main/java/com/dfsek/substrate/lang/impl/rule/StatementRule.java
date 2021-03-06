package com.dfsek.substrate.lang.impl.rule;

import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.impl.operations.looplike.BlockedOperation;
import com.dfsek.substrate.lang.impl.rule.match.ScriptRuleMatcher;
import com.dfsek.substrate.lang.internal.Operation;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

public class StatementRule implements Rule {
    @Override
    public Operation assemble(Tokenizer tokenizer, Parser parser) throws ParseException {

        Operation op = parser.expect(new ScriptRuleMatcher());

        if(!(op instanceof BlockedOperation)) {
            Token statement = tokenizer.consume();
            ParserUtil.checkType(statement, Token.Type.STATEMENT_END);
        }

        return op;
    }
}
