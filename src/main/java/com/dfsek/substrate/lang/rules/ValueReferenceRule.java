package com.dfsek.substrate.lang.rules;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.node.ValueReferenceNode;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

public class ValueReferenceRule implements Rule {
    @Override
    public Node assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        Token id = ParserUtil.checkType(tokenizer.consume(), Token.Type.IDENTIFIER);
        return new ValueReferenceNode(id);
    }
}
