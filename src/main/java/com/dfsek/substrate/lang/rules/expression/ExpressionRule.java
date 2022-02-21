package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.BooleanNotNode;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.binary.arithmetic.*;
import com.dfsek.substrate.lang.node.expression.binary.bool.BooleanAndNode;
import com.dfsek.substrate.lang.node.expression.binary.bool.BooleanOrNode;
import com.dfsek.substrate.lang.node.expression.binary.comparison.*;
import com.dfsek.substrate.lang.node.expression.function.FunctionInvocationNode;
import com.dfsek.substrate.lang.node.expression.function.MacroNode;
import com.dfsek.substrate.lang.node.expression.list.ListIndexNode;
import com.dfsek.substrate.lang.node.expression.list.RangeNode;
import com.dfsek.substrate.parser.ParserScope;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.List;

public class ExpressionRule implements Rule {
    private static final ExpressionRule INSTANCE = new ExpressionRule();

    public static ExpressionRule getInstance() {
        return INSTANCE;
    }

    @Override
    public ExpressionNode assemble(Tokenizer tokenizer, ParseData data, ParserScope scope) throws ParseException {
        return assemble(tokenizer, data, scope, null);
    }

    public ExpressionNode assemble(Tokenizer tokenizer, ParseData data, ParserScope scope, String variableName) throws ParseException {
        ExpressionNode node = simple(tokenizer, data, scope, variableName);
        if (tokenizer.peek().isBinaryOperator()) {
            node = assembleBinaryOperator(node, tokenizer, data, scope, variableName);
        }
        return node;
    }

    private ExpressionNode simple(Tokenizer tokenizer, ParseData data, ParserScope scope, String variableName) {
        Token test = tokenizer.peek();

        boolean not = false;
        Position booleanNot = null;

        if (test.getType() == Token.Type.BOOLEAN_NOT) {
            not = true;
            booleanNot = tokenizer.consume().getPosition();
            test = tokenizer.peek();
        }

        ExpressionNode node;

        boolean possibleFunctionSite = true;

        if (test.isConstant() || test.isIdentifier()) { // simple expression
            if (test.isIdentifier() && data.hasMacro(test.getContent())) {
                tokenizer.consume();
                node = new MacroNode(data.getMacro(test.getContent()), test.getPosition(), parseArguments(tokenizer, data, scope));
            } else {
                node = BasicExpressionRule.getInstance().assemble(tokenizer, data, scope);
            }
        } else if (test.isType()) {
            node = CastRule.getInstance().assemble(tokenizer, data, scope);
            possibleFunctionSite = false;
        } else if (test.getType() == Token.Type.IF) {
            node = IfExpressionRule.getInstance().assemble(tokenizer, data, scope);
        } else if (test.getType() == Token.Type.LIST_BEGIN) {
            node = ListRule.getInstance().assemble(tokenizer, data, scope);
            possibleFunctionSite = false;
        } else if ((tokenizer.peek(1).isIdentifier() && tokenizer.peek(2).getType() == Token.Type.TYPE)
                || tokenizer.peek(2).getType() == Token.Type.ARROW
                || tokenizer.peek(2).getType() == Token.Type.TYPE) { // lambda or function
            node = LambdaExpressionRule.getInstance().assemble(tokenizer, data, scope, variableName);
        } else if (test.getType() == Token.Type.GROUP_BEGIN) {
            node = TupleRule.getInstance().assemble(tokenizer, data, scope);
            possibleFunctionSite = false;
        } else {
            throw new ParseException("Unexpected token: " + test, test.getPosition());
        }


        if (tokenizer.peek().getType() == Token.Type.RANGE) {
            Position pos = tokenizer.consume().getPosition();
            node = new RangeNode(node, assemble(tokenizer, data, scope), pos);
        }

        if (tokenizer.peek().getType() == Token.Type.LIST_BEGIN) {
            tokenizer.consume();

            ExpressionNode index = assemble(tokenizer, data, scope);

            node = new ListIndexNode(node, index);

            ParserUtil.checkType(tokenizer.consume(), Token.Type.LIST_END);
        }

        while (tokenizer.peek().getType() == Token.Type.GROUP_BEGIN && possibleFunctionSite) {
            node = new FunctionInvocationNode(node, parseArguments(tokenizer, data, scope));
        }

        if (not) return new BooleanNotNode(booleanNot, node);
        return node;
    }

