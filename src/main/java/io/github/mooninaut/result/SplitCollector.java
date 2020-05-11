package io.github.mooninaut.result;

import io.github.mooninaut.result.SplitStream.Builder;

import java.util.stream.Collector;

public interface SplitCollector<VAL>
        extends Collector<Result<VAL>, Builder<VAL>, SplitStream<VAL>> {

}
