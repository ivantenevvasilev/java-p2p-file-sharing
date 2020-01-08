package bg.sofia.uni.fmi.mjt.torrent.client.commands;

import bg.sofia.uni.fmi.mjt.torrent.client.commands.enums.ClientCommandsKeywords;
import bg.sofia.uni.fmi.mjt.torrent.client.parsers.interfaces.ResponseParser;
import bg.sofia.uni.fmi.mjt.torrent.client.repositories.interfaces.SharedFilesRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

/**
 * This command sends an unregister files command to the server
 * and removes files from local shared repository
 */
public class UnregisterFilesClientCommand implements ClientCommand {
    private final PrintWriter writer;
    private final BufferedReader reader;
    private final ResponseParser responseParser;
    private final String username;
    private final Set<String> paths;
    private final SharedFilesRepository sharedFilesRepository;

    public UnregisterFilesClientCommand(PrintWriter writer,
                                        BufferedReader reader,
                                        SharedFilesRepository sharedFilesRepository,
                                        ResponseParser responseParser,
                                        String username,
                                        Set<String> paths) {
        this.writer = writer;
        this.reader = reader;
        this.sharedFilesRepository = sharedFilesRepository;
        this.responseParser = responseParser;
        this.username = username;
        this.paths = paths;
    }
    /**
     * This command sends an unregister files command to the server
     * when there are paths provided, in the case when no paths are provided
     * no such request is sent.
     * @return the response from the server parsed by the response parser of the command
     */
    @Override
    public String execute() throws IOException {
        StringBuilder response = new StringBuilder();
        this.sharedFilesRepository.unregisterFiles(this.paths);
        if (!this.paths.isEmpty()) {
            this.writer.println(ClientCommandsKeywords.UNREGISTER_FILES_COMMAND.getKeyword() + ' ' +
                    username + ' ' + String.join(" ", this.paths));
            response.append(this.responseParser.parseResponse(this.reader));
        }

        return response.toString().trim();
    }
}
