package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.BooleanNotNode;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.NumberInverseNode;
import com.dfsek.substrate.lang.node.expression.binary.arithmetic.*;
import com.dfsek.substrate.lang.node.expression.binary.bool.BooleanAndNode;
import com.dfsek.substrate.lang.node.expression.binary.bool.BooleanOrNode;
import com.dfsek.substrate.lang.node.expression.binary.comparison.*;
import com.dfsek.substrate.lang.node.expression.function.FunctionInvocationNode;
import com.dfsek.substrate.lang.node.expression.function.MacroNode;
import com.dfsek.substrate.lang.node.expression.list.ListIndexNode;
import com.dfsek.substrate.lang.node.expression.list.RangeNode;
import com.dfsek.substrate.lexer.Lexer;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.lexer.token.Token;
import com.dfsek.substrate.lexer.token.TokenType;
import com.dfsek.substrate.parser.ParserScope;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.List;


public class ExpressionRule implements Rule {
    private static final ExpressionRule INSTANCE = new ExpressionRule();

    public static ExpressionRule getInstance() {
        return INSTANCE;
    }

    @Override
    public ExpressionNode assemble(Lexer lexer, ParseData data, ParserScope scope) throws ParseException {
        return assemble(lexer, data, scope, null);
    }

    public ExpressionNode assemble(Lexer lexer, ParseData data, ParserScope scope, String variableName) throws ParseException {
        ExpressionNode node = simple(lexer, data, scope, variableName);
        if (lexer.peek().isBinaryOperator()) {
            node = assembleBinaryOperator(node, lexer, data, scope, variableName);
        }
        return node;
    }

    private ExpressionNode simple(Lexer lexer, ParseData data, ParserScope scope, String variableName) {
        Token test = lexer.peek();


        boolean invert = false;
        Position numberInvert = null;
        if (test.getType() == TokenType.SUBTRACTION_OPERATOR) {
            invert = true;
            numberInvert = lexer.consume().getPosition();
            test = lexer.peek();
        }

        boolean not = false;
        Position booleanNot = null;
        if (test.getType() == TokenType.BOOLEAN_NOT) {
            not = true;
            booleanNot = lexer.consume().getPosition();
            test = lexer.peek();
        }

        ExpressionNode node;

        boolean possibleFunctionSite = true;

        if (test.isConstant() || test.isIdentifier()) { // simple expression
            if (test.isIdentifier() && data.hasMacro(test.getContent())) {
                lexer.consume();
                node = new MacroNode(data.getMacro(test.getContent()), test.getPosition(), parseArguments(lexer, data, scope));
            } else {
                node = BasicExpressionRule.getInstance().assemble(lexer, data, scope);
            }
        } else if (test.isType()) {
            node = CastRule.getInstance().assemble(lexer, data, scope);
            possibleFunctionSite = false;
        } else if (test.getType() == TokenType.IF) {
            node = IfExpressionRule.getInstance().assemble(lexer, data, scope);
        } else if (test.getType() == TokenType.LIST_BEGIN) {
            node = ListRule.getInstance().assemble(lexer, data, scope);
            possibleFunctionSite = false;
        } else if ((lexer.peek(1).isIdentifier() && lexer.peek(2).getType() == TokenType.TYPE)
                || lexer.peek(2).getType() == TokenType.ARROW
                || lexer.peek(2).getType() == TokenType.TYPE) { // lambda or function
            node = LambdaExpressionRule.getInstance().assemble(lexer, data, scope, variableName);
        } else if (test.getType() == TokenType.GROUP_BEGIN) {
            node = TupleRule.getInstance().assemble(lexer, data, scope);
            possibleFunctionSite = false;
        } else {
            throw new ParseException("Unexpected token: " + test, test.getPosition());
        }


        if (lexer.peek().getType() == TokenType.RANGE) {
            Position pos = lexer.consume().getPosition();
            node = new RangeNode(node, assemble(lexer, data, scope), pos);
        }

        if (lexer.peek().getType() == TokenType.LIST_BEGIN) {
            lexer.consume();

            ExpressionNode index = assemble(lexer, data, scope);

            node = new ListIndexNode(node, index);

            ParserUtil.checkType(lexer.consume(), TokenType.LIST_END);
        }

        while (lexer.peek().getType() == TokenType.GROUP_BEGIN && possibleFunctionSite) {
            node = new FunctionInvocationNode(node, parseArguments(lexer, data, scope));
        }

        if (invert) return new NumberInverseNode(numberInvert, node);
        if (not) return new BooleanNotNode(booleanNot, node);
        return node;
    }

