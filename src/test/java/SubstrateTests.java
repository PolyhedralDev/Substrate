import com.dfsek.substrate.lang.compiler.build.BuildData;
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
import org.objectweb.asm.MethodVisitor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;

public class SubstrateTests {

    private Parser createParser(String script) {
        Parser parser = new Parser(script, new BaseRule());
        parser.registerFunction("fail", new com.dfsek.substrate.lang.compiler.api.Function() {
            @Override
            public Signature arguments() {
                return Signature.empty();
            }

            @Override
            public void invoke(MethodVisitor visitor, BuildData data, Signature args) {
                visitor.visitMethodInsn(INVOKESTATIC,
                        CompilerUtil.internalName(Assertions.class),
                        "fail",
                        "()V",
                        false);
            }

            @Override
            public Signature reference(BuildData data) {
                return Signature.fun();
            }
        });
        parser.registerFunction("assert", new com.dfsek.substrate.lang.compiler.api.Function() {
            @Override
            public Signature arguments() {
                return Signature.bool();
            }

            @Override
            public void invoke(MethodVisitor visitor, BuildData data, Signature args) {
                visitor.visitMethodInsn(INVOKESTATIC,
                        CompilerUtil.internalName(Assertions.class),
                        "assertTrue",
                        "(Z)V",
                        false);
            }

            @Override
            public Signature reference(BuildData data) {
                return Signature.fun().applyGenericArgument(0, Signature.bool());
            }
        });
        return parser;
    }

    @TestFactory
    public Stream<DynamicNode> tests() {
        List<DynamicNode> nodes = new ArrayList<>();

        List<DynamicNode> parserNodes = new ArrayList<>();
        parserNodes.add(register("valid", Paths.get("src", "test", "resources", "parser", "valid"), path -> () -> {
            try {
                String data = IOUtils.toString(new FileInputStream(path.toFile()), StandardCharsets.UTF_8);
                Parser parser = createParser(data);
                parser.parse().execute(null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
        parserNodes.add(register("invalid", Paths.get("src", "test", "resources", "parser", "invalid"), path -> () -> {
            try {
                String data = IOUtils.toString(new FileInputStream(path.toFile()), StandardCharsets.UTF_8);
                Parser parser = createParser(data);
                parser.parse().execute(null);
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            fail();
        }));
        nodes.add(DynamicContainer.dynamicContainer("parser", parserNodes.stream().sorted(Comparator.comparing(DynamicNode::getDisplayName))));

        List<DynamicNode> tokenizerNodes = new ArrayList<>();

        tokenizerNodes.add(register("invalid", Paths.get("src", "test", "resources", "tokenizer", "invalid"), path -> () -> {
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

        tokenizerNodes.add(register("valid", Paths.get("src", "test", "resources", "tokenizer", "valid"), path -> () -> {
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

        nodes.add(DynamicContainer.dynamicContainer("tokenizer", tokenizerNodes.stream().sorted(Comparator.comparing(DynamicNode::getDisplayName))));

        return nodes.stream().sorted(Comparator.comparing(DynamicNode::getDisplayName));
    }

    public DynamicContainer register(String name, Path parent, Function<Path, Executable> executable) {
        List<DynamicNode> nodes = new ArrayList<>();
        try {
            Files.walk(parent, 1)
                    .forEach(path -> {
                        if(path.equals(parent)) return;
                        String testName = parent.relativize(path).toString();
                        if(path.toFile().isDirectory()) {
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
