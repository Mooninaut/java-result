package io.github.mooninaut.result;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

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

final record AcceptedResult<VAL>(VAL value) implements Result<VAL> {
    AcceptedResult {
        if (value == null) {
            throw new NullPointerException("AcceptedResult does not allow null values");
        }
    }

    ////// Public methods ///////
    @Override
    public boolean isAccepted() {
        return true;
    }

    @Override
    public boolean isPresent() {
        return true;
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
    public <OUT> Result<OUT> checkedCast(Class<OUT> type) throws ClassCastException {
        type.cast(value);
        return (Result<OUT>) (Result) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <OUT> Result<OUT> uncheckedCast() {
        return (Result<OUT>) (Result) this;
    }

    @Override
    public VAL get() {
        return value;
    }

    @Override
    public Throwable getException() {
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
    public <OUT, EF extends ExceptionalFunction<? super VAL, ? extends OUT>>
    Result<OUT> exMap(EF mapper) {
        return ExceptionalFunctionWrapper.wrap(mapper).apply(value);
    }

    @Override
    public <OUT, EF extends ExceptionalFunction<? super VAL, ? extends OUT>>
    Result<OUT> exMapChecked(EF mapper, Class<VAL> inClass, Class<OUT> outClass) {
        return ExceptionalFunctionWrapper.wrapChecked(
                mapper, inClass, outClass
        ).apply(value);
    }

    @Override
    public <OUT, F extends Function<? super VAL, ? extends OUT>>
    Result<OUT> map(F mapper) {
        return Result.accept(mapper.apply(value));
    }

    @Override
    public Optional<VAL> toOptional() throws NullPointerException {
        return Optional.of(value);
    }

    @Override
    public Optional<VAL> toNullableOptional() {
        return Optional.of(value);
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
    public Result<VAL> ifAccepted(Consumer<? super VAL> consumer) {
        if (isAccepted()) {
            consumer.accept(value);
        }
        return this;
    }

    @Override
    public Result<VAL> ifRejected(Consumer<? super Throwable> rejector) {
        if (isRejected()) {
            rejector.accept(getException());
        }
        return this;
    }

    @Override
    public Result<VAL> then(Consumer<? super VAL> consumer, Consumer<? super Throwable> rejector) {
        if (isAccepted()) {
            consumer.accept(value);
        } else {
            rejector.accept(getException());
        }
        return this;
    }

    @Override
    public Result<VAL> acceptOrElse(Consumer<? super VAL> consumer, VAL other) {
        if (isAccepted()) {
            consumer.accept(value);
        } else {
            consumer.accept(other);
        }
        return this;
    }

    @Override
    public Result<VAL> acceptOrElseThrow(Consumer<? super VAL> consumer) throws Throwable {
        if (isRejected()) {
            throw getException();
        }
        consumer.accept(value);
        return this;
    }

    @Override
    public Result<VAL> acceptOrElseThrowRuntime(Consumer<? super VAL> consumer) {
        if (isRejected()) {
            throw new RuntimeException(getException());
        }
        consumer.accept(value);
        return this;
    }

    @Override
    public Result<VAL> acceptOrPrintStacktrace(Consumer<? super VAL> consumer) {
        if (isAccepted()) {
            consumer.accept(value);
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
        AcceptedResult<?> otherResult = (AcceptedResult<?>) o;
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
