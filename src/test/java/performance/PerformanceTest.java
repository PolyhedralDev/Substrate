package performance;

import com.dfsek.substrate.Script;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.parser.Parser;
import io.vavr.collection.List;
import io.vavr.control.Either;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;

public class PerformanceTest {

    public record Input(boolean b) {
    }

    public record Output(boolean b) {
    }

    public static void main(String... args) throws IOException {
        Parser<Input, Output> parser = new Parser<>(IOUtils.toString(Objects.requireNonNull(PerformanceTest.class.getResource("/scripts/performance/performance.sbsc")), Charset.defaultCharset()), Input.class, Output.class);

        parser.registerFunction("assert", new com.dfsek.substrate.lang.compiler.api.Function() {
            @Override
            public Signature arguments() {
                return Signature.bool();
            }

            @Override
            public List<Either<CompileError, Op>> invoke(BuildData data, Signature args) {
                return List.of(Op.invokeStatic(
                        CompilerUtil.internalName(Assertions.class),
                        "assertTrue",
                        "(Z)V"));
            }

            @Override
            public Signature reference() {
                return Signature.fun().applyGenericArgument(0, Signature.bool());
            }
        });


        Script<Input, Output> script = parser.parse();

        for (int i = 0; i < 20; i++) {
            long s = System.nanoTime();

            script.execute(new Input(false), null);

            long e = System.nanoTime();
            long d = e - s;

            System.out.println("Took " + ((double) d) / 1000000 + "ms");
        }
    }
}
