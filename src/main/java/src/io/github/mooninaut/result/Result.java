package src.io.github.mooninaut.result;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/*
 * Result.java
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

/**
 * A Result type that contains, if accepted, a result of type {@code <VAL>} or {@code null},
 * or if rejected, a Throwable of type {@code <ERR>}.
 * @param <VAL> The type of the included value, if present.
 * @param <ERR> The type of the included Throwable, if present.
 */
public interface Result<VAL, ERR extends Throwable> {

    /**
     * Returns an empty Result. May or may not be a singleton.
     */
    static <VAL, ERR extends Throwable> Result<VAL, ERR> empty() {
        return EmptyResult.getInstance();
    }

    /**
     * Creates and returns an accepted Result containing a value, {@code val}, of type {@code <VAL>}.
     */
    static <VAL, ERR extends Throwable> Result<VAL, ERR> accept(VAL val) {
        return val == null ? EmptyResult.getInstance() : new AcceptedResult<>(val);
    }

    /**
     * Creates and returns a rejected Result containing an exception, {@code err}, of type {@code <ERR>}.
     */
    static <VAL, ERR extends Throwable> Result<VAL, ERR> reject(ERR err) {
        return new RejectedResult<>(Objects.requireNonNull(err));
    }

    /**
     * Creates and returns a Result based on the type of the supplied Object.
     * If it extends {@code Throwable}, a rejected Result.
     * Otherwise, an accepted Result.
     */
    @SuppressWarnings("unchecked")
    static <VAL, ERR extends Throwable> Result<VAL, ERR> of(Object o) {
        if (o == null) {
            return EmptyResult.getInstance();
        }
        if (o instanceof Throwable) {
            return new RejectedResult<>((ERR) o);
        }
        return new AcceptedResult<>((VAL) o);
    }

    /**
     * Attempts to cast the value {@code in} to the specified class, capturing the value or the {@link ClassCastException} in a Result.
     * @param in A value of type {@code <IN>}.
     * @param outClass The class to cast {@code in} to.
     * @param <IN> The type of {@code in}
     * @param <OUT> The type to cast {@code in} to.
     * @return A result containing either {@code in} cast to type {@code <OUT>}, or a {@link ClassCastException}.
     */
    static <IN, OUT> Result<OUT, ClassCastException> safeCast(IN in, Class<OUT> outClass) {
        try {
            return new AcceptedResult<>(outClass.cast(in));
        } catch (ClassCastException cce) {
            return new RejectedResult<>(cce);
        }
    }

    /**
     * Create a Result with the supplied value if non-null, or a {@link NullPointerException}.
     * @param val A value of type {@code <VAL>}.
     * @param <VAL> The type of {@code val}.
     * @return A Result containing either a non-null {@code val} or a {@link NullPointerException}.
     */
    static <VAL> Result<VAL, NullPointerException> requireNonNull(VAL val) {
        if (val == null) {
            return new RejectedResult<>(new NullPointerException());
        }
        return new AcceptedResult<>(val);
    }

    /**
     * Helper to generate lambdas to pass to stream.map() and similar functions
     * Example use: {@code List<Result<File, IOException>> files = fileNameResultList.stream(Result.mapper(File::open)).collect(Collectors.toList());}
     * @param exFunc A function from {@code <VAL>} to {@code <OUT>}
     * @param <VAL> The value type of an existing {@code Result}.
     * @param <VAL2> The value type of the new {@code Result} returned by the lambda.
     * @param <ERR> The type of the exception possibly thrown by the mapping function.
     * @return A wrapped function that takes {@code Result<VAL, Throwable>} and returns {@code Result<OUT, Throwable>}.
     */
    static <VAL, VAL2, ERR extends Throwable, EF extends ExceptionalFunction<? super VAL, ? extends VAL2, ERR>>
    Function<Result<VAL, ? extends Throwable>, Result<VAL2, Throwable>> mapper(EF exFunc) {
        return result -> result.map(exFunc);
    }

    /**
     * Helper to generate lambdas to pass to stream.map() and similar functions
     * Example use: {@code List<Result<File, IOException>> files = fileNameResultList.stream(Result.mapper(File::open)).collect(Collectors.toList());}
     * @param func A function from {@code <VAL>} to {@code <OUT>}
     * @param <VAL> The value type of an existing {@code Result}.
     * @param <VAL2> The value type of the new {@code Result} returned by the lambda.
     * @param <ERR> The type of the exception possibly thrown by the mapping function.
     * @return A wrapped function that takes {@code Result<VAL, Throwable>} and returns {@code Result<OUT, Throwable>}.
     */
    static <VAL, VAL2, ERR extends Throwable, F extends Function<? super VAL, ? extends VAL2>>
    Function<Result<VAL, ERR>, Result<VAL2, ERR>> mapper(F func) {
        return result -> result.map(func);
    }

