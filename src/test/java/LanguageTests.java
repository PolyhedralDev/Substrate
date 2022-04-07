import com.dfsek.substrate.lang.std.function.StaticFunction;
import com.dfsek.substrate.lexer.Lexer;
import com.dfsek.substrate.lexer.exceptions.TokenizerException;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.Function1;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class LanguageTests {
    private static final String property = "substrate.DisableOptimisation";

    private static final boolean STACK_TRACES_FOR_INVALID = true;
    private static final boolean DUMP_TO_JARS = true;

    static {
        System.setProperty("substrate.Dump", Boolean.toString(DUMP_TO_JARS));
    }

    @TestFactory
    public Stream<DynamicContainer> tests() {
        return Stream.of(
                DynamicContainer.dynamicContainer("Parser Tests", Stream.of(
                        DynamicContainer.dynamicContainer("Optimised", Stream.of(
                                        register("Valid Scripts", Paths.get("src", "test", "resources", "scripts/language/parser", "valid"), this::createParserTestOptimised),
                                        register("Invalid Scripts", Paths.get("src", "test", "resources", "scripts/language/parser", "invalid"), this::createInvalidParserTestOptimised)
                                ).sorted(Comparator.comparing(DynamicNode::getDisplayName))
                        ),
                        DynamicContainer.dynamicContainer("Unoptimised", Stream.of(
                                        register("Valid Scripts", Paths.get("src", "test", "resources", "scripts/language/parser", "valid"), this::createParserTestUnoptimised),
                                        register("Invalid Scripts", Paths.get("src", "test", "resources", "scripts/language/parser", "invalid"), this::createInvalidParserTestUnoptimised)
                                ).sorted(Comparator.comparing(DynamicNode::getDisplayName))
                        )
                )),
                DynamicContainer.dynamicContainer("Tokenizer Tests", Stream.of(
                        register("Invalid Scripts", Paths.get("src", "test", "resources", "scripts/language/tokenizer", "invalid"), this::createInvalidLexerTest),
                        register("Valid Scripts", Paths.get("src", "test", "resources", "scripts/language/tokenizer", "valid"), this::createLexerTest)
                ).sorted(Comparator.comparing(DynamicNode::getDisplayName)))
        ).sorted(Comparator.comparing(DynamicNode::getDisplayName));
    }


    private Executable createParserTestOptimised(Path path) {
        return createParserTest(path, true);
    }

    private Executable createParserTestUnoptimised(Path path) {
        return createParserTest(path, false);
    }

    public record Input(boolean booleanInput) {
    }

    public record Output(boolean b) {
    }

    private Executable createParserTest(Path path, boolean optimised) {
        return () -> {
            try {
                String data = IOUtils.toString(new FileInputStream(path.toFile()), StandardCharsets.UTF_8);
                Parser<Input, Output> parser = Utils.createParser(data, Input.class, Output.class);
                System.setProperty(property, Boolean.toString(optimised));
                assertTrue(parser.parse().execute(new Input(true), null).b());
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
                Parser<Input, Output> parser = Utils.createParser(data, Input.class, Output.class);
                System.setProperty(property, Boolean.toString(optimised));
                parser.parse().execute(new Input(true), null);
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
