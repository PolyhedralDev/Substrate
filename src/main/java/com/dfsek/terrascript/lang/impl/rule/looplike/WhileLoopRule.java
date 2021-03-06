package com.dfsek.terrascript.lang.impl.rule.looplike;

import com.dfsek.terrascript.lang.Rule;
import com.dfsek.terrascript.lang.impl.operations.looplike.WhileOperation;
import com.dfsek.terrascript.lang.impl.rule.BlockRule;
import com.dfsek.terrascript.lang.impl.rule.ExpressionRule;
import com.dfsek.terrascript.lang.internal.Operation;
import com.dfsek.terrascript.parser.Parser;
import com.dfsek.terrascript.parser.ParserUtil;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Token;
import com.dfsek.terrascript.tokenizer.Tokenizer;

public class WhileLoopRule implements Rule {
    @Override
    public Operation assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        Token ifStatement = tokenizer.consume();

        ParserUtil.checkType(ifStatement, Token.Type.WHILE_LOOP);

        ParserUtil.checkType(tokenizer.consume(), Token.Type.GROUP_BEGIN);

        Operation condition = new ExpressionRule(Operation.ReturnType.BOOL).assemble(tokenizer, parser);
        ParserUtil.checkType(tokenizer.consume(), Token.Type.GROUP_END);

        Operation block = new BlockRule().assemble(tokenizer, parser);

        return new WhileOperation(condition, block, ifStatement.getPosition());
    }
}