    /**
     * Map and filter a Stream of Results to a Stream of just values.
     */
    static <VAL> Stream<VAL> valueStream(Stream<Result<VAL, ? extends Throwable>> stream) {
        return stream.filter(Result::isAccepted).map(Result::get);
    }

    /**
     * Map and filter a Collection of Results to Sstream of just values.
     */
    static <VAL> Stream<VAL> valueStream(Collection<Result<VAL, ? extends Throwable>> collection) {
        return valueStream(collection.stream());
    }

    /**
     * Map and filter a Stream of Results to a Stream of just Throwables.
     */
    static <VAL> Stream<? extends Throwable> exceptionStream(Stream<Result<VAL, ? extends Throwable>> stream) {
        return stream.filter(Result::isRejected).map(Result::getException);
    }

    /**
     * Map and filter a Collection of Results to a Stream of just Throwables.
     */
    static <VAL> Stream<? extends Throwable> exceptionStream(Collection<Result<VAL, ? extends Throwable>> collection) {
        return exceptionStream(collection.stream());
    }

    /**
     * Map a Stream of Results to a Stream of Optionals.
     */
    static <VAL> Stream<Optional<VAL>> optionalStream(Stream<Result<VAL, ? extends Throwable>> stream) {
        return stream.map(Result::toNullableOptional);
    }

    /**
     * Map a Collection of Results to a Stream of Optionals.
     */
    static <VAL> Stream<Optional<VAL>> optionalStream(Collection<Result<VAL, ? extends Throwable>> collection) {
        return optionalStream(collection.stream());
    }

    /**
     * Transforms a Stream of Results to a single SplitStream containing a stream of values and a Stream of Throwables.
     */
    static <VAL, ERR extends Throwable> SplitStream<VAL, ERR> splitStream(Stream<Result<VAL, ERR>> stream) {
        return stream.collect(new SplitCollector<>());
    }

    /**
     * Transforms a Collection of Results to a single SplitStream containing a stream of values and a Stream of Throwables.
     */
    static <VAL, ERR extends Throwable> SplitStream<VAL, ERR> splitStream(Collection<Result<VAL, ERR>> collection) {
        return splitStream(collection.stream());
    }

    ////// Public methods ///////

    /**
     * Is this result Accepted?
     * @return true for an accepted (possibly null) value, false if rejected.
     */
    boolean isAccepted();

    /**
     * Is this Result accepted and non-null?
     * @return true for an accepted non-null value, false if null or rejected.
     */
    boolean isPresent();

    /**
     * Is this Result accepted and null?
     * @return true if this Result is accepted and null, false if non-null or rejected.
     */
    boolean isEmpty();

    /**
     * Is this result rejected?
     * @return true if this Result is rejected, false if accepted.
     */
    boolean isRejected();

    /**
     * Get the class of the contained value or Empty if this Result contains null or is rejected.
     * @return the class of the contained value or Empty if this Result contains null or is rejected.
     */
    Optional<Class<?>> getValueType();

    /**
     * Perform a checked cast to {@code Result<OUT, ERR>} if this Result is accepted.
     * If this Result is rejected, acts as a no-op.
     * @param type The class object of the class to checkedCast to.
     * @param <OUT> The class to checkedCast to.
     * @return {@code this} if {@code <VAL>} can be checkedCast to {@code OUT}.
     */
    <OUT> Result<OUT, ERR> checkedCast(Class<OUT> type) throws ClassCastException;

    /**
     * Performs an unchecked cast to {@code RESULT<OUT, ERR>}.
     */
    <OUT> Result<OUT, ERR> uncheckedCast();

    /**
     * Get this Result's value if this Result is accepted, or throws IllegalStateException.
     */
    VAL get() throws IllegalStateException;

    /**
     * Get this Result's Throwable if this result is Rejected, or throws IllegalStateException.
     */
    ERR getException() throws IllegalStateException;

    /**
     * Get this Result's value if this Result is accepted, or {@code other} if it is rejected.
     */
    VAL orElse(VAL other);


