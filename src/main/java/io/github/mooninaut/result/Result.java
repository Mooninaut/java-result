package io.github.mooninaut.result;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

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
 * An immutable Result type that contains, if accepted, a result of type {@code <IN>} or {@code null},
 * or if rejected, a Throwable.
 * @param <VAL> The type of the included value, if present.
 */
public interface Result<VAL> {

    /**
     * Returns an empty Result. May or may not be a singleton.
     */
    static <VAL> Result<VAL> empty() {
        return EmptyResult.getInstance();
    }

    /**
     * Creates and returns an accepted Result containing a value, {@code val}, of type {@code <IN>}.
     */
    static <VAL> Result<VAL> accept(VAL val) {
        return val == null ? EmptyResult.getInstance() : new AcceptedResult<>(val);
    }

    /**
     * Creates and returns a rejected Result containing an exception, {@code err}, of type {@code <ERR>}.
     */
    static <VAL, ERR extends Throwable> Result<VAL> reject(ERR err) {
        return new RejectedResult<>(Objects.requireNonNull(err));
    }

    /**
     * Creates and returns a Result based on the type of the supplied Object.
     * If it extends {@code Throwable}, a rejected Result.
     * Otherwise, an accepted Result.
     */
    @SuppressWarnings("unchecked")
    static <VAL, ERR extends Throwable> Result<VAL> from(Object o) {
        if (o == null) {
            return EmptyResult.getInstance();
        }
        if (o instanceof Throwable) {
            return new RejectedResult<>((ERR) o);
        }
        return new AcceptedResult<>((VAL) o);
    }

    @SuppressWarnings("unchecked")
    static<VAL, ERR extends Throwable> Result<VAL> of(ExceptionalSupplier<VAL, ERR> es) {
        try {
            return accept(es.get());
        } catch (Throwable err) {
            return reject((ERR) err);
        }
    }

    /**
     * Captures the output of exceptionalSupplier in a Result, checking the types of the value or exception as
     * appropriate.
     * @param exceptionalSupplier an ExceptionalSupplier.
     * @param outClass the class to cast the result of calling {@code exceptionalSupplier} to.
     * @param errClass the class to cast the error thrown by calling {@code exceptionalSupplier} to.
     * @param <OUT> the type of {@code outClass}.
     * @param <ERR> the type of {@code errClass}.
     * @return A {@code Result<OUT, ERR>} containing the result of calling {@code exceptionalSupplier}.
     */
    // TODO: If !(err instanceof ERR), should this rethrow err, throw ClassCastException, or return a RejectedResult of ClassCastException?
    // TODO: I'm almost tempted to return a Result<Result<OUT, ERR>, ClassCastException>
    // TODO: Is this even useful? When would you use it? Maybe in tests?
    // TODO: Maybe cast out but not err?
    static <OUT, ERR extends Throwable> Result<OUT> ofChecked(
            ExceptionalSupplier<OUT, ERR> exceptionalSupplier,
            Class<OUT> outClass,
            Class<ERR> errClass) throws ClassCastException {
        OUT out;
        try {
            out = exceptionalSupplier.get();
        } catch (Throwable err) {
            return reject(errClass.cast(err));
        }
        // Casting outside try block to ensure ClassCastException makes it out.
        return accept(outClass.cast(out));
    }

    /**
     * Attempts to cast the value {@code in} to the specified class, capturing the value or the {@link ClassCastException} in a Result.
     * @param in A value of type {@code <IN>}.
     * @param outClass The class to cast {@code in} to.
     * @param <IN> The type of {@code in}
     * @param <OUT> The type to cast {@code in} to.
     * @return A result containing either {@code in} cast to type {@code <OUT>}, or a {@link ClassCastException}.
     */
    static <IN, OUT> Result<OUT> safeCast(IN in, Class<OUT> outClass) {
        try {
            return accept(outClass.cast(in));
        } catch (ClassCastException cce) {
            return new RejectedResult<>(cce);
        }
    }

