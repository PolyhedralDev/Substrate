import com.dfsek.substrate.lang.rules.BaseRule;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Tokenizer;
import com.dfsek.substrate.tokenizer.exceptions.TokenizerException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.fail;


public class ScriptTest {
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
    public void voidReturn() {
        script("/valid/voidReturn.sbsc");
    }

    @Test
    public void addition() {
        script("/valid/addition.sbsc");
    }

    @Test
    public void noStatementEnd() {
        invalidScript("/invalid/noStatementEnd.sbsc");
    }

    @Test
    public void noKeywordID() {
        invalidScript("/invalid/noKeywordID.sbsc");
    }

    @Test
    public void invalidValue() {
        invalidScript("/invalid/invalidValue.sbsc");
    }

    @Test
    public void badIntCast() {
        invalidScript("/invalid/badIntCast.sbsc");
    }

    @Test
    public void badNumCast() {
        invalidScript("/invalid/badNumCast.sbsc");
    }

    @Test
    public void badStringCast() {
        invalidScript("/invalid/badStringCast.sbsc");
    }

    @Test
    public void noMultiCast() {
        invalidScript("/invalid/noMultiCast.sbsc");
    }

    @Test
    public void noValueReassignment() {
        invalidScript("invalid/noValueReassignment.sbsc");
    }

    @Test
    public void casts() {
        script("/valid/casts.sbsc");
    }

    @Test
    public void danglingCloseBrace() {
        invalidScript("/invalid/danglingCloseBrace.sbsc");
    }

    @Test
    public void noEndOfString() {
        invalidTokenStream("/invalid/noEndOfString.sbsc");
    }

    @Test
    public void eof() {
        invalidTokenStream("/invalid/eof.sbsc");
    }

    public void script(String file) {
        try {
            String data = IOUtils.toString(Objects.requireNonNull(ScriptTest.class.getResource(file)), StandardCharsets.UTF_8);
            Parser parser = new Parser(data, new BaseRule());
            parser.parse().execute(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void invalidScript(String file) {
        try {
            String data = IOUtils.toString(Objects.requireNonNull(ScriptTest.class.getResource(file)), StandardCharsets.UTF_8);
            Parser parser = new Parser(data, new BaseRule());
            parser.parse().execute(null);
        } catch (ParseException e) {
            return; // These scripts should fail to parse
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        fail(); // If it parsed, something is wrong.
    }

    public void invalidTokenStream(String file) {
        try {
            String data = IOUtils.toString(Objects.requireNonNull(ScriptTest.class.getResource(file)), StandardCharsets.UTF_8);
            Tokenizer tokenizer = new Tokenizer(data);

            while (tokenizer.hasNext()) {
                tokenizer.consume();
            }
        } catch (TokenizerException e) {
            return; // These scripts should fail to parse
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        fail(); // If it parsed, something is wrong.
    }
}
