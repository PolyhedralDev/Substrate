package com.dfsek.substrate.lang.rules;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.node.expression.BlockNode;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.ParserScope;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.lexer.token.TokenType;
import com.dfsek.substrate.lexer.Lexer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The base rule of the parser.
 */
public class BaseRule implements Rule {

    @Override
    public Node assemble(Lexer lexer, ParseData data, ParserScope scope) throws ParseException {
        List<Node> contents = new ArrayList<>();

        Position begin = lexer.peek().getPosition();

        while (lexer.hasNext()) {
            if (lexer.peek().getType() == TokenType.BLOCK_BEGIN) { // Parse a new block
                contents.add(BlockRule.getInstance().assemble(lexer, data, scope));
            } else { // Parse a statement.
                contents.add(StatementRule.getInstance().assemble(lexer, data, scope));
            }
        }

        return new BlockNode(contents, Collections.emptyList(), begin);
    }
}
