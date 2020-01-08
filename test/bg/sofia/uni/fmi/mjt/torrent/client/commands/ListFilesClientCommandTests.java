package bg.sofia.uni.fmi.mjt.torrent.client.commands;

import bg.sofia.uni.fmi.mjt.torrent.client.parsers.DefaultResponseParser;
import bg.sofia.uni.fmi.mjt.torrent.models.SharedFile;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class ListFilesClientCommandTests {
    @Test
    public void testListFilesCommandRespondsInExpectedFormat() throws IOException {
        SharedFile[] files = {
            new SharedFile("peter", "/opt"),
            new SharedFile("root", "/var"),
            new SharedFile("etc", "/file")
        };

        StringBuilder expectedServerResponse = new StringBuilder();
        StringBuilder expectedCommandResult = new StringBuilder();

        expectedServerResponse.append(files.length);
        expectedServerResponse.append(System.lineSeparator());

        for (SharedFile file : files) {
            expectedServerResponse.append(file.toString());
            expectedServerResponse.append(System.lineSeparator());

            expectedCommandResult.append(file.toString());
            expectedCommandResult.append(System.lineSeparator());
        }

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(expectedServerResponse.toString().getBytes());
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(outputStream);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            ClientCommand command = new ListFilesClientCommand(writer,
                    reader,
                    new DefaultResponseParser());

            String result = command.execute().trim();
            String expected = expectedCommandResult.toString().trim();

            assertEquals(expected, result);
        }
    }
}
