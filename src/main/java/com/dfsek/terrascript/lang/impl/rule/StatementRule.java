package com.dfsek.terrascript.lang.impl.rule;

import com.dfsek.terrascript.lang.Rule;
import com.dfsek.terrascript.lang.impl.operations.looplike.BlockedOperation;
import com.dfsek.terrascript.lang.impl.rule.match.ScriptRuleMatcher;
import com.dfsek.terrascript.lang.internal.Operation;
import com.dfsek.terrascript.parser.Parser;
import com.dfsek.terrascript.parser.ParserUtil;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Token;
import com.dfsek.terrascript.tokenizer.Tokenizer;

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
