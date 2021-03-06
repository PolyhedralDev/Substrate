package com.dfsek.substrate.lang.impl.rule.looplike;

import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.impl.operations.looplike.IfOperation;
import com.dfsek.substrate.lang.impl.rule.BlockRule;
import com.dfsek.substrate.lang.impl.rule.ExpressionRule;
import com.dfsek.substrate.lang.internal.Operation;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

public class IfStatementRule implements Rule {
    @Override
    public Operation assemble(Tokenizer tokenizer, Parser parser) throws ParseException {

        Token ifStatement = tokenizer.consume();

        ParserUtil.checkType(ifStatement, Token.Type.IF_STATEMENT);
        ParserUtil.checkType(tokenizer.consume(), Token.Type.GROUP_BEGIN);

        Operation condition = new ExpressionRule(Operation.ReturnType.BOOL).assemble(tokenizer, parser);

        ParserUtil.checkType(tokenizer.consume(), Token.Type.GROUP_END);

        Operation block = new BlockRule().assemble(tokenizer, parser);

        return new IfOperation(condition, block, ifStatement.getPosition());
    }
}
