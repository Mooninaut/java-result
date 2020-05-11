package io.github.mooninaut.result;

@FunctionalInterface
public interface ExceptionalSupplier<OUT> {

    OUT get() throws Throwable;
}
