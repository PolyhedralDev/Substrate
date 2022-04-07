import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test input/output from scripts.
 */
public class IOTests {
    private static final String returnInputScript = getScript("returnInput");
    private static final String compareInputsEquals = getScript("compareInputsEquals");

    @Test
    public void testSingleBooleanRecordInput() throws NoSuchMethodException {
        assertTrue(Utils.createParser(returnInputScript, Records.BooleanInput.class, Records.BooleanInput.class).parse().execute(new Records.BooleanInput(true), null).input());
    }

    @Test
    public void testSingleIntRecordInput() throws NoSuchMethodException {
        assertEquals(5, Utils.createParser(returnInputScript, Records.IntInput.class, Records.IntInput.class).parse().execute(new Records.IntInput(5), null).input());
    }

    @Test
    public void testSingleDoubleRecordInput() throws NoSuchMethodException {
        assertEquals(5.5, Utils.createParser(returnInputScript, Records.DoubleInput.class, Records.DoubleInput.class).parse().execute(new Records.DoubleInput(5.5), null).input());
    }

    @Test
    public void testSingleStringRecordInput() throws NoSuchMethodException {
        assertEquals("bazinga", Utils.createParser(returnInputScript, Records.StringInput.class, Records.StringInput.class).parse().execute(new Records.StringInput("bazinga"), null).input());
    }

    @Test
    public void testIntEquals() throws NoSuchMethodException {
        assertTrue(Utils.createParser(compareInputsEquals, Records.TwoInts.class, Records.BooleanInput.class).parse().execute(new Records.TwoInts(5, 5), null).input());
    }

    @Test
    public void testDoubleEquals() throws NoSuchMethodException {
        assertTrue(Utils.createParser(compareInputsEquals, Records.TwoDoubles.class, Records.BooleanInput.class).parse().execute(new Records.TwoDoubles(5.5, 5.5), null).input());
    }

    @Test
    public void testStringEquals() throws NoSuchMethodException {
        assertTrue(Utils.createParser(compareInputsEquals, Records.TwoStrings.class, Records.BooleanInput.class).parse().execute(new Records.TwoStrings("bazinga", "bazinga"), null).input());
    }

    private static String getScript(String script) {
        try {
            return IOUtils.resourceToString("/scripts/io/" + script + ".sbsc", Charset.defaultCharset());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
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
    }
}
