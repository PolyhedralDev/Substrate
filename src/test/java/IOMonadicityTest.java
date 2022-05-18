import com.dfsek.substrate.environment.Environment;
import com.dfsek.substrate.environment.IO;
import com.dfsek.substrate.environment.io.IOFunctionObj2Obj;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IOMonadicityTest {
    @Test
    public void leftIdentity() {
        TestEnvironment environment = new TestEnvironment();

        IOFunctionObj2Obj<String, IO<String, TestEnvironment>> f = (env, in) -> IO.unit(in.toUpperCase(Locale.ROOT));

        String value = "hello";

        IO<String, TestEnvironment> bind = IO.bind(environment, IO.unit(value), f);
        IO<String, TestEnvironment> apply = f.apply(environment, value);

        assertEquals(bind.apply(environment), apply.apply(environment));
    }

    @Test
    public void rightIdentity() {
        TestEnvironment environment = new TestEnvironment();

        IO<String, TestEnvironment> io = IO.unit("hello");

        IO<String, TestEnvironment> bind = IO.bind(environment, io, (env, s) -> IO.unit(s));

        assertEquals(io.apply(environment), bind.apply(environment));
    }

    @Test
    public void associativity() {
        TestEnvironment environment = new TestEnvironment();

        IO<String, TestEnvironment> io = IO.unit("hello");

        IOFunctionObj2Obj<String, IO<String, TestEnvironment>> f = (env, in) -> IO.unit(in.toUpperCase(Locale.ROOT));
        IOFunctionObj2Obj<String, IO<String, TestEnvironment>> g = (env, in) -> IO.unit(in + " world");

        IO<String, TestEnvironment> bind =
                IO.bind(environment,
                        IO.bind(environment,
                                io, f), g
                );


        IO<String, TestEnvironment> bind2 = IO.bind(environment, io, (e, m) -> g.apply(e, f.apply(e, m).apply(environment)));
        assertEquals(bind.apply(environment), bind2.apply(environment));
    }

    public static final class TestEnvironment implements Environment {

    }
}
