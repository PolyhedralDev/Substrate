package com.dfsek.substrate.lang.rules.value;

import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.value.ValueAssignmentNode;
import com.dfsek.substrate.lang.rules.expression.ExpressionRule;
import com.dfsek.substrate.parser.ParserScope;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

public class ValueAssignmentRule implements Rule {
    private static final ValueAssignmentRule INSTANCE = new ValueAssignmentRule();

    public static ValueAssignmentRule getInstance() {
        return INSTANCE;
    }

    @Override
    public ValueAssignmentNode assemble(Tokenizer tokenizer, ParseData data, ParserScope scope) throws ParseException {
        Token id = ParserUtil.checkType(tokenizer.consume(), Token.Type.IDENTIFIER);
        ParserUtil.checkType(tokenizer.consume(), Token.Type.ASSIGNMENT); // next token should be =
        ExpressionNode value = ExpressionRule.getInstance().assemble(tokenizer, data, scope);
        return new ValueAssignmentNode(id, value);
    }
}
