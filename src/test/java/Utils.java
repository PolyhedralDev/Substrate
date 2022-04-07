import com.dfsek.substrate.lang.std.function.StaticFunction;
import com.dfsek.substrate.parser.Parser;
import org.junit.jupiter.api.Assertions;

public class Utils {
    static final String DISABLE_OPTIMISATION_PROPERTY = "substrate.DisableOptimisation";
    static final boolean STACK_TRACES_FOR_INVALID = true;
    static final boolean DUMP_TO_JARS = true;

    public static <P extends Record, R extends Record> Parser<P, R> createParser(String script, Class<P> params, Class<R> ret) throws NoSuchMethodException {
        Parser<P, R> parser = new Parser<>(script, params, ret);
        parser.registerFunction("fail", new StaticFunction(Utils.class.getMethod("fail")));
        parser.registerFunction("assert", new StaticFunction(Assertions.class.getMethod("assertTrue", boolean.class)));
        return parser;
    }

    public static void fail() {
        Assertions.fail();
    }
}
