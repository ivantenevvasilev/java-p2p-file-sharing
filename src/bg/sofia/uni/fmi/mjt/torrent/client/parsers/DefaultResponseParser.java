package bg.sofia.uni.fmi.mjt.torrent.client.parsers;

import bg.sofia.uni.fmi.mjt.torrent.client.parsers.interfaces.ResponseParser;

import java.io.BufferedReader;
import java.io.IOException;

public class DefaultResponseParser implements ResponseParser {
    @Override
    public String parseResponse(BufferedReader reader) throws IOException {
        StringBuilder responseBuilder = new StringBuilder();
        String line = reader.readLine();
        final int countOfLines = Integer.parseInt(line);
        for (int i = 0; i < countOfLines; ++i) {
            line = reader.readLine();
            responseBuilder.append(line);
            responseBuilder.append(System.lineSeparator());
        }
        return responseBuilder.toString().trim();
    }
}
