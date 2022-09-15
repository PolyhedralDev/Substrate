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
import com.dfsek.substrate.parser.ParserScope;
import com.dfsek.substrate.parser.ParserUtil;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;

import java.util.function.BiFunction;
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

    private static Tuple2<ParserScope, LinkedHashMap<String, ExpressionNode>> assignmentNode(LinkedHashMap<String, ExpressionNode> start, Lexer lexer, ParseData data, ParserScope scope) {
        System.out.println("Checking");
        if (lexer.peek().getType() != TokenType.IDENTIFIER) return new Tuple2<>(scope, start);
        System.out.println("hhh");
        Token id = lexer.consume();

        ExpressionNode valueNode = ParserUtil.check(lexer.consume(), TokenType.ASSIGNMENT)
                .getP(() -> ExpressionRule.assemble(lexer, data, scope))
                .map(node -> (ValueAssignmentNode.of(id, node, scope.getLocalWidth())))
                .get()
                .fold(ErrorNode::of, Function.identity()).unchecked();
        LinkedHashMap<String, ExpressionNode> put = start.put(id.getContent(), valueNode);

        Value value = new PrimitiveValue(valueNode.reference(), id.getContent(), scope.getLocalWidth(), valueNode.reference().frames());
        if (lexer.peek().getType() == TokenType.SEPARATOR) {
            lexer.consume();
            return assignmentNode(put, lexer, data, scope.register(id.getContent(), value));
        }
        return new Tuple2<>(scope.register(id.getContent(), value), put);
    }
}
