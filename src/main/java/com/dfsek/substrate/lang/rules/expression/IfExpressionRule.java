package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.IfExpressionNode;
import com.dfsek.substrate.lang.node.expression.function.LambdaExpressionNode;
import com.dfsek.substrate.lang.node.expression.function.LambdaInvocationNode;
import com.dfsek.substrate.lang.rules.BlockRule;
import com.dfsek.substrate.lexer.Lexer;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.lexer.token.TokenType;
import com.dfsek.substrate.parser.ParserScope;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.control.Either;

import java.util.Collection;
import java.util.Collections;

public class IfExpressionRule {
    public static Unchecked<? extends ExpressionNode> assemble(Lexer lexer, ParseData data, ParserScope scope) throws ParseException {
        ParserUtil.checkType(lexer.consume(), TokenType.IF);
        ParserUtil.checkType(lexer.consume(), TokenType.GROUP_BEGIN);
        Unchecked<? extends ExpressionNode> predicate = ExpressionRule.assemble(lexer, data, scope);
        ParserUtil.checkType(lexer.consume(), TokenType.GROUP_END);

        Unchecked<? extends ExpressionNode> caseTrueNode = parseIfBlock(lexer, data, scope);

        ParserUtil.checkType(lexer.consume(), TokenType.ELSE);

        Unchecked<? extends ExpressionNode> caseFalseNode = parseIfBlock(lexer, data, scope);

        return IfExpressionNode.of(predicate, caseTrueNode, caseFalseNode);
    }

    private static Unchecked<? extends ExpressionNode> parseIfBlock(Lexer lexer, ParseData data, ParserScope scope) {
        Unchecked<? extends ExpressionNode> caseFalseNode;
        if (lexer.peek().getType() == TokenType.BLOCK_BEGIN) {
            Unchecked<? extends ExpressionNode> internal = BlockRule.assemble(lexer, data, scope);
            caseFalseNode = LambdaInvocationNode.of(LambdaExpressionNode.of(internal, List.empty(), internal.unchecked().getPosition(), internal.reference(), HashSet.empty()));
        } else {
            caseFalseNode = ExpressionRule.assemble(lexer, data, scope);
        }
        return caseFalseNode;
    }
}
