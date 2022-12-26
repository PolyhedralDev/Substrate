package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.compiler.value.PrimitiveValue;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.LetNode;
import com.dfsek.substrate.lang.node.expression.error.ErrorNode;
import com.dfsek.substrate.lang.node.expression.value.ValueAssignmentNode;
import com.dfsek.substrate.lexer.Lexer;
import com.dfsek.substrate.lexer.token.Token;
import com.dfsek.substrate.lexer.token.TokenType;
import com.dfsek.substrate.parser.scope.ParserScope;
import com.dfsek.substrate.parser.ParserUtil;
import io.vavr.Tuple2;
import io.vavr.collection.LinkedHashMap;
import io.vavr.control.Either;

import java.util.function.Function;

public class LetRule {
    public static Unchecked<? extends ExpressionNode> assemble(Lexer lexer, ParseData data, ParserScope scope) {
        System.out.println("Parsing LET");
        return ParserUtil.check(lexer.consume(), TokenType.LET)
                .and(lexer.consume(), TokenType.BLOCK_BEGIN)
                .getP(() -> assignmentNode(LinkedHashMap.empty(), lexer, data, scope))
                .and(lexer.consume(), TokenType.BLOCK_END)
                .and(lexer.consume(), TokenType.IN)
                .map(assignment -> (Unchecked<? extends ExpressionNode>)
                        LetNode.of(assignment._2,
                                ExpressionRule.assemble(lexer, data, assignment._1)
                        ))
                .get()
                .fold(ErrorNode::of, Function.identity());
    }

    private static Tuple2<ParserScope, LinkedHashMap<String, Either<ErrorNode, ValueAssignmentNode>>> assignmentNode(LinkedHashMap<String, Either<ErrorNode, ValueAssignmentNode>> start, Lexer lexer, ParseData data, ParserScope scope) {
        System.out.println("Checking");
        if (lexer.peek().getType() != TokenType.IDENTIFIER) return new Tuple2<>(scope, start);
        Token id = lexer.consume();

        Either<ErrorNode, ValueAssignmentNode> valueNode = ParserUtil.check(lexer.consume(), TokenType.ASSIGNMENT)
                .getP(() -> ExpressionRule.assemble(lexer, data, scope))
                .map(node -> ValueAssignmentNode.of(id, node, scope.getLocalWidth(), new PrimitiveValue(node.reference(), id.getContent(), scope.getLocalWidth(), node.reference().frames())))
                .get()
                .bimap(ErrorNode::of, Function.identity())
                .bimap(Unchecked::unchecked, Unchecked::unchecked);
        LinkedHashMap<String, Either<ErrorNode, ValueAssignmentNode>> put = start.put(id.getContent(), valueNode);

        ParserScope newScope = valueNode.fold(ignore -> scope, assignment -> scope.register(id.getContent(), assignment.getReference()));

        if (lexer.peek().getType() == TokenType.SEPARATOR) {
            lexer.consume();
            return assignmentNode(put, lexer, data, newScope);
        }
        return new Tuple2<>(newScope, put);
    }
}
