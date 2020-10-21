package io.github.mooninaut.result;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/*
 * RejectedResult.java
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

public final class RejectedResult<VAL> extends Result<VAL> {
    ////// Fields //////
    private final Throwable throwable;

    ////// Constructor ///////

    RejectedResult(Throwable throwable) {
        this.throwable = throwable;
    }

    ////// Public methods ///////
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
        return false;
    }

    @Override
    public boolean isRejected() {
        return true;
    }

    @Override
    public Optional<Class<?>> getValueType() {
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <OUT> Result<OUT> checkedCast(Class<OUT> type) {
        return (Result<OUT>) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <OUT> Result<OUT> uncheckedCast() {
        return (Result<OUT>) this;
    }

    @Override
    public VAL get() {
        throw new IllegalStateException("Cannot get value from rejected Result");
    }

    @Override
    public Throwable getException() {
        return throwable;
    }

    @Override
    public VAL orElse(VAL other) {
        return other;
    }

    @Override
    public VAL orElseThrow() throws Throwable {
        throw throwable;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <OUT, EF extends ExceptionalFunction<? super VAL, ? extends OUT>>
    Result<OUT> exMap(EF mapper) {
        return (Result<OUT>) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <OUT, EF extends ExceptionalFunction<? super VAL, ? extends OUT>>
    Result<OUT> exMapChecked(EF mapper, Class<VAL> inClass, Class<OUT> outClass) {
        return (Result<OUT>) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <OUT, F extends Function<? super VAL, ? extends OUT>>
    Result<OUT> map(F mapper) {
        return (Result<OUT>) this;
    }

    @Override
    public Optional<VAL> toOptional() {
        return Optional.empty();
    }

    @Override
    public Optional<VAL> toNullableOptional() {
        return Optional.empty();
    }

    @Override
    public VAL orElseThrowRuntime() {
        throw new RuntimeException(throwable);
    }

    @Override
    public void throwIfRejected() throws Throwable {
        throw throwable;
    }

    @Override
    public void throwRuntimeIfRejected() throws RuntimeException {
        throw new RuntimeException(throwable);
    }

    @Override
    public Result<VAL> ifAccepted(Consumer<? super VAL> consumer) {
        return this;
    }

    @Override
    public Result<VAL> ifRejected(Consumer<? super Throwable> rejector) {
        rejector.accept(throwable);
        return this;
    }

    @Override
    public Result<VAL> then(Consumer<? super VAL> consumer, Consumer<? super Throwable> rejector) {
        rejector.accept(throwable);
        return this;
    }
    @Override
    public Result<VAL> acceptOrElse(Consumer<? super VAL> consumer, VAL other) {
        consumer.accept(other);
        return this;
    }
    @Override
    public Result<VAL> acceptOrElseThrow(Consumer<? super VAL> consumer) throws Throwable {
        throw throwable;
    }
    @Override
    public Result<VAL> acceptOrElseThrowRuntime(Consumer<? super VAL> consumer) {
        throw new RuntimeException(throwable);
    }
    @Override
    public Result<VAL> acceptOrPrintStacktrace(Consumer<? super VAL> consumer) {
        throwable.printStackTrace();
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
        RejectedResult<?> otherResult = (RejectedResult<?>) o;
        return throwable == otherResult.throwable;
    }

    @Override
    public int hashCode() {
        return Objects.hash(throwable);
    }

    @Override
    public String toString() {
        return "Result: rejected, " + throwable;
    }
}
