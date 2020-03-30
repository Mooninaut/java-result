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

        Assert.assertEquals(results.get(0), "line1\nline2\nline3\nline4");
        Assert.assertEquals(results.get(1), "lineA\nlineB\nlineC");

        List<Throwable> errors = splitStream.getExceptionStream().collect(Collectors.toList());

        Assert.assertTrue(errors.get(0) instanceof NullPointerException);
        Assert.assertTrue(errors.get(1) instanceof MalformedURLException);
    }
}
