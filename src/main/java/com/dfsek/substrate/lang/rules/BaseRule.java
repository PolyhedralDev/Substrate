package com.dfsek.substrate.lang.rules;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.node.StatementNode;
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


/**
 * The base rule of the parser.
 */
public class BaseRule {
    public static Node assemble(Lexer lexer, ParseData data, ParserScope scope) throws ParseException {
        List<Node> contents = List.empty();

        Position begin = lexer.peek().getPosition();

        Unchecked<ReturnNode> returnNode = null;
        while (lexer.hasNext()) {
            if (lexer.peek().getType() == TokenType.BLOCK_BEGIN) { // Parse a new block
                contents = contents.append(BlockRule.assemble(lexer, data, scope).unchecked());
            } else if (lexer.peek().getType() == TokenType.RETURN) {
                returnNode = ReturnRule.assemble(lexer, data, scope, data.getReturnType());
                contents = contents.append(returnNode.unchecked());
                if (lexer.hasNext())
                    ParserUtil.checkType(lexer.peek(), TokenType.BLOCK_END); // nothing after return.
            } else { // Parse a statement.
                contents = contents.append(StatementRule.assemble(lexer, data, scope));
            }
        }

        if (returnNode == null) {
            if (contents.size() == 1) {
                Unchecked<ExpressionNode> node;
                if (contents.get(0) instanceof ExpressionNode) {
                    node = Unchecked.of((ExpressionNode) contents.get(0));
                } else {
                    node = Unchecked.of(((StatementNode) contents.get(0)).getContent());
                }
                returnNode = ReturnNode.of(Position.getNull(), node, data.getReturnType());
                return BlockNode.of(List.of(returnNode.unchecked()), Option.of(returnNode.unchecked()), begin).get(data.getReturnType());
            } else if (contents.size() == 0) {
                throw new ParseException("Empty script.", Position.getNull());
            } else {
                throw new ParseException("Cannot infer return expression", contents.last().getPosition());
            }
        } else {
            return BlockNode.of(contents, Option.of(returnNode.unchecked()), begin).get(data.getReturnType());
        }
    }
}
