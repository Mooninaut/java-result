package io.github.mooninaut.result;

public interface Exceptions {

    public static boolean isCheckedException(Object object) {
        return (object instanceof Throwable)
                && !(object instanceof RuntimeException)
                && !(object instanceof Error);
    }

    public static boolean isUncheckedException(Object object) {
        return object instanceof RuntimeException || object instanceof Error;
    }

    public static void throwIfUnchecked(Throwable throwable) {
        if (throwable instanceof Error) {
            throw (Error) throwable;
        }
        if (throwable instanceof RuntimeException) {
            throw (RuntimeException) throwable;
        }
    }
}
