package bg.sofia.uni.fmi.mjt.torrent.client.commands;

import bg.sofia.uni.fmi.mjt.torrent.client.commands.enums.ClientCommandsKeywords;
import bg.sofia.uni.fmi.mjt.torrent.client.parsers.interfaces.ResponseParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Issues a request to the that fetches currently active users
 * and returns a string of them in a format that is readable
 */
public class ListFilesClientCommand implements ClientCommand {
    private static final String NO_FILES_AVAILABLE = "No files are currently shared.";
    private final PrintWriter writer;
    private final BufferedReader reader;
    private final ResponseParser responseParser;

    public ListFilesClientCommand(PrintWriter writer,
                                  BufferedReader reader,
                                  ResponseParser responseParser) {
        this.writer = writer;
        this.reader = reader;
        this.responseParser = responseParser;
    }

    /**
     *
     * @return Parsed server response in format that is resulted by the Response Parser
     * given in the constructor.
     * Or a no files message in the case in which the response is empty.
     * @throws IOException when an IO exception occurs
     */
    @Override
    public String execute() throws IOException {
        this.writer.println(ClientCommandsKeywords.LIST_FILES_COMMAND.getKeyword());
        String response = this.responseParser.parseResponse(this.reader);
        if (response.isEmpty()) {
            return NO_FILES_AVAILABLE;
        }
        return response;
    }
}
