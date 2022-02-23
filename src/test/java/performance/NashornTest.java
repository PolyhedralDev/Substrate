package performance;

import org.apache.commons.io.IOUtils;

import javax.script.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class NashornTest {
    public static void main(String... args) throws IOException, ScriptException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");


        String script = IOUtils.toString(NashornTest.class.getResource("/performance/nashorn.js"), StandardCharsets.UTF_8);

        CompiledScript compiledScript = ((Compilable) engine).compile(script);

        for (int i = 0; i < 20; i++) {
            long s = System.nanoTime();

            compiledScript.eval();

            long e = System.nanoTime();
            long d = e - s;

            System.out.println("Took " + ((double) d) / 1000000 + "ms");
        }

    }
}