    private List<ExpressionNode> parseArguments(Tokenizer tokenizer, ParseData data, ParserScope scope) {
        ParserUtil.checkType(tokenizer.consume(), Token.Type.GROUP_BEGIN);

        List<ExpressionNode> args = new ArrayList<>();
        while (tokenizer.peek().getType() != Token.Type.GROUP_END) {
            args.add(ExpressionRule.getInstance().assemble(tokenizer, data, scope));
            if (ParserUtil.checkType(tokenizer.peek(), Token.Type.SEPARATOR, Token.Type.GROUP_END).getType() == Token.Type.SEPARATOR) {
                tokenizer.consume(); // consume separator
            }
        }
        ParserUtil.checkType(tokenizer.consume(), Token.Type.GROUP_END);
        return args;
    }

    private ExpressionNode assembleBinaryOperator(ExpressionNode left, Tokenizer tokenizer, ParseData data, ParserScope scope, String variableName) {
        Token op = tokenizer.consume();
        ExpressionNode right = simple(tokenizer, data, scope, variableName);

        Token next = tokenizer.peek();
        if (next.isBinaryOperator()) {
            if (ParserUtil.hasPrecedence(op.getType(), next.getType())) {
                return join(left, op, assembleBinaryOperator(right, tokenizer, data, scope, variableName), data);
            } else {
                return assembleBinaryOperator(join(left, op, right, data), tokenizer, data, scope, variableName);
            }
        }

        return join(left, op, right, data);
    }

    private ExpressionNode join(ExpressionNode left, Token op, ExpressionNode right, ParseData data) {
        if (op.getType() == Token.Type.ADDITION_OPERATOR) {
            return new AdditionNode(
                    data.checkType(left, Signature.integer(), Signature.string(), Signature.decimal()),
                    data.assertEqual(right, left),
                    op);
        } else if (op.getType() == Token.Type.SUBTRACTION_OPERATOR) {
            return new SubtractionNode(
                    data.checkType(left, Signature.integer(), Signature.decimal()),
                    data.assertEqual(right, left),
                    op);
        } else if (op.getType() == Token.Type.MULTIPLICATION_OPERATOR) {
            return new MultiplyNode(
                    data.checkType(left, Signature.integer(), Signature.decimal()),
                    data.assertEqual(right, left),
                    op);
        } else if (op.getType() == Token.Type.DIVISION_OPERATOR) {
            return new DivisionNode(
                    data.checkType(left, Signature.integer(), Signature.decimal()),
                    data.assertEqual(right, left),
                    op);
        } else if (op.getType() == Token.Type.MODULO_OPERATOR) {
            return new ModulusNode(
                    data.checkType(left, Signature.integer(), Signature.decimal()),
                    data.assertEqual(right, left),
                    op);
        } else if (op.getType() == Token.Type.EQUALS_OPERATOR) {
            return new EqualsNode(
                    left,
                    data.assertEqual(right, left),
                    op);
        } else if (op.getType() == Token.Type.NOT_EQUALS_OPERATOR) {
            return new NotEqualsNode(
                    left,
                    data.assertEqual(right, left),
                    op);
        } else if (op.getType() == Token.Type.GREATER_THAN_OPERATOR) {
            return new GreaterThanNode(
                    data.checkType(left, Signature.decimal(), Signature.integer()),
                    data.assertEqual(right, left),
                    op);
        } else if (op.getType() == Token.Type.LESS_THAN_OPERATOR) {
            return new LessThanNode(
                    data.checkType(left, Signature.decimal(), Signature.integer()),
                    data.assertEqual(right, left),
                    op);
        } else if (op.getType() == Token.Type.GREATER_THAN_OR_EQUALS_OPERATOR) {
            return new GreaterThanOrEqualsNode(
                    data.checkType(left, Signature.decimal(), Signature.integer()),
                    data.assertEqual(right, left),
                    op);
        } else if (op.getType() == Token.Type.LESS_THAN_OR_EQUALS_OPERATOR) {
            return new LessThanOrEqualsNode(
                    data.checkType(left, Signature.decimal(), Signature.integer()),
                    data.assertEqual(right, left),
                    op);
        } else if (op.getType() == Token.Type.BOOLEAN_AND) {
            return new BooleanAndNode(
                    data.checkType(left, Signature.bool()),
                    data.assertEqual(right, left),
                    op);
        } else if (op.getType() == Token.Type.BOOLEAN_OR) {
            return new BooleanOrNode(
                    data.checkType(left, Signature.bool()),
                    data.assertEqual(right, left),
                    op);
        } else {
            throw new ParseException("Unexpected token: " + op, op.getPosition());
        }
    }
}
