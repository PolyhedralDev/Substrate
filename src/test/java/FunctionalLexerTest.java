import com.dfsek.substrate.lexer.FunctionalLexer;

public class FunctionalLexerTest {
    public static void main(String... args) {
        FunctionalLexer lexer = new FunctionalLexer("""
                fizzbuzz = (i: int) -> {
                    if(i >= 1) {
                        fizzbuzz(i-1);
                        println(
                            if(i % 3 == 0) if(i % 5 == 0) "FizzBuzz" else "Fizz"
                            else if(i % 5 == 0) "Buzz" else str(i)
                        );
                    };
                };
                                
                fizzbuzz(100);
                                
                return true;
                """);

        while (true) {
            System.out.println(lexer.current());
            lexer = lexer.next();
        }
    }
}
