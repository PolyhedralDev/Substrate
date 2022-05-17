import com.dfsek.substrate.environment.Environment;
import com.dfsek.substrate.environment.IO;
import com.dfsek.substrate.lang.std.function.StaticFunction;
import com.dfsek.substrate.parser.Parser;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test input/output from scripts.
 */
public class IOTests {
    private static final String returnInputScript = getScript("returnInput");
    private static final String compareInputsEquals = getScript("compareInputsEquals");
    private static final String returnTupleIntDoubleString = getScript("returnTupleIntDoubleString");
    private static final String inputClosureInt = getScript("inputClosureInt");

    private static final String basicMonadic = getScript("basicMonadic");

    static {
        System.setProperty("substrate.Dump", Boolean.toString(Utils.DUMP_TO_JARS));
    }

    @Test
    public void singleBooleanRecordInput() throws NoSuchMethodException {
        assertTrue(Utils.createParser(returnInputScript, Records.BooleanInput.class, Records.BooleanInput.class, true).parse().execute(new Records.BooleanInput(true), null).input());
    }

    @Test
    public void singleIntRecordInput() throws NoSuchMethodException {
        assertEquals(5, Utils.createParser(returnInputScript, Records.IntInput.class, Records.IntInput.class, true).parse().execute(new Records.IntInput(5), null).input());
    }

    @Test
    public void singleDoubleRecordInput() throws NoSuchMethodException {
        assertEquals(5.5, Utils.createParser(returnInputScript, Records.DoubleInput.class, Records.DoubleInput.class, true).parse().execute(new Records.DoubleInput(5.5), null).input());
    }

    @Test
    public void singleStringRecordInput() throws NoSuchMethodException {
        assertEquals("bazinga", Utils.createParser(returnInputScript, Records.StringInput.class, Records.StringInput.class, true).parse().execute(new Records.StringInput("bazinga"), null).input());
    }

    @Test
    public void intEquals() throws NoSuchMethodException {
        assertTrue(Utils.createParser(compareInputsEquals, Records.TwoInts.class, Records.BooleanInput.class, true).parse().execute(new Records.TwoInts(5, 5), null).input());
    }

    @Test
    public void doubleEquals() throws NoSuchMethodException {
        assertTrue(Utils.createParser(compareInputsEquals, Records.TwoDoubles.class, Records.BooleanInput.class, true).parse().execute(new Records.TwoDoubles(5.5, 5.5), null).input());
    }

    @Test
    public void stringEquals() throws NoSuchMethodException {
        assertTrue(Utils.createParser(compareInputsEquals, Records.TwoStrings.class, Records.BooleanInput.class, true).parse().execute(new Records.TwoStrings("bazinga", "bazinga"), null).input());
    }

    @Test
    public void returnTupleIntDoubleString() throws NoSuchMethodException {
        assertEquals(new Records.IntDoubleString(5, 4.3, "bazinga"), Utils.createParser(returnTupleIntDoubleString, Records.Void.class, Records.IntDoubleString.class, true).parse().execute(new Records.Void(), null));
    }

    @Test
    public void basicMonadic() throws NoSuchMethodException {
        Records.IOOut.BasicEnvironment environment = new Records.IOOut.BasicEnvironment();
        Parser<Records.Void, Records.IOOut> parser = Utils.createParser(basicMonadic, Records.Void.class, Records.IOOut.class, true);

        parser.registerFunction("putLine", new StaticFunction(IOTests.class.getMethod("putLine", String.class)));

        parser.parse().execute(new Records.Void(), environment).io().apply(environment);
    }

    @Test
    public void inputClosureInt() throws NoSuchMethodException {
        System.setProperty(Utils.DISABLE_OPTIMISATION_PROPERTY, "true");
        assertEquals(5, Utils.createParser(inputClosureInt, Records.IntInput.class, Records.IntInput.class, true).parse().execute(new Records.IntInput(5), null).input());
        System.clearProperty(Utils.DISABLE_OPTIMISATION_PROPERTY);
    }


    private static String getScript(String script) {
        try {
            return IOUtils.resourceToString("/scripts/io/" + script + ".sbsc", Charset.defaultCharset());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static IO<Records.IOOut.BasicEnvironment> putLine(String in) {
        return env -> env.getOut().println(in);
    }

    public static class Records {
        public record BooleanInput(boolean input) {
        }

        public record DoubleInput(double input) {
        }

        public record StringInput(String input) {
        }

        public record IntInput(int input) {
        }


        public record TwoDoubles(double input1, double input2) {
        }

        public record TwoStrings(String input1, String input2) {
        }

        public record TwoInts(int input1, int input2) {
        }

        public record IntDoubleString(int input1, double input2, String input3) {
        }

        public record Void() {
        }

        public record IOOut(IO<BasicEnvironment> io) {
            public static final class BasicEnvironment implements Environment {
                public PrintStream getOut() {
                    return System.out;
                }
            }
        }
    }
}
