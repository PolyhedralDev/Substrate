import com.dfsek.substrate.lang.std.function.StaticFunction;
import com.dfsek.substrate.parser.Parser;
import org.junit.jupiter.api.Assertions;

public class Utils {
    public static Parser<LanguageTests.Input, LanguageTests.Output> createParser(String script) throws NoSuchMethodException {
        Parser<LanguageTests.Input, LanguageTests.Output> parser = new Parser<>(script, LanguageTests.Input.class, LanguageTests.Output.class);
        parser.registerFunction("fail", new StaticFunction(Utils.class.getMethod("fail")));
        parser.registerFunction("assert", new StaticFunction(Assertions.class.getMethod("assertTrue", boolean.class)));
        return parser;
    }

    public static void fail() {
        Assertions.fail();
    }
}
