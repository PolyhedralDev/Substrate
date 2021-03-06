package com.dfsek.substrate.lang.impl.rule;

import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.impl.operations.BlockOperation;
import com.dfsek.substrate.lang.internal.Operation;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.List;

public class BlockRule implements Rule {
    @Override
    public Operation assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        Token begin = tokenizer.consume();
        ParserUtil.checkType(begin, Token.Type.BLOCK_BEGIN);

        List<Operation> ops = new ArrayList<>();
        while(tokenizer.peek().getType() != Token.Type.BLOCK_END) {
            ops.add(new StatementRule().assemble(tokenizer, parser));
        }

        ParserUtil.checkType(tokenizer.consume(), Token.Type.BLOCK_END);

        return new BlockOperation(ops, begin.getPosition());
    }
}
