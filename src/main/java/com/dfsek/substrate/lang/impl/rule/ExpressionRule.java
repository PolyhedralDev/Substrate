package com.dfsek.substrate.lang.impl.rule;

import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.impl.operations.comparison.number.EqualsOperationNumber;
import com.dfsek.substrate.lang.impl.operations.comparison.number.GreaterThanOperationNumber;
import com.dfsek.substrate.lang.impl.operations.comparison.number.GreaterThanOrEqualsOperationNumber;
import com.dfsek.substrate.lang.impl.operations.comparison.number.LessThanOperationNumber;
import com.dfsek.substrate.lang.impl.operations.comparison.number.LessThanOrEqualsOperationNumber;
import com.dfsek.substrate.lang.impl.operations.comparison.number.NotEqualsOperationNumber;
import com.dfsek.substrate.lang.impl.operations.comparison.string.EqualsOperationString;
import com.dfsek.substrate.lang.impl.operations.comparison.string.NotEqualsOperationString;
import com.dfsek.substrate.lang.impl.operations.number.binary.*;
import com.dfsek.substrate.lang.impl.rule.match.LiteralRuleMatcher;
import com.dfsek.substrate.lang.internal.Operation;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

public class ExpressionRule implements Rule {
    private final Operation.ReturnType type;

    public ExpressionRule() {
        this.type = null;
    }

    public ExpressionRule(Operation.ReturnType req) {
        this.type = req;
    }
    @Override
    public Operation assemble(Tokenizer tokenizer, Parser parser) throws ParseException {

        Operation expression = parser.expect(new LiteralRuleMatcher(type));

        System.out.println("EXP:" + expression);
        System.out.println("NEXT:"+tokenizer.peek());
        while (tokenizer.peek().isBinaryOperator()) {
            Token op = tokenizer.consume();
            switch (op.getType()) {
                case ADDITION_OPERATOR:
                    expression = new AdditionOperation(expression, new ExpressionRule(type).assemble(tokenizer, parser), op.getPosition());
                    break;
                case SUBTRACTION_OPERATOR:
                    expression = new SubtractionOperation(expression, new ExpressionRule(type).assemble(tokenizer, parser), op.getPosition());
                    break;
                case MULTIPLICATION_OPERATOR:
                    expression = new MultiplicationOperation(expression, new ExpressionRule(type).assemble(tokenizer, parser), op.getPosition());
                    break;
                case DIVISION_OPERATOR:
                    expression = new DivisionOperation(expression, new ExpressionRule(Operation.ReturnType.NUM).assemble(tokenizer, parser), op.getPosition());
                    break;
                case MODULO_OPERATOR:
                    expression = new ModuloOperation(expression, new ExpressionRule(Operation.ReturnType.STR).assemble(tokenizer, parser), op.getPosition());
                    break;
                case EQUALS_OPERATOR:
                    if(expression.getType() == Operation.ReturnType.NUM) {
                        expression = new EqualsOperationNumber(expression, new ExpressionRule(Operation.ReturnType.NUM).assemble(tokenizer, parser), op.getPosition());
                    } else if(expression.getType() == Operation.ReturnType.STR) {
                        expression = new EqualsOperationString(expression, new ExpressionRule(Operation.ReturnType.STR).assemble(tokenizer, parser), op.getPosition());
                    }
                    break;
                case NOT_EQUALS_OPERATOR:
                    System.out.println("NEQ");
                    System.out.println(expression.getType());
                    if(expression.getType() == Operation.ReturnType.NUM) {
                        expression = new NotEqualsOperationNumber(expression, new ExpressionRule(Operation.ReturnType.NUM).assemble(tokenizer, parser), op.getPosition());
                    } else if(expression.getType() == Operation.ReturnType.STR) {
                        System.out.println("STRNEQ");
                        expression = new NotEqualsOperationString(expression, new ExpressionRule(Operation.ReturnType.STR).assemble(tokenizer, parser), op.getPosition());
                    }
                    break;
                case GREATER_THAN_OPERATOR:
                    expression = new GreaterThanOperationNumber(expression, new ExpressionRule(Operation.ReturnType.NUM).assemble(tokenizer, parser), op.getPosition());
                    break;
                case GREATER_THAN_OR_EQUALS_OPERATOR:
                    expression = new GreaterThanOrEqualsOperationNumber(expression, new ExpressionRule(Operation.ReturnType.NUM).assemble(tokenizer, parser), op.getPosition());
                    break;
                case LESS_THAN_OPERATOR:
                    expression = new LessThanOperationNumber(expression, new ExpressionRule(Operation.ReturnType.NUM).assemble(tokenizer, parser), op.getPosition());
                    break;
                case LESS_THAN_OR_EQUALS_OPERATOR:
                    expression = new LessThanOrEqualsOperationNumber(expression, new ExpressionRule(Operation.ReturnType.NUM).assemble(tokenizer, parser), op.getPosition());
                    break;
            }
        }

        return expression;
    }
}
