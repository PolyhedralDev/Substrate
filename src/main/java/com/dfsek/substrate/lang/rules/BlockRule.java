package com.dfsek.substrate.lang.rules;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.node.expression.BlockNode;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.ReturnNode;
import com.dfsek.substrate.lexer.Lexer;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.lexer.token.TokenType;
import com.dfsek.substrate.parser.ParserScope;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.List;


public class BlockRule implements Rule {

    private static final BlockRule INSTANCE = new BlockRule();

    public static BlockRule getInstance() {
        return INSTANCE;
    }

    @Override
    public ExpressionNode assemble(Lexer lexer, ParseData data, ParserScope scope) throws ParseException {
        List<Node> contents = List.empty();

        List<ReturnNode> ret = List.empty();

        Position begin = lexer.peek().getPosition();
        ParserUtil.checkType(lexer.consume(), TokenType.BLOCK_BEGIN); // Block beginning

        ParserScope sub = scope.sub(); // sub-scope

        while (lexer.peek().getType() != TokenType.BLOCK_END) {
            if (lexer.peek().getType() == TokenType.BLOCK_BEGIN) { // Parse a new block
                contents = contents.append(this.assemble(lexer, data, sub));
            } else if (lexer.peek().isIdentifier()
                    || lexer.peek().getType() == TokenType.GROUP_BEGIN
                    || lexer.peek().getType() == TokenType.IF) { // Parse a statement.
                contents = contents.append(StatementRule.getInstance().assemble(lexer, data, sub));
            } else if (lexer.peek().getType() == TokenType.RETURN) { // Parse a return
                ReturnNode returnNode = ReturnRule.getInstance().assemble(lexer, data, sub);
                ret = ret.append(returnNode);
                contents = contents.append(returnNode);
            } else {
                throw new ParseException("Unexpected token: " + lexer.peek(), lexer.consume().getPosition());
            }
        }

        ParserUtil.checkType(lexer.consume(), TokenType.BLOCK_END); // Block must end.

        return new BlockNode(contents, ret, begin);
    }
}
