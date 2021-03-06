package io.github.mooninaut.result;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/*
 * Results.java
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

public interface Results {

    /**
     * Helper to generate lambdas to pass to stream.map() and similar functions
     * Example use:
     * {@code List<Result<List<String>, IOException>> fileContents = pathList.stream(
     *     Result.exMapper(path -> Files.readAllLines(path, Charset.defaultCharset()))
     * ).collect(Collectors.toList());}
     * @param exFunc A function from {@code <IN>} to {@code <OUT>}
     * @param <IN> The value type of an existing {@code Result}.
     * @param <OUT> The value type of the new {@code Result} returned by the lambda.
     * @return A wrapped function that takes {@code Result<IN, Throwable>} and returns {@code Result<OUT, Throwable>}.
     */
    static <IN, OUT>
    Function<Result<IN>, Result<OUT>> exMapper(ExceptionalFunction<? super IN, ? extends OUT> exFunc) {
        return result -> result.exMap(exFunc);
    }

    static <IN, OUT>
    Function<Result<IN>, Result<OUT>> exMapperChecked(
            ExceptionalFunction<? super IN, ? extends OUT> exFunc,
            Class<IN> inClass, Class<OUT> outClass) {
        return result -> result.exMapChecked(exFunc, inClass, outClass);
    }

    /**
     * Helper to generate lambdas to pass to stream.map() and similar functions
     * Example use: {@code List<Result<String, IOException>> fileContentStrings = fileContents.stream(
     *     Result.mapper(listOfStrings -> String.join("\n", listOfStrings))
     * ).collect(Collectors.toList());}
     * @param func A function from {@code <IN>} to {@code <OUT>}
     * @param <VAL> The value type of an existing {@code Result}.
     * @param <VAL2> The value type of the new {@code Result} returned by the lambda.
     * @return A wrapped function that takes {@code Result<IN, Throwable>} and returns {@code Result<OUT, Throwable>}.
     */
    static <VAL, VAL2>
    Function<Result<VAL>, Result<VAL2>> mapper(Function<? super VAL, ? extends VAL2> func) {
        return result -> result.map(func);
    }

    /**
     * Map and filter a Stream of Results to a Stream of just values.
     */
    static <VAL> Stream<VAL> valueStream(Stream<Result<VAL>> stream) {
        return stream.filter(Result::isAccepted).map(Result::get);
    }

    /**
     * Map and filter a Collection of Results to a Stream of just values.
     */
    static <VAL> Stream<VAL> valueStream(Collection<Result<VAL>> collection) {
        return valueStream(collection.stream());
    }

    /**
     * Map and filter a Stream of Results to a Stream of just Throwables.
     */
    static <VAL> Stream<? extends Throwable> exceptionStream(Stream<Result<VAL>> stream) {
        return stream.filter(Result::isRejected).map(Result::getException);
    }

    /**
     * Map and filter a Collection of Results to a Stream of just Throwables.
     */
    static <VAL> Stream<? extends Throwable> exceptionStream(Collection<Result<VAL>> collection) {
        return exceptionStream(collection.stream());
    }

    /**
     * Map a Stream of Results to a Stream of Optionals.
     */
    static <VAL> Stream<Optional<VAL>> optionalStream(Stream<Result<VAL>> stream) {
        return stream.map(Result::toNullableOptional);
    }

    /**
     * Map a Collection of Results to a Stream of Optionals.
     */
    static <VAL> Stream<Optional<VAL>> optionalStream(Collection<Result<VAL>> collection) {
        return optionalStream(collection.stream());
    }

    /**
     * Transforms a Stream of Results to a single SplitStream containing a stream of values and a Stream of Throwables.
     */
    static <VAL> SplitStream<VAL> splitStream(Stream<Result<VAL>> stream) {
        return stream.collect(SplitCollectorImpl.collector());
    }

    /**
     * Transforms a Collection of Results to a single SplitStream containing a stream of values and a Stream of Throwables.
     */
    static <VAL> SplitStream<VAL> splitStream(Collection<Result<VAL>> collection) {
        return splitStream(collection.stream());
    }
}
