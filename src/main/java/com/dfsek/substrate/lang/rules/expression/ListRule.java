package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

public class ListRule implements Rule {
    private static final ListRule INSTANCE = new ListRule();
    @Override
    public ExpressionNode assemble(Tokenizer tokenizer) throws ParseException {
        ParserUtil.checkType(tokenizer.consume(), Token.Type.LIST_BEGIN);
        return null;
    }

    public static ListRule getInstance() {
        return INSTANCE;
    }
}
