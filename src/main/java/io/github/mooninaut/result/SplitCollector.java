package io.github.mooninaut.result;

import io.github.mooninaut.result.SplitStream.Builder;

import java.util.stream.Collector;

public interface SplitCollector<VAL, ERR extends Throwable>
        extends Collector<Result<VAL, ERR>, Builder<VAL, ERR>, SplitStream<VAL, ERR>> {

}
