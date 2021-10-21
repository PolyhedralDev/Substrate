import com.dfsek.substrate.lang.compiler.lambda.LambdaFactory;
import com.dfsek.substrate.lang.compiler.tuple.TupleFactory;
import com.dfsek.substrate.lang.compiler.type.DataType;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.rules.BaseRule;
import com.dfsek.substrate.parser.DynamicClassLoader;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.exception.ParseException;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.objectweb.asm.Opcodes;

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
    public void functionSimple() throws IOException {
        script("/functionSimple.sbsc");
    }

    @Test
    public void functionNesting() throws IOException {
        script("/functionNesting.sbsc");
    }

    @Test
    public void functionReturn() throws IOException {
        script("/functionReturn.sbsc");
    }

    @Test
    public void functionTuple() throws IOException {
        script("/functionTuple.sbsc");
    }

    @Test
    public void lambda() {
        DynamicClassLoader classLoader = new DynamicClassLoader();
        LambdaFactory lambdaFactory = new LambdaFactory(classLoader, new TupleFactory(classLoader));

        lambdaFactory.implement(new Signature(DataType.NUM, DataType.STR, DataType.BOOL), new Signature(DataType.NUM), (method, clazz) -> {
            method.visitLdcInsn(1.5);
            method.visitInsn(Opcodes.DRETURN);
        });
    }
}
