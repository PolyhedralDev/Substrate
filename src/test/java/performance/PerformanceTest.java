package performance;

import com.dfsek.substrate.Script;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.rules.BaseRule;
import com.dfsek.substrate.parser.Parser;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.nio.charset.Charset;

public class PerformanceTest {
    public static void main(String... args) throws IOException {
        Parser parser = new Parser(IOUtils.toString(PerformanceTest.class.getResource("/performance/performance.sbsc"), Charset.defaultCharset()), new BaseRule());

        parser.registerFunction("assert", new com.dfsek.substrate.lang.compiler.api.Function() {
            @Override
            public Signature arguments() {
                return Signature.bool();
            }

            @Override
            public void invoke(MethodBuilder visitor, BuildData data, Signature args) {
                visitor.invokeStatic(
                        CompilerUtil.internalName(Assertions.class),
                        "assertTrue",
                        "(Z)V");
            }

            @Override
            public Signature reference() {
                return Signature.fun().applyGenericArgument(0, Signature.bool());
            }
        });


        Script script = parser.parse();

        for (int i = 0; i < 20; i++) {
            long s = System.nanoTime();

            script.execute(null);

            long e = System.nanoTime();
            long d = e - s;

            System.out.println("Took " + ((double) d) / 1000000 + "ms");
        }
    }
}
