package com.dfsek.substrate.lang.rules;

import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.node.BlockNode;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Tokenizer;

import java.util.Collections;

public class BlockRule implements Rule {
    @Override
    public Node assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        return new BlockNode(Collections.emptyList(), tokenizer.consume().getPosition());
    }
}
