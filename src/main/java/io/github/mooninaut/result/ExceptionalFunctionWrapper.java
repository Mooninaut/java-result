package io.github.mooninaut.result;

import java.util.function.Function;

/*
 * ExceptionalFunctionWrapper.java
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

public interface ExceptionalFunctionWrapper<IN, OUT, ERR extends Throwable> extends Function<IN, Result<OUT, ERR>> {

    static <IN, OUT, ERR extends Throwable>
    ExceptionalFunctionWrapperImpl<IN, OUT, ERR>
    wrap(ExceptionalFunction<? super IN, ? extends OUT, ? extends ERR> ef) {
        return new ExceptionalFunctionWrapperImpl<>(ef);
    }

    static <IN, OUT, ERR extends Throwable>
    ExceptionalFunctionWrapper<IN, OUT, ERR>
    wrapChecked(
            ExceptionalFunction<? super IN, ? extends OUT, ? extends ERR> ef,
            Class<IN> inClass,
            Class<OUT> outClass,
            Class<ERR> errClass) {
        return new CheckedExceptionalFunctionWrapperImpl<>(ef, inClass, outClass, errClass);
    }

    @Override
    Result<OUT, ERR> apply(IN in);


}
