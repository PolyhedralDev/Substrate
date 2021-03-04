package com.dfsek.terrascript.lang.impl.rule;

import com.dfsek.terrascript.lang.Rule;
import com.dfsek.terrascript.lang.impl.operations.BlockOperation;
import com.dfsek.terrascript.lang.impl.rule.match.ScriptRuleMatcher;
import com.dfsek.terrascript.lang.internal.Operation;
import com.dfsek.terrascript.parser.Parser;
import com.dfsek.terrascript.parser.ParserUtil;
import com.dfsek.terrascript.parser.TokenView;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Token;
import com.dfsek.terrascript.tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.List;

public class BlockRule implements Rule {
    @Override
    public Operation assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        Token begin = tokenizer.consume();
        ParserUtil.checkType(begin, Token.Type.BLOCK_BEGIN);

        List<Operation> ops = new ArrayList<>();
        while(tokenizer.peek().getType() != Token.Type.BLOCK_END) {
            ops.add(new ScriptRuleMatcher().match(tokenizer.peek(), new TokenView(tokenizer)).assemble(tokenizer, parser));
        }

        ParserUtil.checkType(tokenizer.consume(), Token.Type.BLOCK_END);

        return new BlockOperation(ops, begin.getPosition());
    }
}
