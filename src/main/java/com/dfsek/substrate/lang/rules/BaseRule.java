package com.dfsek.substrate.lang.rules;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.node.expression.BlockNode;
import com.dfsek.substrate.lexer.Lexer;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.lexer.token.TokenType;
import com.dfsek.substrate.parser.ParserScope;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.List;


/**
 * The base rule of the parser.
 */
public class BaseRule {
    public static Node assemble(Lexer lexer, ParseData data, ParserScope scope) throws ParseException {
        List<Node> contents = List.empty();

        Position begin = lexer.peek().getPosition();

        while (lexer.hasNext()) {
            if (lexer.peek().getType() == TokenType.BLOCK_BEGIN) { // Parse a new block
                contents = contents.append(BlockRule.assemble(lexer, data, scope));
            } else { // Parse a statement.
                contents = contents.append(StatementRule.assemble(lexer, data, scope));
            }
        }

        return new BlockNode(contents, List.empty(), begin);
    }
}
