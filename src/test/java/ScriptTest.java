import com.dfsek.substrate.lang.compiler.DataType;
import com.dfsek.substrate.lang.compiler.LambdaFactory;
import com.dfsek.substrate.lang.compiler.Signature;
import com.dfsek.substrate.lang.compiler.TupleFactory;
import com.dfsek.substrate.lang.rules.BaseRule;
import com.dfsek.substrate.parser.DynamicClassLoader;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.exception.ParseException;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
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

    @Test
    public void lambda() {
        DynamicClassLoader classLoader = new DynamicClassLoader();
        LambdaFactory lambdaFactory = new LambdaFactory(classLoader, new TupleFactory(classLoader));

        lambdaFactory.generate(new Signature(DataType.NUM, DataType.STR, DataType.BOOL), new Signature(DataType.STR, DataType.NUM));
    }
}