    /**
     * Create a Result with the supplied value if non-null, or a {@link NullPointerException}.
     * @param val A value of type {@code <IN>}.
     * @param <VAL> The type of {@code val}.
     * @return A Result containing either a non-null {@code val} or a {@link NullPointerException}.
     */
    static <VAL> Result<VAL> requireNonNull(VAL val) {
        if (val == null) {
            return new RejectedResult<>(new NullPointerException());
        }
        return new AcceptedResult<>(val);
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
     * @return {@code this} if {@code <IN>} can be checkedCast to {@code OUT}.
     */
    <OUT> Result<OUT> checkedCast(Class<OUT> type) throws ClassCastException;

    /**
     * Performs an unchecked cast to {@code RESULT<OUT, ERR>}.
     */
    <OUT> Result<OUT> uncheckedCast();

    /**
     * Get this Result's value if this Result is accepted, or throws IllegalStateException.
     */
    VAL get() throws IllegalStateException;

    /**
     * Get this Result's Throwable if this result is Rejected, or throws IllegalStateException.
     */
    Throwable getException() throws IllegalStateException;

    /**
     * Get this Result's value if this Result is accepted, or {@code other} if it is rejected.
     */
    VAL orElse(VAL other);


    /**
     * Get this Result's value if this Result is accepted, or throws the included Throwable if it is rejected.
     */
    VAL orElseThrow() throws Throwable;

    /**
     * Get this Result's value if this Result is accepted, or throws the included Throwable
     * wrapped in a {@link RuntimeException} if it is rejected.
     */
    VAL orElseThrowRuntime() throws RuntimeException;

    /**
     * Throws the included Throwable if it is rejected, otherwise does nothing.
     */
    void throwIfRejected() throws Throwable;

    /**
     * Throws the included Throwable wrapped in a {@link RuntimeException} if it is rejected, otherwise does nothing.
     */
    void throwRuntimeIfRejected() throws RuntimeException;

    /**
     * Calls {@code mapper} on IN and returns either {@code Result<OUT,ERR>} or {@code Result<OUT,OUTERR>},
     * where {@code <OUTERR>} is the exception type thrown by {@code mapper}.
     * Since it cannot be determined at compile time which exception type will be the result, casts both exceptions to Throwable.
     * A run-time type check will be required if the type of the exception is significant.
     * @param <OUT> The return type of {@code mapper}.
     * @param <OUTERR> The exception type thrown by {@code mapper}.
     * @param mapper A function mapping from type {@code <IN>} to type {@code <OUT>}, possibly throwing an exception of type {@code <OUTERR>}
     * @return If this Result accepted, the result of executing {@code mapper} on this Result's value, otherwise {@code this}
     */
    <OUT, OUTERR extends Throwable, EF extends ExceptionalFunction<? super VAL, ? extends OUT, ? extends OUTERR>>
    Result<OUT> exMap(EF mapper);

    <OUT, OUTERR extends Throwable, EF extends ExceptionalFunction<? super VAL, ? extends OUT, ? extends OUTERR>>
    Result<OUT> exMapChecked(EF mapper, Class<VAL> inClass, Class<OUT> outClass, Class<OUTERR> outErrClass);

    /**
     * Calls {@code mapper} on IN and returns {@code Result<OUT,ERR>}
     * @param mapper A function mapping from type {@code <IN>} to type {@code <OUT>}
     * @param <OUT> The return type of {@code mapper}
     * @return If this Result accepted, the Result of executing {@code mapper} on this Result's value, otherwise {@code this}
     */
    <OUT, F extends Function<? super VAL, ? extends OUT>>
    Result<OUT> map(F mapper);

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
    Result<VAL> ifAccepted(Consumer<? super VAL> consumer);

    /**
     * If this Result is rejected, feed the included {@link Throwable} to the supplied {@link Consumer}, otherwise, do nothing.
     * Chainable.
     */
    Result<VAL> ifRejected(Consumer<? super Throwable> rejector);

    /**
     * Feed this Result's value or {@link Throwable} to the appropriate {@link Consumer}.
     * Chainable.
     */
    Result<VAL> then(Consumer<? super VAL> consumer, Consumer<? super Throwable> rejector);

    /**
     * Feed this Result's value to the supplied {@link Consumer} if it is present, otherwise feed {@code other} to it.
     * Chainable.
     */
    Result<VAL> acceptOrElse(Consumer<? super VAL> consumer, VAL other);

    /**
     * Feed this Result's value to the supplied {@link Consumer} if it is present, otherwise throw this Result's {@link Throwable}.
     * Chainable.
     */
    Result<VAL> acceptOrElseThrow(Consumer<? super VAL> consumer) throws Throwable;

    /**
     * Feed this Result's value to the supplied {@link Consumer} if it is present, otherwise
     * throw this Result's {@link Throwable} wrapped in a {@link RuntimeException}.
     * Chainable.
     */
    Result<VAL> acceptOrElseThrowRuntime(Consumer<? super VAL> consumer) throws RuntimeException;

    /**
     * Feed this Result's value to the supplied {@link Consumer} if it is present, otherwise
     * print the stack trace associated with this Result's {@link Throwable} to standard error.
     * Chainable.
     */
    Result<VAL> acceptOrPrintStacktrace(Consumer<? super VAL> consumer);
}
