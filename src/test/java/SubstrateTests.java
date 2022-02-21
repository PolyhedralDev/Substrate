import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.rules.BaseRule;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Tokenizer;
import com.dfsek.substrate.tokenizer.exceptions.TokenizerException;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;

public class SubstrateTests {

    private static final String property = "substrate.DisableOptimisation";

    static {
        System.setProperty("substrate.Dump", Boolean.toString(false));
    }

    private Parser createParser(String script) {
        Parser parser = new Parser(script, new BaseRule());
        parser.registerFunction("fail", new com.dfsek.substrate.lang.compiler.api.Function() {
            @Override
            public Signature arguments() {
                return Signature.empty();
            }

            @Override
            public void invoke(MethodBuilder visitor, BuildData data, Signature args) {
                visitor.invokeStatic(
                        CompilerUtil.internalName(Assertions.class),
                        "fail",
                        "()V");
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
            public void invoke(MethodBuilder visitor, BuildData data, Signature args) {
                visitor.invokeStatic(
                        CompilerUtil.internalName(Assertions.class),
                        "assertTrue",
                        "(Z)V");
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
        List<DynamicNode> nodes = new ArrayList<>();

        List<DynamicNode> parserNodes = new ArrayList<>();
        parserNodes.add(register("Valid Scripts", Paths.get("src", "test", "resources", "parser", "valid"), path -> () -> {
            try {
                String data = IOUtils.toString(new FileInputStream(path.toFile()), StandardCharsets.UTF_8);
                Parser parser = createParser(data);
                System.setProperty(property, Boolean.toString(optimisations));
                parser.parse().execute(null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
        parserNodes.add(register("Invalid Scripts", Paths.get("src", "test", "resources", "parser", "invalid"), path -> () -> {
            try {
                String data = IOUtils.toString(new FileInputStream(path.toFile()), StandardCharsets.UTF_8);
                Parser parser = createParser(data);
                System.setProperty(property, Boolean.toString(optimisations));
                parser.parse().execute(null);
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            fail();
        }));
        nodes.add(DynamicContainer.dynamicContainer("Parser Tests", parserNodes.stream().sorted(Comparator.comparing(DynamicNode::getDisplayName))));

        List<DynamicNode> tokenizerNodes = new ArrayList<>();

        tokenizerNodes.add(register("Invalid Scripts", Paths.get("src", "test", "resources", "tokenizer", "invalid"), path -> () -> {
            try {
                String data = IOUtils.toString(new FileInputStream(path.toFile()), StandardCharsets.UTF_8);
                Tokenizer tokenizer = new Tokenizer(data);

                while (tokenizer.hasNext()) {
                    tokenizer.consume();
                }
            } catch (TokenizerException e) {
                e.printStackTrace();
                return; // These scripts should fail to parse
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            fail(); // If it parsed, something is wrong.
        }));

        tokenizerNodes.add(register("Valid Scripts", Paths.get("src", "test", "resources", "tokenizer", "valid"), path -> () -> {
            try {
                String data = IOUtils.toString(new FileInputStream(path.toFile()), StandardCharsets.UTF_8);
                Tokenizer tokenizer = new Tokenizer(data);

                while (tokenizer.hasNext()) {
                    tokenizer.consume();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));

        nodes.add(DynamicContainer.dynamicContainer("Tokenizer Tests", tokenizerNodes.stream().sorted(Comparator.comparing(DynamicNode::getDisplayName))));

        return DynamicContainer.dynamicContainer(optimisations ? "Optimised Compilation (default)" : "Unoptimised Compilation", nodes.stream().sorted(Comparator.comparing(DynamicNode::getDisplayName)));
    }

    public DynamicContainer register(String name, Path parent, Function<Path, Executable> executable) {
        List<DynamicNode> nodes = new ArrayList<>();
        try {
            Files.walk(parent, 1)
                    .forEach(path -> {
                        if (path.equals(parent)) return;
                        String testName = parent.relativize(path).toString();
                        if (path.toFile().isDirectory()) {
                            nodes.add(register(testName, parent.resolve(testName), executable));
                        } else {
                            nodes.add(DynamicTest.dynamicTest(testName, executable.apply(path)));
                        }
                    });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return DynamicContainer.dynamicContainer(name, nodes.stream().sorted(Comparator.comparing(DynamicNode::getDisplayName)));
    }
}
