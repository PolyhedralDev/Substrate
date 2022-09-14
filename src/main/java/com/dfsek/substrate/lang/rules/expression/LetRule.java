package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.LetNode;
import com.dfsek.substrate.lang.node.expression.error.ErrorNode;
import com.dfsek.substrate.lang.node.expression.value.ValueAssignmentNode;
import com.dfsek.substrate.lexer.Lexer;
import com.dfsek.substrate.lexer.token.Token;
import com.dfsek.substrate.lexer.token.TokenType;
import com.dfsek.substrate.parser.ParserScope;
import com.dfsek.substrate.parser.ParserUtil;
import io.vavr.Tuple2;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;

import java.util.function.BiFunction;
import java.util.function.Function;

public class LetRule {
    public static Unchecked<? extends ExpressionNode> assemble(Lexer lexer, ParseData data, ParserScope scope) {
        return ParserUtil.check(lexer.consume(), TokenType.LET)
                .and(lexer.consume(), TokenType.BLOCK_BEGIN)
                .getP(() -> assignmentNode(LinkedHashMap.empty(), lexer, data, scope))
                .and(lexer.consume(), TokenType.BLOCK_END)
                .map(assignment -> (Unchecked<? extends ExpressionNode>) LetNode.of(assignment,
                        ExpressionRule.assemble(lexer, data, assignment
                                .foldLeft(scope, (parserScope, stringExpressionNodeTuple2) -> parserScope
                                        .register(stringExpressionNodeTuple2._1, stringExpressionNodeTuple2._2.reference()))
                        )
                ))
                .get()
                .fold(ErrorNode::of, Function.identity());
    }

    private static LinkedHashMap<String, ExpressionNode> assignmentNode(LinkedHashMap<String, ExpressionNode> start, Lexer lexer, ParseData data, ParserScope scope) {
        if (lexer.peek().getType() != TokenType.IDENTIFIER) return start;
        Token id = lexer.consume();

        LinkedHashMap<String, ExpressionNode> put = start.put(id.getContent(),
                ParserUtil.check(lexer.consume(), TokenType.ASSIGNMENT)
                        .getP(() -> ExpressionRule.assemble(lexer, data, scope))
                        .map(node -> (Unchecked<? extends ExpressionNode>) ValueAssignmentNode.of(id, node))
                        .get()
                        .fold(ErrorNode::of, Function.identity()).unchecked());

        if (lexer.peek().getType() == TokenType.SEPARATOR) {
            return assignmentNode(put, lexer, data, scope);
        }
        return put;
    }
}
