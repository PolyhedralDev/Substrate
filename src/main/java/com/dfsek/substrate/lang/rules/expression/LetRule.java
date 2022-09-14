package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.error.ErrorNode;
import com.dfsek.substrate.lang.node.expression.value.ValueAssignmentNode;
import com.dfsek.substrate.lexer.Lexer;
import com.dfsek.substrate.lexer.token.TokenType;
import com.dfsek.substrate.parser.ParserScope;
import com.dfsek.substrate.parser.ParserUtil;
import io.vavr.Tuple2;

import java.util.List;

public class LetRule {
    public static Unchecked<? extends ExpressionNode> assemble(Lexer lexer, ParseData data, ParserScope scope) {

        ParserUtil.check(lexer.consume(), TokenType.LET)
                .and(lexer.consume(), TokenType.BLOCK_BEGIN)
                .getP(() -> assignmentNode(lexer, data, scope))
        ;
        return null;
    }

    private static ValueAssignmentNode assignmentNode(Lexer lexer, ParseData data, ParserScope scope) {
        return null;
    }
}
