package com.dfsek.substrate.lang.impl.rule;

import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.impl.operations.comparison.EqualsOperation;
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
                    expression = new DivisionOperation(expression, new ExpressionRule(type).assemble(tokenizer, parser), op.getPosition());
                    break;
                case MODULO_OPERATOR:
                    expression = new ModuloOperation(expression, new ExpressionRule(type).assemble(tokenizer, parser), op.getPosition());
                    break;
                case EQUALS_OPERATOR:
                    expression = new EqualsOperation(expression, new ExpressionRule(type).assemble(tokenizer, parser), op.getPosition());
                    break;
            }
        }

        return expression;
    }
}
