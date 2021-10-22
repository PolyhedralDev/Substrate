import com.dfsek.substrate.lang.rules.BaseRule;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Tokenizer;
import com.dfsek.substrate.tokenizer.exceptions.TokenizerException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;

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

    @TestFactory
    public Collection<DynamicTest> tests() throws IOException {
        register(Paths.get("src", "test", "resources", "parser", "valid"), path -> () -> {
            try {
                String data = IOUtils.toString(new FileInputStream(path.toFile()), StandardCharsets.UTF_8);
                Parser parser = new Parser(data, new BaseRule());
                parser.parse().execute(null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        register(Paths.get("src", "test", "resources", "parser", "invalid"), path -> () -> {
            try {
                String data = IOUtils.toString(new FileInputStream(path.toFile()), StandardCharsets.UTF_8);
                Parser parser = new Parser(data, new BaseRule());
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

        return tests;
    }

    public void register(Path parent, Function<Path, Executable> executable) throws IOException {
        Files.walk(parent)
                .filter(path -> !path.toFile().isDirectory())
                .forEach(path -> {
                    String name = path.getFileName().toString();
                    tests.add(DynamicTest.dynamicTest(name, executable.apply(path)));
                });

    }
}
