package com.dfsek.substrate.lang.rules;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.node.BlockNode;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.List;

public class BlockRule implements Rule {

    private static final BlockRule INSTANCE = new BlockRule();
    @Override
    public ExpressionNode assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        List<Node> contents = new ArrayList<>();

        Position begin = tokenizer.peek().getPosition();
        ParserUtil.checkType(tokenizer.consume(), Token.Type.BLOCK_BEGIN); // Block beginning

        while (tokenizer.peek().getType() != Token.Type.BLOCK_END) {
            if(tokenizer.peek().getType() == Token.Type.BLOCK_BEGIN) { // Parse a new block
                contents.add(this.assemble(tokenizer, parser));
            } else { // Parse a statement.
                contents.add(StatementRule.getInstance().assemble(tokenizer, parser));
            }
        }

        ParserUtil.checkType(tokenizer.consume(), Token.Type.BLOCK_END); // Block must end.

        return new BlockNode(contents, null, begin);
    }

    public static BlockRule getInstance() {
        return INSTANCE;
    }
}
