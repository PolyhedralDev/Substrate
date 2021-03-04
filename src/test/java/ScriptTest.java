import com.dfsek.terrascript.lang.Rule;
import com.dfsek.terrascript.lang.RuleMatcher;
import com.dfsek.terrascript.lang.impl.rule.IdRule;
import com.dfsek.terrascript.lang.impl.rule.variable.declaration.NumberVariableDeclarationRule;
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
        parser.expectStart(new RuleMatcher() {
            @Override
            public Rule match(Token initial, TokenView view) throws ParseException {
                return new IdRule();
            }
        });

        parser.addRule(Token.Type.NUMBER_VARIABLE, new RuleMatcher() {
            @Override
            public Rule match(Token initial, TokenView view) throws ParseException {
                return new NumberVariableDeclarationRule();
            }
        });
        parser.parse().execute(null);
    }

}
