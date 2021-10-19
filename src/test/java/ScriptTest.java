import com.dfsek.substrate.lang.rules.BlockRule;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Tokenizer;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ScriptTest {
    @Test
    public void script() throws IOException, ParseException {
        String data = IOUtils.toString(ScriptTest.class.getResource("/test.sbsc"), StandardCharsets.UTF_8);
        Parser parser = new Parser(data, new BlockRule());

        parser.parse().execute(null);
    }

    @Test
    public void tokenStream() throws ParseException, IOException {
        Tokenizer tokenizer = new Tokenizer(IOUtils.toString(ScriptTest.class.getResource("/test.sbsc"), StandardCharsets.UTF_8));

        while(tokenizer.hasNext()) {
            System.out.println(tokenizer.consume());
        }
    }
}