    /**
     * Get this Result's value if this Result is accepted, or throws the included Throwable if it is rejected.
     */
    VAL orElseThrow() throws ERR;

    /**
     * Get this Result's value if this Result is accepted, or throws the included Throwable
     * wrapped in a {@link RuntimeException} if it is rejected.
     */
    VAL orElseThrowRuntime() throws RuntimeException;

    /**
     * Throws the included Throwable if it is rejected, otherwise does nothing.
     */
    void throwIfRejected() throws ERR;

    /**
     * Throws the included Throwable wrapped in a {@link RuntimeException} if it is rejected, otherwise does nothing.
     */
    void throwRuntimeIfRejected() throws RuntimeException;

    /**
     * Calls {@code mapper} on VAL and returns either {@code Result<OUT,ERR>} or {@code Result<OUT,OUTERR>},
     * where {@code <OUTERR>} is the exception type thrown by {@code mapper}.
     * Since it cannot be determined at compile time which exception type will be the result, casts both exceptions to Throwable.
     * A run-time type check will be required if the type of the exception is significant.
     * @param mapper A function mapping from type {@code <VAL>} to type {@code <OUT>}, possibly throwing an exception of type {@code <OUTERR>}
     * @param <OUT> The return type of {@code mapper}.
     * @param <OUTERR> The exception type thrown by {@code mapper}.
     * @return If this Result accepted, the result of executing {@code mapper} on this Result's value, otherwise {@code this}
     */
    <OUT, OUTERR extends Throwable, EF extends ExceptionalFunction<? super VAL, ? extends OUT, OUTERR>>
    Result<OUT, Throwable> map(EF mapper);

    /**
     * Calls {@code mapper} on VAL and returns {@code Result<OUT,ERR>}
     * @param mapper A function mapping from type {@code <VAL>} to type {@code <OUT>}
     * @param <OUT> The return type of {@code mapper}
     * @return If this Result accepted, the Result of executing {@code mapper} on this Result's value, otherwise {@code this}
     */
    <OUT, F extends Function<? super VAL, ? extends OUT>>
    Result<OUT, ERR> map(F mapper);

    /**
     * Converts the Result to an {@link Optional}.
     * @return the accepted value in an Optional, or, if rejected, an empty Optional.
     * @throws NullPointerException if result is accepted, but value is null
     */
    Optional<VAL> toOptional() throws NullPointerException;

    /**
     * Converts the Result to an {@link Optional}.
     * Does not distinguish between a rejected result and an accepted, but null result. In either case, returns an empty Optional.
     * @return the accepted value in an Optional, or, if rejected, an empty Optional.
     */
    Optional<VAL> toNullableOptional();

    /**
     * If this Result is accepted, feed the included value to the supplied {@link Consumer}, otherwise, do nothing.
     * Chainable.
     */
    Result<VAL, ERR> accept(Consumer<? super VAL> consumer);

    /**
     * If this Result is rejected, feed the included {@link Throwable} to the supplied {@link Consumer}, otherwise, do nothing.
     * Chainable.
     */
    Result<VAL, ERR> reject(Consumer<? super ERR> rejector);

    /**
     * Feed this Result's value or {@link Throwable} to the appropriate {@link Consumer}.
     * Chainable.
     */
    Result<VAL, ERR> then(Consumer<? super VAL> consumer, Consumer<? super ERR> rejector);

    /**
     * Feed this Result's value to the supplied {@link Consumer} if it is present, otherwise feed {@code other} to it.
     * Chainable.
     */
    Result<VAL, ERR> acceptOrElse(Consumer<? super VAL> consumer, VAL other);

    /**
     * Feed this Result's value to the supplied {@link Consumer} if it is present, otherwise throw this Result's {@link Throwable}.
     * Chainable.
     */
    Result<VAL, ERR> acceptOrElseThrow(Consumer<? super VAL> consumer) throws ERR;

    /**
     * Feed this Result's value to the supplied {@link Consumer} if it is present, otherwise
     * throw this Result's {@link Throwable} wrapped in a {@link RuntimeException}.
     * Chainable.
     */
    Result<VAL, ERR> acceptOrElseThrowRuntime(Consumer<? super VAL> consumer) throws RuntimeException;

    /**
     * Feed this Result's value to the supplied {@link Consumer} if it is present, otherwise
     * print the stack trace associated with this Result's {@link Throwable} to standard error.
     * Chainable.
     */
    Result<VAL, ERR> acceptOrPrintStacktrace(Consumer<? super VAL> consumer);
}
