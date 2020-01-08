package bg.sofia.uni.fmi.mjt.torrent.client.parsers.interfaces;

import java.io.BufferedReader;
import java.io.IOException;

public interface ResponseParser {
    String parseResponse(BufferedReader reader) throws IOException;
}
