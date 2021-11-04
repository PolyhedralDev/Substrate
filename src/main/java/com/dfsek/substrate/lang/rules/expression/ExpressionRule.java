package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.ListNode;
import com.dfsek.substrate.lang.node.expression.RangeNode;
import com.dfsek.substrate.lang.node.expression.binary.*;
import com.dfsek.substrate.lang.rules.FunctionInvocationRule;
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
        } else if(test.getType() == Token.Type.LIST_BEGIN) {
            node = ListRule.getInstance().assemble(tokenizer);
        } else if((tokenizer.peek(1).isIdentifier() && tokenizer.peek(2).getType() == Token.Type.TYPE)
        || tokenizer.peek(2).getType() == Token.Type.ARROW) { // lambda or function
            node = LambdaExpressionRule.getInstance().assemble(tokenizer);
        } else {
            node = TupleRule.getInstance().assemble(tokenizer);
        }

        if (tokenizer.peek().isBinaryOperator()) {
            ExpressionNode left = node;
            Token op = tokenizer.consume();
            ExpressionNode right = assemble(tokenizer);
            if (op.getType() == Token.Type.ADDITION_OPERATOR) {
                node = new AdditionNode(left, right, op);
            } else if(op.getType() == Token.Type.SUBTRACTION_OPERATOR) {
                node = new SubtractionNode(left, right, op);
            } else if(op.getType() == Token.Type.MULTIPLICATION_OPERATOR) {
                node = new MultiplyNode(left, right, op);
            } else if(op.getType() == Token.Type.DIVISION_OPERATOR) {
                node = new DivisionNode(left, right, op);
            } else if(op.getType() == Token.Type.MODULO_OPERATOR) {
                node = new ModulusNode(left, right, op);
            } else if(op.getType() == Token.Type.EQUALS_OPERATOR) {
                node = new EqualsNode(left, right, op);
            }else {
                throw new ParseException("Unexpected token: " + op, op.getPosition());
            }
        }
        if(tokenizer.peek().getType() == Token.Type.RANGE) {
            Position pos = tokenizer.consume().getPosition();
            node = new RangeNode(node, assemble(tokenizer), pos);
        }

        return node;
    }
}
