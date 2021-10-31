import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.rules.BaseRule;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Tokenizer;
import com.dfsek.substrate.tokenizer.exceptions.TokenizerException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.objectweb.asm.MethodVisitor;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.fail;

public class SubstrateTests {
    private final List<DynamicTest> tests = new ArrayList<>();

    private Parser createParser(String script) {
        Parser parser = new Parser(script, new BaseRule());
        parser.registerMacro(data -> {
            data.registerValue("fail", new com.dfsek.substrate.lang.compiler.value.Function() {
                @Override
                public Signature arguments() {
                    return Signature.empty();
                }

                @Override
                public void invoke(MethodVisitor visitor, BuildData data, Signature args, List<ExpressionNode> argExpressions) {
                    visitor.visitMethodInsn(INVOKESTATIC,
                            CompilerUtil.internalName(Assertions.class),
                            "fail",
                            "()V",
                            false);
                }

                @Override
                public Signature reference() {
                    return Signature.fun();
                }
            });
            data.registerValue("assert", new com.dfsek.substrate.lang.compiler.value.Function() {
                @Override
                public Signature arguments() {
                    return Signature.bool();
                }

                @Override
                public void invoke(MethodVisitor visitor, BuildData data, Signature args, List<ExpressionNode> argExpressions) {
                    visitor.visitMethodInsn(INVOKESTATIC,
                            CompilerUtil.internalName(Assertions.class),
                            "assertTrue",
                            "(Z)V",
                            false);
                }

                @Override
                public Signature reference() {
                    return Signature.fun().applyGenericArgument(0, Signature.bool());
                }
            });
        });
        return parser;
    }

    @TestFactory
    public Collection<DynamicTest> tests() throws IOException {
        register(Paths.get("src", "test", "resources", "parser", "valid"), path -> () -> {
            try {
                String data = IOUtils.toString(new FileInputStream(path.toFile()), StandardCharsets.UTF_8);
                Parser parser = createParser(data);
                parser.parse().execute(null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        register(Paths.get("src", "test", "resources", "parser", "invalid"), path -> () -> {
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
        });

        register(Paths.get("src", "test", "resources", "tokenizer", "invalid"), path -> () -> {
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
        });

        register(Paths.get("src", "test", "resources", "tokenizer", "valid"), path -> () -> {
            try {
                String data = IOUtils.toString(new FileInputStream(path.toFile()), StandardCharsets.UTF_8);
                Tokenizer tokenizer = new Tokenizer(data);

                while (tokenizer.hasNext()) {
                    tokenizer.consume();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        tests.sort((a, b) -> a.getDisplayName().compareToIgnoreCase(b.getDisplayName()));
        return tests;
    }

    public void register(Path parent, Function<Path, Executable> executable) throws IOException {
        Files.walk(parent)
                .filter(path -> !path.toFile().isDirectory())
                .forEach(path -> {
                    String name = parent.relativize(path).toString();
                    tests.add(DynamicTest.dynamicTest(name, executable.apply(path)));
                });

    }
}
