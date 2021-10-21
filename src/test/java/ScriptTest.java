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

import static org.junit.Assert.fail;

public class ScriptTest {
    public void script(String file) {
        try {
            String data = IOUtils.toString(ScriptTest.class.getResource(file), StandardCharsets.UTF_8);
            Parser parser = new Parser(data, new BaseRule());
            parser.parse().execute(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void invalidScript(String file) {
        try {
            String data = IOUtils.toString(ScriptTest.class.getResource(file), StandardCharsets.UTF_8);
            Parser parser = new Parser(data, new BaseRule());
            parser.parse().execute(null);
        } catch (ParseException e) {
            return; // These scripts should fail to parse
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        fail(); // If it parsed, something is wrong.
    }

    @Test
    public void script() {
        script("/valid/test.sbsc");
    }

    @Test
    public void tuple() {
        script("/valid/tuple.sbsc");
    }

    @Test
    public void scope() {
        script("/valid/scope.sbsc");
    }

    @Test
    public void functionSimple() {
        script("/valid/functionSimple.sbsc");
    }

    @Test
    public void functionNesting() {
        script("/valid/functionNesting.sbsc");
    }

    @Test
    public void functionReturn() {
        script("/valid/functionReturn.sbsc");
    }

    @Test
    public void functionTuple() {
        script("/valid/functionTuple.sbsc");
    }

    @Test
    public void noStatementEnd() {
        invalidScript("/invalid/noStatementEnd.sbsc");
    }

    @Test
    public void danglingCloseBrace() {
        invalidScript("/invalid/danglingCloseBrace.sbsc");
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
