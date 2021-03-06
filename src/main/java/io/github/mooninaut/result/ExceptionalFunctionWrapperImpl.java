package io.github.mooninaut.result;

import java.util.Objects;

/*
 * ExceptionalFunctionWrapperImpl.java
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

public class ExceptionalFunctionWrapperImpl<IN, OUT> implements
        ExceptionalFunctionWrapper<IN, OUT> {
    private final ExceptionalFunction<? super IN, ? extends OUT> exceptionalFunction;

    ExceptionalFunctionWrapperImpl(ExceptionalFunction<? super IN, ? extends OUT> exceptionalFunction) {
        this.exceptionalFunction = Objects.requireNonNull(exceptionalFunction);
    }

    public ExceptionalFunction<? super IN, ? extends OUT> getExceptionalFunction() {
        return exceptionalFunction;
    }

    @Override
    public Result<OUT> apply(IN in) {
        try {
            return Result.accept(exceptionalFunction.apply(in));
        } catch (Throwable ex) {
            Exceptions.throwIfUnchecked(ex);
            return new RejectedResult<>(ex);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExceptionalFunctionWrapperImpl<?, ?> that = (ExceptionalFunctionWrapperImpl<?, ?>) o;
        return exceptionalFunction.equals(that.exceptionalFunction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exceptionalFunction);
    }
}
