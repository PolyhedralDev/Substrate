import com.dfsek.substrate.lang.compiler.DataType;
import com.dfsek.substrate.lang.compiler.Signature;
import com.dfsek.substrate.lang.compiler.TupleFactory;
import com.dfsek.substrate.lang.internal.Lambda;
import com.dfsek.substrate.lang.rules.BaseRule;
import com.dfsek.substrate.parser.DynamicClassLoader;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Tokenizer;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

public class ScriptTest {
    public void script(String file) throws IOException, ParseException {
        String data = IOUtils.toString(ScriptTest.class.getResource(file), StandardCharsets.UTF_8);
        Parser parser = new Parser(data, new BaseRule());
        parser.parse().execute(null);
    }

    @Test
    public void script() throws IOException {
        script("/test.sbsc");
    }

    @Test
    public void tuple() throws IOException {
        script("/tuple.sbsc");
    }

    @Test
    public void scope() throws IOException {
        script("/scope.sbsc");
    }
}
