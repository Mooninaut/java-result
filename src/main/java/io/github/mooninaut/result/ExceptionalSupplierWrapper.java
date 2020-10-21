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

public interface ExceptionalSupplierWrapper<OUT> extends Supplier<Result<OUT>> {

    static <OUT>
    ExceptionalSupplierWrapper<OUT>
    wrap(ExceptionalSupplier<? extends OUT> es) {
        return new ExceptionalSupplierWrapperImpl<>(es);
    }

    static <IN, OUT>
    ExceptionalSupplierWrapper<OUT>
    wrapChecked(
            ExceptionalSupplier<? extends OUT> es,
            Class<OUT> outClass) {
        return new CheckedExceptionalSupplierWrapperImpl<>(es, outClass);
    }

    @Override
    Result<OUT> get();
}
