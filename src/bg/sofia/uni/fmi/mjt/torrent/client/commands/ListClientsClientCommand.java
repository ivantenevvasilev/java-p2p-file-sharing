package bg.sofia.uni.fmi.mjt.torrent.client.commands;

import bg.sofia.uni.fmi.mjt.torrent.client.commands.enums.ClientCommandsKeywords;
import bg.sofia.uni.fmi.mjt.torrent.client.parsers.interfaces.ResponseParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This command issues a request to main server to see who
 * is currently active and returns a string in format where
 * each line is in the format
 * <user> : <hostname>:<port>
 */
public class ListClientsClientCommand  implements ClientCommand {

    private static final String NO_USERS_ONLINE_MESSAGE = "Currently nobody is online.";
    private final PrintWriter writer;
    private final BufferedReader reader;
    private final ResponseParser responseParser;

    public ListClientsClientCommand(PrintWriter writer,
                                    BufferedReader reader,
                                    ResponseParser responseParser) {
        this.writer = writer;
        this.reader = reader;
        this.responseParser = responseParser;
    }
    /**
     * This command issues a request to main server to see who
     * is currently active and returns a string in format where
     * each line is in the format
     * <user> : <hostname>:<port>
     *
     * Uses the ResponseParser given in the constructor to
     * parse the result given by the server
     */
    @Override
    public String execute() throws IOException {
        this.writer.println(ClientCommandsKeywords.LIST_USERS_COMMAND.getKeyword());
        String response = this.responseParser.parseResponse(this.reader);
        if (response.isEmpty()) {
            return NO_USERS_ONLINE_MESSAGE;
        }
        return response;
    }
}
