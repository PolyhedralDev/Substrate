package com.dfsek.substrate.parser.check;

import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.lexer.token.Token;
import com.dfsek.substrate.lexer.token.TokenType;
import com.dfsek.substrate.parser.ParserUtil;
import io.vavr.Function2;
import io.vavr.Tuple2;
import io.vavr.control.Either;
import io.vavr.control.Option;

import java.util.function.Function;
import java.util.function.Supplier;

public class ParameterizedCheck<T> extends TokenCheck{
    private final Option<T> parameter;

    protected ParameterizedCheck(Token checked, Option<Tuple2<String, Position>> fail, Option<T> parameter) {
        super(checked, fail);
        this.parameter = parameter;
    }

    @Override
    public ParameterizedCheck<T> and(Token value, TokenType... types) {
        return new ParameterizedCheck<>(value, ParserUtil.checkTypeFunctional(value, types).swap().toOption(), parameter);
    }

    public <U> ParameterizedCheck<U> map(Function<T, U> map) {
        return new ParameterizedCheck<>(checked, fail, parameter.map(map));
    }

    public <U> ParameterizedCheck<U> map(Function2<Token, T, U> map) {
        return new ParameterizedCheck<>(checked, fail, parameter.map(map.apply(checked)));
    }

    public Either<Tuple2<String, Position>, T> get() {
        return fail.toEither(parameter::get).swap();
    }
}
