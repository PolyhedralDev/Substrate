package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.ListIndexNode;
import com.dfsek.substrate.lang.node.expression.RangeNode;
import com.dfsek.substrate.lang.node.expression.binary.arithmetic.*;
import com.dfsek.substrate.lang.node.expression.binary.comparison.EqualsNode;
import com.dfsek.substrate.lang.rules.FunctionInvocationRule;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

public class ExpressionRule implements Rule {
    private static final ExpressionRule INSTANCE = new ExpressionRule();

    public static ExpressionRule getInstance() {
        return INSTANCE;
    }

    @Override
    public ExpressionNode assemble(Tokenizer tokenizer) throws ParseException {
        ExpressionNode node = simple(tokenizer);
        if (tokenizer.peek().isBinaryOperator()) {
            node = assembleBinaryOperator(node, tokenizer);
        }
        return node;
    }

    private ExpressionNode simple(Tokenizer tokenizer) {
        Token test = tokenizer.peek();

        ExpressionNode node;

        if (test.isConstant() || test.isIdentifier()) { // simple expression
            if (tokenizer.peek(1).getType() == Token.Type.GROUP_BEGIN) {
                node = FunctionInvocationRule.getInstance().assemble(tokenizer);
            } else {
                node = BasicExpressionRule.getInstance().assemble(tokenizer);
            }
        } else if (test.isType()) {
            node = CastRule.getInstance().assemble(tokenizer);
        } else if (test.getType() == Token.Type.IF) {
            node = IfExpressionRule.getInstance().assemble(tokenizer);
        } else if (test.getType() == Token.Type.LIST_BEGIN) {
            node = ListRule.getInstance().assemble(tokenizer);
        } else if ((tokenizer.peek(1).isIdentifier() && tokenizer.peek(2).getType() == Token.Type.TYPE)
                || tokenizer.peek(2).getType() == Token.Type.ARROW) { // lambda or function
            node = LambdaExpressionRule.getInstance().assemble(tokenizer);
        } else {
            node = TupleRule.getInstance().assemble(tokenizer);
        }


        if (tokenizer.peek().getType() == Token.Type.RANGE) {
            Position pos = tokenizer.consume().getPosition();
            node = new RangeNode(node, assemble(tokenizer), pos);
        }

        if(tokenizer.peek().getType() == Token.Type.LIST_BEGIN) {
            tokenizer.consume();

            ExpressionNode index = assemble(tokenizer);

            node = new ListIndexNode(node, index);

            ParserUtil.checkType(tokenizer.consume(), Token.Type.LIST_END);
        }

        return node;
    }

    private ExpressionNode assembleBinaryOperator(ExpressionNode left, Tokenizer tokenizer) {
        Token op = tokenizer.consume();
        ExpressionNode right = simple(tokenizer);

        Token next = tokenizer.peek();
        if (next.isBinaryOperator()) {
            if (ParserUtil.hasPrecedence(op.getType(), next.getType())) {
                return join(left, op, assembleBinaryOperator(right, tokenizer));
            } else {
                return assembleBinaryOperator(join(left, op, right), tokenizer);
            }
        }

        return join(left, op, right);
    }

    private ExpressionNode join(ExpressionNode left, Token op, ExpressionNode right) {
        if (op.getType() == Token.Type.ADDITION_OPERATOR) {
            return new AdditionNode(left, right, op);
        } else if (op.getType() == Token.Type.SUBTRACTION_OPERATOR) {
            return new SubtractionNode(left, right, op);
        } else if (op.getType() == Token.Type.MULTIPLICATION_OPERATOR) {
            return new MultiplyNode(left, right, op);
        } else if (op.getType() == Token.Type.DIVISION_OPERATOR) {
            return new DivisionNode(left, right, op);
        } else if (op.getType() == Token.Type.MODULO_OPERATOR) {
            return new ModulusNode(left, right, op);
        } else if (op.getType() == Token.Type.EQUALS_OPERATOR) {
            return new EqualsNode(left, right, op);
        } else {
            throw new ParseException("Unexpected token: " + op, op.getPosition());
        }
    }
}
