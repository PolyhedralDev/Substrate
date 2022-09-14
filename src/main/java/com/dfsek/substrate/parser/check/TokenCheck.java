package com.dfsek.substrate.parser.check;

import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.lexer.token.Token;
import com.dfsek.substrate.lexer.token.TokenType;
import com.dfsek.substrate.parser.ParserUtil;
import io.vavr.Tuple2;
import io.vavr.control.Either;
import io.vavr.control.Option;

import java.util.function.Function;
import java.util.function.Supplier;

public class TokenCheck {
    protected final Token checked;

    protected final Option<Tuple2<String, Position>> fail;

    public TokenCheck(Token checked, Option<Tuple2<String, Position>> fail) {
        this.checked = checked;
        this.fail = fail;
    }

    public TokenCheck and(Token value, TokenType... types) {
        return new TokenCheck(value, ParserUtil.checkTypeFunctional(value, types).swap().toOption());
    }

    public <T> Either<Tuple2<String, Position>, T> get(Supplier<T> get) {
        return get(ignore -> get.get());
    }

    public <T> Either<Tuple2<String, Position>, T> get(T get) {
        return get(() -> get);
    }

    public <T> Either<Tuple2<String, Position>, T> get(Function<Token, T> get) {
        return fail.toEither(() -> get.apply(checked)).swap();
    }

    public <T> ParameterizedCheck<T> getP(T get) {
        return getP(() -> get);
    }

    public <T> ParameterizedCheck<T> getP(Supplier<T> get) {
        return getP(ignore -> get.get());
    }

    public <T> ParameterizedCheck<T> getP(Function<Token, T> get) {
        return new ParameterizedCheck<>(checked, fail, get(get).toOption());
    }
}
