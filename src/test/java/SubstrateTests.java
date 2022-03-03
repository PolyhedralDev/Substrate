import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.rules.BaseRule;
import com.dfsek.substrate.lexer.Lexer;
import com.dfsek.substrate.lexer.exceptions.TokenizerException;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.exception.ParseException;
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
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;

public class SubstrateTests {

    private static final String property = "substrate.DisableOptimisation";

    private static final boolean STACK_TRACES_FOR_INVALID = false;
    private static final boolean DUMP_TO_JARS = true;

    static {
        System.setProperty("substrate.Dump", Boolean.toString(DUMP_TO_JARS));
    }

    private Parser createParser(String script) {
        Parser parser = new Parser(script, new BaseRule());
        parser.registerFunction("fail", new com.dfsek.substrate.lang.compiler.api.Function() {
            @Override
            public Signature arguments() {
                return Signature.empty();
            }

            @Override
            public List<Either<CompileError, Op>> invoke(BuildData data, Signature args) {
                return List.of(Op.invokeStatic(
                        CompilerUtil.internalName(Assertions.class),
                        "fail",
                        "()V"));
            }

            @Override
            public Signature reference() {
                return Signature.fun();
            }
        });
        parser.registerFunction("assert", new com.dfsek.substrate.lang.compiler.api.Function() {
            @Override
            public Signature arguments() {
                return Signature.bool();
            }

            @Override
            public List<Either<CompileError, Op>> invoke(BuildData data, Signature args) {
                return List.of(Op.invokeStatic(
                        CompilerUtil.internalName(Assertions.class),
                        "assertTrue",
                        "(Z)V"));
            }

            @Override
            public Signature reference() {
                return Signature.fun().applyGenericArgument(0, Signature.bool());
            }
        });
        return parser;
    }

    @TestFactory
    public Stream<DynamicNode> tests() {
        return Stream.of(tests(true), tests(false));
    }

    public DynamicNode tests(boolean optimisations) {
        List<DynamicNode> parserNodes = List.of(
                register("Valid Scripts", Paths.get("src", "test", "resources", "parser", "valid"), path -> () -> {
                    try {
                        String data = IOUtils.toString(new FileInputStream(path.toFile()), StandardCharsets.UTF_8);
                        Parser parser = createParser(data);
                        System.setProperty(property, Boolean.toString(optimisations));
                        parser.parse().execute(null);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }),
                register("Invalid Scripts", Paths.get("src", "test", "resources", "parser", "invalid"), path -> () -> {
                    try {
                        String data = IOUtils.toString(new FileInputStream(path.toFile()), StandardCharsets.UTF_8);
                        Parser parser = createParser(data);
                        System.setProperty(property, Boolean.toString(optimisations));
                        parser.parse().execute(null);
                    } catch (ParseException e) {
                        if (STACK_TRACES_FOR_INVALID) e.printStackTrace();
                        return;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    fail();
                })
        );

        List<DynamicNode> tokenizerNodes = List.of(
                register("Invalid Scripts", Paths.get("src", "test", "resources", "tokenizer", "invalid"), path -> () -> {
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
                }),
                register("Valid Scripts", Paths.get("src", "test", "resources", "tokenizer", "valid"), path -> () -> {
                    try {
                        String data = IOUtils.toString(new FileInputStream(path.toFile()), StandardCharsets.UTF_8);
                        Lexer lexer = new Lexer(data);

                        while (lexer.hasNext()) {
                            lexer.consume();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
        );

        List<DynamicNode> nodes = List.of(
                DynamicContainer.dynamicContainer("Parser Tests", parserNodes.toStream().sorted(Comparator.comparing(DynamicNode::getDisplayName))),
                DynamicContainer.dynamicContainer("Tokenizer Tests", tokenizerNodes.toStream().sorted(Comparator.comparing(DynamicNode::getDisplayName)))
        );

        return DynamicContainer.dynamicContainer(optimisations ? "Optimised Compilation (default)" : "Unoptimised Compilation", nodes.toStream().sorted(Comparator.comparing(DynamicNode::getDisplayName)));
    }

    public DynamicContainer register(String name, Path parent, Function<Path, Executable> executable) {
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
