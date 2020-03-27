package io.github.mooninaut.result;

import java.util.stream.Stream;

/*
 * SplitStream.java
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

public class SplitStream<VAL, ERR extends Throwable> {
    private final Stream<VAL> valueStream;
    private final Stream<ERR> exceptionStream;

    SplitStream(Stream.Builder<VAL> valueBuilder, Stream.Builder<ERR> exceptionBuilder) {
        valueStream = valueBuilder.build();
        exceptionStream = exceptionBuilder.build();
    }

    SplitStream(Stream<VAL> valueStream, Stream<ERR> errorStream) {
        this.valueStream = valueStream;
        this.exceptionStream = errorStream;
    }

    public Stream<VAL> getValueStream() {
        return valueStream;
    }

    public Stream<ERR> getExceptionStream() {
        return exceptionStream;
    }

    public static <VAL, ERR extends Throwable> Builder<VAL, ERR> builder() {
        return new Builder<>();
    }

    public static class Builder<VAL, ERR extends Throwable> {
        private final Stream.Builder<VAL> valueBuilder;
        private final Stream.Builder<ERR> exceptionBuilder;

        public Builder() {
            valueBuilder = Stream.builder();
            exceptionBuilder = Stream.builder();
        }

        public Builder(Stream.Builder<VAL> valueBuilder, Stream.Builder<ERR> exceptionBuilder) {
            this.valueBuilder = valueBuilder;
            this.exceptionBuilder = exceptionBuilder;
        }

        public SplitStream<VAL, ERR> build() {
            return new SplitStream<>(valueBuilder, exceptionBuilder);
        }

        public void addValue(VAL value) {
            valueBuilder.add(value);
        }

        public void addException(ERR error) {
            exceptionBuilder.add(error);
        }

        public void add(Result<VAL, ERR> result) {
            if (result.isAccepted()) {
                valueBuilder.add(result.get());
            } else {
                exceptionBuilder.add(result.getException());
            }
        }
    }
}
