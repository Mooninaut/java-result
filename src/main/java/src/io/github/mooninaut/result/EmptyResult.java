package src.io.github.mooninaut.result;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static src.io.github.mooninaut.result.ExceptionalFunctionWrapper.wrapFunction;

/*
 * EmptyResult.java
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

final class EmptyResult<VAL, ERR extends Throwable> implements Result<VAL, ERR> {

    private static final int HASH_CODE = Objects.hash(EmptyResult.class, null);

    private static final Result<Void, ? extends Throwable> INSTANCE = new EmptyResult<>();

    private EmptyResult() { }

    @SuppressWarnings("unchecked")
    public static <VAL, ERR extends Throwable> Result<VAL, ERR> getInstance() {
        return (Result<VAL, ERR>) INSTANCE;
    }

    @Override
    public boolean isAccepted() {
        return false;
    }

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean isRejected() {
        return false;
    }

    @Override
    public Optional<Class<?>> getValueType() {
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <OUT> Result<OUT, ERR> checkedCast(Class<OUT> type) {
        return (Result<OUT, ERR>) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <OUT> Result<OUT, ERR> uncheckedCast() {
        return (Result<OUT, ERR>) this;
    }

    @Override
    public VAL get() {
        return null;
    }

    @Override
    public ERR getException() {
        throw new IllegalStateException("Cannot get exception from empty Result");
    }

    @Override
    public VAL orElse(VAL other) {
        return other;
    }

    @Override
    public VAL orElseThrow() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <OUT, OUTERR extends Throwable, EF extends ExceptionalFunction<? super VAL, ? extends OUT, OUTERR>> Result<OUT, Throwable> map(
            EF mapper) {
        return (Result<OUT, Throwable>) wrapFunction(mapper).apply(null);
    }

    @Override
    public <OUT, F extends Function<? super VAL, ? extends OUT>> Result<OUT, ERR> map(F mapper) {
        return new AcceptedResult<>(mapper.apply(null));
    }

    @Override
    public Optional<VAL> toOptional() throws NullPointerException {
        throw new NullPointerException();
    }

    @Override
    public Optional<VAL> toNullableOptional() {
        return Optional.empty();
    }

    @Override
    public VAL orElseThrowRuntime() {
        return null;
    }

    @Override
    public void throwIfRejected() { }

    @Override
    public void throwRuntimeIfRejected() throws RuntimeException { }

    @Override
    public Result<VAL, ERR> accept(Consumer<? super VAL> consumer) {
        consumer.accept(null);
        return this;
    }

    @Override
    public Result<VAL, ERR> reject(Consumer<? super ERR> rejector) {
        return this;
    }

    @Override
    public Result<VAL, ERR> then(Consumer<? super VAL> consumer, Consumer<? super ERR> rejector) {
        consumer.accept(null);
        return this;
    }

    @Override
    public Result<VAL, ERR> acceptOrElse(Consumer<? super VAL> consumer, VAL other) {
        consumer.accept(null);
        return this;
    }

    @Override
    public Result<VAL, ERR> acceptOrElseThrow(Consumer<? super VAL> consumer) {
        consumer.accept(null);
        return this;
    }

    @Override
    public Result<VAL, ERR> acceptOrElseThrowRuntime(Consumer<? super VAL> consumer) {
        consumer.accept(null);
        return this;
    }

    @Override
    public Result<VAL, ERR> acceptOrPrintStacktrace(Consumer<? super VAL> consumer) {
        consumer.accept(null);
        return this;
    }

    // Object method overrides

    @Override
    public int hashCode() {
        return HASH_CODE;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
