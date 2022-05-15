package com.dfsek.substrate.lang.rules;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
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
import io.vavr.control.Option;


public class BlockRule {
    public static Unchecked<? extends ExpressionNode> assemble(Lexer lexer, ParseData data, ParserScope scope) throws ParseException {
        List<Node> statements = List.empty();

        Option<Unchecked<ReturnNode>> ret = Option.none();

        Position begin = lexer.peek().getPosition();
        ParserUtil.checkType(lexer.consume(), TokenType.BLOCK_BEGIN); // Block beginning

        ParserScope sub = scope.sub(); // sub-scope

        while (lexer.peek().getType() != TokenType.BLOCK_END) {
            if (lexer.peek().getType() == TokenType.BLOCK_BEGIN) { // Parse a new block
                statements = statements.append(assemble(lexer, data, sub).unchecked());
            } else if (lexer.peek().isIdentifier()
                    || lexer.peek().getType() == TokenType.GROUP_BEGIN
                    || lexer.peek().getType() == TokenType.IF) { // Parse a statement.
                statements = statements.append(StatementRule.assemble(lexer, data, sub));
            } else if (lexer.peek().getType() == TokenType.RETURN) { // Parse a return
                Unchecked<ReturnNode> returnNode = ReturnRule.assemble(lexer, data, sub);
                ret = Option.of(returnNode);
                statements = statements.append(returnNode.unchecked());
                ParserUtil.checkType(lexer.peek(), TokenType.BLOCK_END); // nothing after return.
            } else {
                throw new ParseException("Unexpected token: " + lexer.peek(), lexer.consume().getPosition());
            }
        }

        ParserUtil.checkType(lexer.consume(), TokenType.BLOCK_END); // Block must end.

        return BlockNode.of(statements, ret.map(Unchecked::unchecked), begin);
    }
}
