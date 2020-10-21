package io.github.mooninaut.result;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

/*
 * SplitCollectorImpl.java
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

public class SplitCollectorImpl<VAL>
        implements SplitCollector<VAL> {

    private enum Self {
        INSTANCE;

        private final SplitCollectorImpl<?> value = new SplitCollectorImpl<>();

        @SuppressWarnings("unchecked")
        public static <VAL> SplitCollector<VAL> getInstance() {
            return (SplitCollector<VAL>) INSTANCE.value;
        }
    }

    public static <VAL> SplitCollector<VAL> collector() {
        return Self.getInstance();
    }

    private SplitCollectorImpl() { }

    @Override
    public Supplier<SplitStream.Builder<VAL>> supplier() {
        return SplitStream.Builder::new;
    }

    @Override
    public BiConsumer<SplitStream.Builder<VAL>, Result<VAL>> accumulator() {
        return SplitCollectorImpl::accumulate;
    }
    private static <VAL> void accumulate(SplitStream.Builder<VAL> builders, Result<VAL> value) {
        builders.add(value);
    }

    @Override
    public BinaryOperator<SplitStream.Builder<VAL>> combiner() {
        return SplitCollectorImpl::combine;
    }
    private static <VAL> SplitStream.Builder<VAL> combine(
            SplitStream.Builder<VAL> one,
            SplitStream.Builder<VAL> two) {
        return one.append(two);
    }

    @Override
    public Function<SplitStream.Builder<VAL>, SplitStream<VAL>> finisher() {
        return SplitCollectorImpl::finish;
    }
    private static <VAL> SplitStream<VAL> finish(SplitStream.Builder<VAL> builders) {
        return builders.build();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }
}
