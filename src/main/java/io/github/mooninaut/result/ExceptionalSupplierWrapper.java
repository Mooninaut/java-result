package io.github.mooninaut.result;

import java.util.function.Supplier;

/*
 * ExceptionalSupplierWrapper.java
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

public interface ExceptionalSupplierWrapper<OUT, ERR extends Throwable> extends Supplier<Result<OUT, ERR>> {

    static <OUT, ERR extends Throwable>
    ExceptionalSupplierWrapper<OUT, ERR>
    wrap(ExceptionalSupplier<? extends OUT, ? extends ERR> es) {
        return new ExceptionalSupplierWrapperImpl<>(es);
    }

    static <IN, OUT, ERR extends Throwable>
    ExceptionalSupplierWrapper<OUT, ERR>
    wrapChecked(
            ExceptionalSupplier<? extends OUT, ? extends ERR> es,
            Class<OUT> outClass,
            Class<ERR> errClass) {
        return new CheckedExceptionalSupplierWrapperImpl<>(es, outClass, errClass);
    }

    @Override
    Result<OUT, ERR> get();
}
