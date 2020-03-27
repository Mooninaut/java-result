package src.io.github.mooninaut.result;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static src.io.github.mooninaut.result.ExceptionalFunctionWrapper.wrapFunction;

/*
 * AcceptedResult.java
 * Copyright 2020 Clement Cherlin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

final class AcceptedResult<VAL, ERR extends Throwable> implements Result<VAL, ERR> {
    ////// Fields //////
    private final VAL value;

    ////// Constructors ///////
    AcceptedResult(VAL value) {
        this.value = value;
    }

    ////// Public methods ///////
    @Override
    public boolean isAccepted() {
        return true;
    }

    @Override
    public boolean isPresent() {
        return value != null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isRejected() {
        return false;
    }

    @Override
    public Optional<Class<?>> getValueType() {
        return Optional.of(value.getClass());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <OUT> Result<OUT, ERR> checkedCast(Class<OUT> type) throws ClassCastException {
        type.cast(get());
        return (Result<OUT, ERR>) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <OUT> Result<OUT, ERR> uncheckedCast() {
        return (Result<OUT, ERR>) this;
    }

    @Override
    public VAL get() {
        return value;
    }

    @Override
    public ERR getException() {
        throw new IllegalStateException("Cannot get exception from accepted Result");
    }

    @Override
    public VAL orElse(VAL other) {
        return value;
    }

    @Override
    public VAL orElseThrow() {
        return value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <OUT, OUTERR extends Throwable, EF extends ExceptionalFunction<? super VAL, ? extends OUT, OUTERR>>
    Result<OUT, Throwable> map(EF mapper) {
        return (Result<OUT, Throwable>) wrapFunction(mapper).apply(get());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <OUT, F extends Function<? super VAL, ? extends OUT>>
    Result<OUT, ERR> map(F mapper) {
        return Result.accept(mapper.apply(get()));
    }

    @Override
    public Optional<VAL> toOptional() throws NullPointerException {
        return Optional.of(get());

    }

    @Override
    public Optional<VAL> toNullableOptional() {
        return Optional.of(get());
    }

    @Override
    public VAL orElseThrowRuntime() {
        return value;
    }

    @Override
    public void throwIfRejected() { }

    @Override
    public void throwRuntimeIfRejected() { }

    @Override
    public Result<VAL, ERR> accept(Consumer<? super VAL> consumer) {
        if (isAccepted()) {
            consumer.accept(get());
        }
        return this;
    }

    @Override
    public Result<VAL, ERR> reject(Consumer<? super ERR> rejector) {
        if (isRejected()) {
            rejector.accept(getException());
        }
        return this;
    }

    @Override
    public Result<VAL, ERR> then(Consumer<? super VAL> consumer, Consumer<? super ERR> rejector) {
        if (isAccepted()) {
            consumer.accept(get());
        } else {
            rejector.accept(getException());
        }
        return this;
    }
    @Override
    public Result<VAL, ERR> acceptOrElse(Consumer<? super VAL> consumer, VAL other) {
        if (isAccepted()) {
            consumer.accept(get());
        } else {
            consumer.accept(other);
        }
        return this;
    }
    @Override
    public Result<VAL, ERR> acceptOrElseThrow(Consumer<? super VAL> consumer) throws ERR {
        if (isRejected()) {
            throw getException();
        }
        consumer.accept(get());
        return this;
    }
    @Override
    public Result<VAL, ERR> acceptOrElseThrowRuntime(Consumer<? super VAL> consumer) {
        if (isRejected()) {
            throw new RuntimeException(getException());
        }
        consumer.accept(get());
        return this;
    }
    @Override
    public Result<VAL, ERR> acceptOrPrintStacktrace(Consumer<? super VAL> consumer) {
        if (isAccepted()) {
            consumer.accept(get());
        } else {
            getException().printStackTrace();
        }
        return this;
    }

    ////// Object overrides //////

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AcceptedResult<?, ?> otherResult = (AcceptedResult<?, ?>) o;
        return Objects.equals(value, otherResult.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Result: " + (isAccepted() ? "accepted" : "rejected") + ", " + value;
    }

}
