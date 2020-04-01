package io.github.mooninaut.result;

@FunctionalInterface
public interface ExceptionalSupplier<OUT, ERR extends Throwable> {

    OUT get() throws ERR;
}
