package com.dfsek.substrate.lang.rules;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.node.expression.BlockNode;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The base rule of the parser.
 */
public class BaseRule implements Rule {

    @Override
    public Node assemble(Tokenizer tokenizer) throws ParseException {
        List<Node> contents = new ArrayList<>();

        Position begin = tokenizer.peek().getPosition();

        while (tokenizer.hasNext()) {
            System.out.println(tokenizer.peek());
            if (tokenizer.peek().getType() == Token.Type.BLOCK_BEGIN) { // Parse a new block
                contents.add(BlockRule.getInstance().assemble(tokenizer));
            } else { // Parse a statement.
                contents.add(StatementRule.getInstance().assemble(tokenizer));
            }
        }

        return new BlockNode(contents, Collections.emptyList(), begin);
    }
}
