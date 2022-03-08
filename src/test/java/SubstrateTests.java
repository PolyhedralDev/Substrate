import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.rules.BaseRule;
import com.dfsek.substrate.lang.std.function.StaticFunction;
import com.dfsek.substrate.lexer.Lexer;
import com.dfsek.substrate.lexer.exceptions.TokenizerException;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.Function1;
import io.vavr.collection.List;
import io.vavr.control.Either;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;

public class SubstrateTests {

    private static final String property = "substrate.DisableOptimisation";

    private static final boolean STACK_TRACES_FOR_INVALID = false;
    private static final boolean DUMP_TO_JARS = true;

    static {
        System.setProperty("substrate.Dump", Boolean.toString(DUMP_TO_JARS));
    }

    private Parser createParser(String script) throws NoSuchMethodException {
        Parser parser = new Parser(script, new BaseRule());
        parser.registerFunction("fail", new StaticFunction(SubstrateTests.class.getMethod("fail")));
        parser.registerFunction("assert", new StaticFunction(Assertions.class.getMethod("assertTrue", boolean.class)));
        return parser;
    }

    public static void fail() {
        Assertions.fail();
    }

    @TestFactory
    public Stream<DynamicContainer> tests() {
        return Stream.of(
                DynamicContainer.dynamicContainer("Parser Tests", Stream.of(
                        DynamicContainer.dynamicContainer("Optimised", Stream.of(
                                        register("Valid Scripts", Paths.get("src", "test", "resources", "parser", "valid"), this::createParserTestOptimised),
                                        register("Invalid Scripts", Paths.get("src", "test", "resources", "parser", "invalid"), this::createInvalidParserTestOptimised)
                                ).sorted(Comparator.comparing(DynamicNode::getDisplayName))
                        ),
                        DynamicContainer.dynamicContainer("Unoptimised", Stream.of(
                                        register("Valid Scripts", Paths.get("src", "test", "resources", "parser", "valid"), this::createParserTestUnoptimised),
                                        register("Invalid Scripts", Paths.get("src", "test", "resources", "parser", "invalid"), this::createInvalidParserTestUnoptimised)
                                ).sorted(Comparator.comparing(DynamicNode::getDisplayName))
                        )
                )),
                DynamicContainer.dynamicContainer("Tokenizer Tests", Stream.of(
                        register("Invalid Scripts", Paths.get("src", "test", "resources", "tokenizer", "invalid"), this::createInvalidLexerTest),
                        register("Valid Scripts", Paths.get("src", "test", "resources", "tokenizer", "valid"), this::createLexerTest)
                ).sorted(Comparator.comparing(DynamicNode::getDisplayName)))
        ).sorted(Comparator.comparing(DynamicNode::getDisplayName));
    }


    private Executable createParserTestOptimised(Path path) {
        return createParserTest(path, true);
    }

    private Executable createParserTestUnoptimised(Path path) {
        return createParserTest(path, false);
    }

    private Executable createParserTest(Path path, boolean optimised) {
        return () -> {
            try {
                String data = IOUtils.toString(new FileInputStream(path.toFile()), StandardCharsets.UTF_8);
                Parser parser = createParser(data);
                System.setProperty(property, Boolean.toString(optimised));
                parser.parse().execute(null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    private Executable createInvalidParserTestOptimised(Path path) {
        return createInvalidParserTest(path, true);
    }

    private Executable createInvalidParserTestUnoptimised(Path path) {
        return createInvalidParserTest(path, false);
    }

    private Executable createInvalidParserTest(Path path, boolean optimised) {
        return () -> {
            try {
                String data = IOUtils.toString(new FileInputStream(path.toFile()), StandardCharsets.UTF_8);
                Parser parser = createParser(data);
                System.setProperty(property, Boolean.toString(optimised));
                parser.parse().execute(null);
            } catch (ParseException e) {
                if (STACK_TRACES_FOR_INVALID) e.printStackTrace();
                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            fail();
        };
    }

    private Executable createLexerTest(Path path) {
        return () -> {
            try {
                String data = IOUtils.toString(new FileInputStream(path.toFile()), StandardCharsets.UTF_8);
                Lexer lexer = new Lexer(data);

                while (lexer.hasNext()) {
                    lexer.consume();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private Executable createInvalidLexerTest(Path path) {
        return () -> {
            try {
                String data = IOUtils.toString(new FileInputStream(path.toFile()), StandardCharsets.UTF_8);
                Lexer lexer = new Lexer(data);

                while (lexer.hasNext()) {
                    lexer.consume();
                }
            } catch (TokenizerException e) {
                if (STACK_TRACES_FOR_INVALID) e.printStackTrace();
                return; // These scripts should fail to parse
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            fail(); // If it parsed, something is wrong.
        };
    }

    public DynamicContainer register(String name, Path parent, Function1<Path, Executable> executable) {
        try {
            return DynamicContainer.dynamicContainer(name, Files.walk(parent, 1)
                    .flatMap(path -> {
                        if (path.equals(parent)) return Stream.empty();
                        String testName = parent.relativize(path).toString();
                        if (path.toFile().isDirectory()) {
                            return Stream.of(register(testName, parent.resolve(testName), executable));
                        } else {
                            return Stream.of(DynamicTest.dynamicTest(testName, executable.apply(path)));
                        }
                    })
                    .sorted(Comparator.comparing(DynamicNode::getDisplayName)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


}
