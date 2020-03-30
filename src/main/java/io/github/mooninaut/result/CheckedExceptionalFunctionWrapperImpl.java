package io.github.mooninaut.result;

import java.util.Objects;

/*
 * CheckedExceptionalFunctionWrapperImpl.java
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

public class CheckedExceptionalFunctionWrapperImpl<IN, OUT, ERR extends Throwable> extends
        ExceptionalFunctionWrapperImpl<IN, OUT, ERR> {
    private final Class<IN> inClass;
    private final Class<OUT> outClass;
    private final Class<ERR> errClass;
    CheckedExceptionalFunctionWrapperImpl(
            ExceptionalFunction<? super IN, ? extends OUT, ? extends ERR> ef,
            Class<IN> inClass,
            Class<OUT> outClass,
            Class<ERR> errClass) {
        super(ef);
        this.inClass = Objects.requireNonNull(inClass);
        this.outClass = Objects.requireNonNull(outClass);
        this.errClass = Objects.requireNonNull(errClass);
    }

    @Override
    public Result<OUT, ERR> apply(IN in) throws ClassCastException {
        Result<OUT, ERR> result = super.apply(inClass.cast(in));

        if (result.isAccepted()) {
            return result.checkedCast(outClass);
        }

        System.err.println("Attempting to cast " + result + " to " + errClass);
        errClass.cast(result.getException());

        return result;
    }

    public Class<IN> getInClass() {
        return inClass;
    }

    public Class<OUT> getOutClass() {
        return outClass;
    }

    public Class<ERR> getErrClass() {
        return errClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        CheckedExceptionalFunctionWrapperImpl<?, ?, ?> that = (CheckedExceptionalFunctionWrapperImpl<?, ?, ?>) o;
        return getInClass().equals(that.getInClass()) &&
                getOutClass().equals(that.getOutClass()) &&
                getErrClass().equals(that.getErrClass());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getInClass(), getOutClass(), getErrClass());
    }
}
