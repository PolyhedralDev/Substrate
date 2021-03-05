import com.dfsek.terrascript.lang.Rule;
import com.dfsek.terrascript.lang.RuleMatcher;
import com.dfsek.terrascript.lang.impl.operations.variable.declaration.StringVariableDeclarationOperation;
import com.dfsek.terrascript.lang.impl.rule.IdRule;
import com.dfsek.terrascript.lang.impl.rule.StatementRule;
import com.dfsek.terrascript.lang.impl.rule.looplike.IfStatementRule;
import com.dfsek.terrascript.lang.impl.rule.looplike.WhileLoopRule;
import com.dfsek.terrascript.lang.impl.rule.match.IdentifierRuleMatcher;
import com.dfsek.terrascript.lang.impl.rule.variable.declaration.BooleanVariableDeclarationRule;
import com.dfsek.terrascript.lang.impl.rule.variable.declaration.NumberVariableDeclarationRule;
import com.dfsek.terrascript.lang.impl.rule.variable.declaration.StringVariableDeclarationRule;
import com.dfsek.terrascript.parser.Parser;
import com.dfsek.terrascript.parser.TokenView;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Token;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ScriptTest {
    @Test
    public void script() throws IOException, ParseException {
        String data = IOUtils.toString(ScriptTest.class.getResource("/test.tesf"), StandardCharsets.UTF_8);
        Parser parser = new Parser(data);
        parser.expectStart((initial, view) -> new IdRule());

        parser.addRule(Token.Type.NUMBER_VARIABLE, (initial, view) -> new NumberVariableDeclarationRule());
        parser.addRule(Token.Type.STRING_VARIABLE, (initial, view) -> new StringVariableDeclarationRule());
        parser.addRule(Token.Type.BOOLEAN_VARIABLE, (initial, view) -> new BooleanVariableDeclarationRule());
        parser.addRule(Token.Type.IDENTIFIER, new IdentifierRuleMatcher());


        parser.addRule(Token.Type.STATEMENT_END, (initial, view) -> new StatementRule());
        parser.addRule(Token.Type.IF_STATEMENT, (initial, view) -> new IfStatementRule());
        parser.addRule(Token.Type.WHILE_LOOP, (initial, view) -> new WhileLoopRule());
        parser.parse().execute(null);
    }

}
