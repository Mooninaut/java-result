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

public class SplitStream<VAL> {
    private final Stream<VAL> valueStream;
    private final Stream<Throwable> exceptionStream;

    SplitStream(Stream.Builder<VAL> valueBuilder, Stream.Builder<Throwable> exceptionBuilder) {
        valueStream = valueBuilder.build();
        exceptionStream = exceptionBuilder.build();
    }

    SplitStream(Stream<VAL> valueStream, Stream<Throwable> errorStream) {
        this.valueStream = valueStream;
        this.exceptionStream = errorStream;
    }

    public Stream<VAL> getValueStream() {
        return valueStream;
    }

    public Stream<Throwable> getExceptionStream() {
        return exceptionStream;
    }

    public static <VAL, ERR extends Throwable> Builder<VAL> builder() {
        return new Builder<>();
    }

    public static class Builder<VAL> {
        private final Stream.Builder<VAL> valueBuilder;
        private final Stream.Builder<Throwable> exceptionBuilder;

        public Builder() {
            valueBuilder = Stream.builder();
            exceptionBuilder = Stream.builder();
        }

        public Builder(Stream.Builder<VAL> valueBuilder, Stream.Builder<Throwable> exceptionBuilder) {
            this.valueBuilder = valueBuilder;
            this.exceptionBuilder = exceptionBuilder;
        }

        public SplitStream<VAL> build() {
            return new SplitStream<>(valueBuilder, exceptionBuilder);
        }

        public SplitStream.Builder<VAL> append(SplitStream.Builder<VAL> other) {
            SplitStream<VAL> otherStream = other.build();
            otherStream.getValueStream().forEach(valueBuilder);
            otherStream.getExceptionStream().forEach(exceptionBuilder);
            return this;
        }

        public Builder<VAL> addValue(VAL value) {
            valueBuilder.add(value);
            return this;
        }

        public Builder<VAL> addException(Throwable error) {
            exceptionBuilder.add(error);
            return this;
        }

        public Builder<VAL> add(Result<VAL> result) {
            if (result.isAccepted()) {
                valueBuilder.add(result.get());
            } else {
                exceptionBuilder.add(result.getException());
            }
            return this;
        }
    }
}
