package bg.sofia.uni.fmi.mjt.torrent.client.parsers;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class DefaultResponseParserTests {
    @Test
    public void testResponseParser() throws IOException {
        String[] randomLines = {
            "text",
            "test",
            "tst"
        };
        StringBuilder randomResponse = new StringBuilder();
        randomResponse.append(randomLines.length);
        randomResponse.append(System.lineSeparator());
        for (String line : randomLines) {
            randomResponse.append(line);
            randomResponse.append(System.lineSeparator());
        }

        String expected = Arrays
                .stream(randomResponse.toString().split(System.lineSeparator()))
                .skip(1)
                .collect(Collectors.joining(System.lineSeparator()));
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(randomResponse.toString().getBytes())))) {
            String result = new DefaultResponseParser().parseResponse(reader);
            assertEquals(expected, result);
        }

    }
}
