package io.github.mooninaut.result;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

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

final class EmptyResult<VAL> implements Result<VAL> {

    private enum Self {
        INSTANCE;

        private final EmptyResult<?> value = new EmptyResult<>();

        @SuppressWarnings("unchecked")
        public static <VAL> Result<VAL> getInstance() {
            return (Result<VAL>) INSTANCE.value;
        }
    }
    private static final int HASH_CODE = Objects.hash(EmptyResult.class, null);

    private EmptyResult() { }

    public static <VAL> Result<VAL> getInstance() {
        return Self.getInstance();
    }

    @Override
    public boolean isAccepted() {
        return true;
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
        return null;
    }

    @Override
    public Throwable getException() {
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
    public <OUT, OUTERR extends Throwable, EF extends ExceptionalFunction<? super VAL, ? extends OUT, ? extends OUTERR>>
    Result<OUT> exMap(
            EF mapper) {
        return (Result<OUT>) ExceptionalFunctionWrapper.wrap(mapper).apply(null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <OUT, OUTERR extends Throwable, EF extends ExceptionalFunction<? super VAL, ? extends OUT, ? extends OUTERR>>
    Result<OUT> exMapChecked(EF mapper, Class<VAL> inClass, Class<OUT> outClass, Class<OUTERR> outErrClass) {
        return (Result<OUT>) ExceptionalFunctionWrapper
                .wrapChecked(mapper, inClass, outClass, outErrClass);
    }

    @Override
    public <OUT, F extends Function<? super VAL, ? extends OUT>> Result<OUT> map(F mapper) {
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
    public Result<VAL> ifAccepted(Consumer<? super VAL> consumer) {
        consumer.accept(null);
        return this;
    }

    @Override
    public Result<VAL> ifRejected(Consumer<? super Throwable> rejector) {
        return this;
    }

    @Override
    public Result<VAL> then(Consumer<? super VAL> consumer, Consumer<? super Throwable> rejector) {
        consumer.accept(null);
        return this;
    }

    @Override
    public Result<VAL> acceptOrElse(Consumer<? super VAL> consumer, VAL other) {
        consumer.accept(null);
        return this;
    }

    @Override
    public Result<VAL> acceptOrElseThrow(Consumer<? super VAL> consumer) {
        consumer.accept(null);
        return this;
    }

    @Override
    public Result<VAL> acceptOrElseThrowRuntime(Consumer<? super VAL> consumer) {
        consumer.accept(null);
        return this;
    }

    @Override
    public Result<VAL> acceptOrPrintStacktrace(Consumer<? super VAL> consumer) {
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
