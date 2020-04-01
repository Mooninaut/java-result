package io.github.mooninaut.result;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertNotNull;

public class ResultTest {

    @Test
    public void fileReadTest() {
        List<String> fileNameList = Arrays.asList("files/file1.txt", "files/file2.txt", "files/doesnotexist");
        SplitStream<String, Throwable> splitStream = Result.splitStream(
            Stream.concat(
                fileNameList.stream()
                    .map(fileName -> ResultTest.class.getClassLoader().getResource(fileName))
                    .map(Result::requireNonNull),
                Stream.of("badURL").map(ExceptionalFunctionWrapper.wrap(URL::new))
            )
                .map(Result.exMapperChecked(URL::toURI, URL.class, URI.class, URISyntaxException.class))
                .map(Result.mapper(Paths::get))
                .map(Result.exMapper(Files::newBufferedReader))
                .map(Result.exMapper(BufferedReader::lines))
                .map(Result.mapper(stream -> stream.collect(Collectors.toList())))
                .map(Result.mapper(lines -> String.join("\n", lines)))
        );

        List<String> results = splitStream.getValueStream().collect(Collectors.toList());

        assertEquals(results.get(0), "line1\nline2\nline3\nline4");
        assertEquals(results.get(1), "lineA\nlineB\nlineC");

        List<Throwable> errors = splitStream.getExceptionStream().collect(Collectors.toList());

        assertTrue(errors.get(0) instanceof NullPointerException);
        Assert.assertTrue(errors.get(1) instanceof MalformedURLException);
    }

    @Test
    public void cannotRejectNonThrowable() {
        boolean success;
        try {
            Result.reject((Throwable) (Object) "not a throwable");
            success = false;
        } catch (Throwable err) {
            success = err instanceof ClassCastException;
        }
        assertTrue("Can reject non-Throwable", success);
    }

    @Test
    public void canAcceptThrowable() { // don't know why you'd want to but there's no law against it
        boolean success;
        try {
            Result.accept(new Exception());
            success = true;
        } catch (Throwable err) {
            err.printStackTrace();
            success = false;
        }
        assertTrue("Cannot accept Throwable", success);
    }

    @Test
    public void cannotRejectNull() {
        boolean success;
        try {
            Result.reject(null);
            success = false;
        } catch (Throwable err) {
            success = err instanceof NullPointerException;
        }
        assertTrue("Can reject null", success);
    }

    @Test
    public void resultFromThrowableIsRejected() {
        assertTrue(Result.from(new Throwable()) instanceof RejectedResult);
    }
    @Test
    public void resultFromNullIsEmpty() {
        assertTrue(Result.from(null) instanceof EmptyResult);
    }
    @Test
    public void resultFromNonNullNonThrowableIsAccepted() {
        assertTrue(Result.from(new Object()) instanceof AcceptedResult);
    }

    @Test
    public void ofCatchesCorrectly() {
        Result<Object, Throwable> result = null;
        Throwable throwable = new Throwable();
        try {
            result = Result.of(() -> { throw throwable; });
        } catch (Throwable ignored) { }

        assertNotNull(result);
        assertTrue(result.isRejected());
        assertSame(result.getException(), throwable);
        assertEquals(result, Result.reject(throwable));
    }

    @Test
    public void ofReturnsEmptyCorrectly() {
        Result<Object, Throwable> result = Result.of(() -> null);

        assertTrue(result.isAccepted());
        assertTrue(result.isEmpty());
        assertEquals(result, Result.empty());
    }

    @Test
    public void ofReturnsAcceptedCorrectly() {
        Object o = new Object();
        Result<Object, Throwable> result = Result.of(() -> o);

        assertTrue(result.isAccepted());
        assertTrue(result.isPresent());
        assertSame(result.get(), o);
        assertEquals(result, Result.accept(o));
    }

    @Test
    public void requireNonNullRejectsWithNullPointerException() {
        assertEquals(Result.requireNonNull(null).getException().getClass(), NullPointerException.class);
    }

    @Test
    public void requireNonNullAcceptsNonNullValue() {
        Object object = new Object();
        assertSame(Result.requireNonNull(object).get(), object);
    }

    @Test
    public void safeCastAcceptsValidCast() {
        String string = "a string";
        Result<CharSequence, ClassCastException> result = Result.safeCast(string, CharSequence.class);
        assertSame(result.get(), string);
    }

    @Test
    public void safeCastRejectsInvalidCast() {
        Object object = new Object();
        Result<CharSequence, ClassCastException> result = Result.safeCast(object, CharSequence.class);
        assertEquals(result.getException().getClass(), ClassCastException.class);
    }

    @Test
    public void safeCastAcceptsNull() {
        Result<CharSequence, ClassCastException> result = Result.safeCast(null, CharSequence.class);
        assertNull(result.get());
    }
}