    private List<ExpressionNode> parseArguments(Lexer lexer, ParseData data, ParserScope scope) {
        ParserUtil.checkType(lexer.consume(), TokenType.GROUP_BEGIN);

        List<ExpressionNode> args = List.empty();
        while (lexer.peek().getType() != TokenType.GROUP_END) {
            args = args.append(ExpressionRule.getInstance().assemble(lexer, data, scope));
            if (ParserUtil.checkType(lexer.peek(), TokenType.SEPARATOR, TokenType.GROUP_END).getType() == TokenType.SEPARATOR) {
                lexer.consume(); // consume separator
            }
        }
        ParserUtil.checkType(lexer.consume(), TokenType.GROUP_END);
        return args;
    }

    private ExpressionNode assembleBinaryOperator(ExpressionNode left, Lexer lexer, ParseData data, ParserScope scope, String variableName) {
        Token op = lexer.consume();
        ExpressionNode right = simple(lexer, data, scope, variableName);

        Token next = lexer.peek();
        if (next.isBinaryOperator()) {
            if (ParserUtil.hasPrecedence(op.getType(), next.getType())) {
                return join(left, op, assembleBinaryOperator(right, lexer, data, scope, variableName), data);
            } else {
                return assembleBinaryOperator(join(left, op, right, data), lexer, data, scope, variableName);
            }
        }

        return join(left, op, right, data);
    }

    private ExpressionNode join(ExpressionNode left, Token op, ExpressionNode right, ParseData data) {
        if (op.getType() == TokenType.ADDITION_OPERATOR) {
            return new AdditionNode(
                    data.checkType(left, Signature.integer(), Signature.string(), Signature.decimal()),
                    data.assertEqual(right, left),
                    op);
        } else if (op.getType() == TokenType.SUBTRACTION_OPERATOR) {
            return new SubtractionNode(
                    data.checkType(left, Signature.integer(), Signature.decimal()),
                    data.assertEqual(right, left),
                    op);
        } else if (op.getType() == TokenType.MULTIPLICATION_OPERATOR) {
            return new MultiplyNode(
                    data.checkType(left, Signature.integer(), Signature.decimal()),
                    data.assertEqual(right, left),
                    op);
        } else if (op.getType() == TokenType.DIVISION_OPERATOR) {
            return new DivisionNode(
                    data.checkType(left, Signature.integer(), Signature.decimal()),
                    data.assertEqual(right, left),
                    op);
        } else if (op.getType() == TokenType.MODULO_OPERATOR) {
            return new ModulusNode(
                    data.checkType(left, Signature.integer(), Signature.decimal()),
                    data.assertEqual(right, left),
                    op);
        } else if (op.getType() == TokenType.EQUALS_OPERATOR) {
            return new EqualsNode(
                    left,
                    data.assertEqual(right, left),
                    op);
        } else if (op.getType() == TokenType.NOT_EQUALS_OPERATOR) {
            return new NotEqualsNode(
                    left,
                    data.assertEqual(right, left),
                    op);
        } else if (op.getType() == TokenType.GREATER_THAN_OPERATOR) {
            return new GreaterThanNode(
                    data.checkType(left, Signature.decimal(), Signature.integer()),
                    data.assertEqual(right, left),
                    op);
        } else if (op.getType() == TokenType.LESS_THAN_OPERATOR) {
            return new LessThanNode(
                    data.checkType(left, Signature.decimal(), Signature.integer()),
                    data.assertEqual(right, left),
                    op);
        } else if (op.getType() == TokenType.GREATER_THAN_OR_EQUALS_OPERATOR) {
            return new GreaterThanOrEqualsNode(
                    data.checkType(left, Signature.decimal(), Signature.integer()),
                    data.assertEqual(right, left),
                    op);
        } else if (op.getType() == TokenType.LESS_THAN_OR_EQUALS_OPERATOR) {
            return new LessThanOrEqualsNode(
                    data.checkType(left, Signature.decimal(), Signature.integer()),
                    data.assertEqual(right, left),
                    op);
        } else if (op.getType() == TokenType.BOOLEAN_AND) {
            return new BooleanAndNode(
                    data.checkType(left, Signature.bool()),
                    data.assertEqual(right, left),
                    op);
        } else if (op.getType() == TokenType.BOOLEAN_OR) {
            return new BooleanOrNode(
                    data.checkType(left, Signature.bool()),
                    data.assertEqual(right, left),
                    op);
        } else {
            throw new ParseException("Unexpected token: " + op, op.getPosition());
        }
    }
}
