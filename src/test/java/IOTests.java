import com.dfsek.substrate.lang.std.function.StaticFunction;
import com.dfsek.substrate.parser.Parser;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;

/**
 * Test input/output from scripts.
 */
public class IOTests {
    private static final String returnInputScript = getScript("returnInput");
    private static final String compareInputsEquals = getScript("compareInputsEquals");

    @Test
    public void testSingleBooleanRecordInput() {

    }

    private static String getScript(String script) {
        try {
            return IOUtils.resourceToString("scripts/io/" + script + ".sbsc", Charset.defaultCharset());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public class Records {
        public record BooleanInput(boolean input) {
        }

        public record DoubleInput(double input) {
        }

        public record StringInput(String input) {
        }

        public record IntInput(int input) {
        }
    }
}
