package com.dfsek.terrascript.lang.impl.rule.looplike;

import com.dfsek.terrascript.lang.Rule;
import com.dfsek.terrascript.lang.impl.operations.looplike.IfOperation;
import com.dfsek.terrascript.lang.impl.rule.BlockRule;
import com.dfsek.terrascript.lang.impl.rule.match.ScriptRuleMatcher;
import com.dfsek.terrascript.lang.internal.Operation;
import com.dfsek.terrascript.parser.Parser;
import com.dfsek.terrascript.parser.ParserUtil;
import com.dfsek.terrascript.parser.TokenView;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Token;
import com.dfsek.terrascript.tokenizer.Tokenizer;

public class IfStatementRule implements Rule {
    @Override
    public Operation assemble(Tokenizer tokenizer, Parser parser) throws ParseException {

        Token ifStatement = tokenizer.consume();

        ParserUtil.checkType(ifStatement, Token.Type.IF_STATEMENT);
        ParserUtil.checkType(tokenizer.consume(), Token.Type.GROUP_BEGIN);

        Operation condition = new ScriptRuleMatcher().match(tokenizer.peek(), new TokenView(tokenizer)).assemble(tokenizer, parser);

        ParserUtil.checkType(tokenizer.consume(), Token.Type.GROUP_END);

        Operation block = new BlockRule().assemble(tokenizer, parser);

        return new IfOperation(condition, block, ifStatement.getPosition());
    }
}
