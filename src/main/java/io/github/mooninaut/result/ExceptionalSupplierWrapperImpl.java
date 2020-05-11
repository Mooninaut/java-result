package io.github.mooninaut.result;

import java.util.Objects;

/*
 * ExceptionalSupplierWrapperImpl.java
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

public class ExceptionalSupplierWrapperImpl<OUT, ERR extends Throwable> implements
        ExceptionalSupplierWrapper<OUT, ERR> {
    private final ExceptionalSupplier<? extends OUT, ? extends ERR> es;

    ExceptionalSupplierWrapperImpl(ExceptionalSupplier<? extends OUT, ? extends ERR> es) {
        this.es = Objects.requireNonNull(es);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Result<OUT> get() {
        try {
            return Result.accept(es.get());
        } catch (Throwable ex) {
            Exceptions.throwIfUnchecked(ex);
            return new RejectedResult<>((ERR) ex);
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
        ExceptionalSupplierWrapperImpl<?, ?> that = (ExceptionalSupplierWrapperImpl<?, ?>) o;
        return es.equals(that.es);
    }

    @Override
    public int hashCode() {
        return Objects.hash(es);
    }
}
