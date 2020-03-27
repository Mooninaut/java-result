package io.github.mooninaut.result;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

/*
 * SplitCollector.java
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

public class SplitCollector<VAL, ERR extends Throwable>
        implements java.util.stream.Collector<Result<VAL, ERR>, SplitStream.Builder<VAL, ERR>, SplitStream<VAL, ERR>> {
    public static <VAL, ERR extends Throwable> SplitCollector<VAL, ERR> collector() {
        return new SplitCollector<>();
    }
    @Override
    public Supplier<SplitStream.Builder<VAL, ERR>> supplier() {
        return this::supply;
    }
    private SplitStream.Builder<VAL, ERR> supply() {
        return new SplitStream.Builder<>();
    }

    @Override
    public BiConsumer<SplitStream.Builder<VAL, ERR>, Result<VAL, ERR>> accumulator() {
        return this::accumulate;
    }
    private void accumulate(SplitStream.Builder<VAL, ERR> builders, Result<VAL, ERR> value) {
        builders.add(value);
    }

    @Override
    public BinaryOperator<SplitStream.Builder<VAL, ERR>> combiner() {
        return this::combine;
    }
    private SplitStream.Builder<VAL, ERR> combine(
            SplitStream.Builder<VAL, ERR> one,
            SplitStream.Builder<VAL, ERR> two) {
        SplitStream<VAL, ERR> twoStream = two.build();
        twoStream.getValueStream().forEach(one::addValue);
        twoStream.getExceptionStream().forEach(one::addException);
        return one;
    }

    @Override
    public Function<SplitStream.Builder<VAL, ERR>, SplitStream<VAL, ERR>> finisher() {
        return this::finish;
    }
    private SplitStream<VAL, ERR> finish(SplitStream.Builder<VAL, ERR> builders) {
        return builders.build();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }
}
