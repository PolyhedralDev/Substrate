import com.dfsek.terrascript.lang.impl.rule.IdRule;
import com.dfsek.terrascript.lang.impl.rule.StatementRule;
import com.dfsek.terrascript.parser.Parser;
import com.dfsek.terrascript.parser.exception.ParseException;
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

        parser.expectDefault((initial, view) -> new StatementRule());
        parser.parse().execute(null);
    }
}
