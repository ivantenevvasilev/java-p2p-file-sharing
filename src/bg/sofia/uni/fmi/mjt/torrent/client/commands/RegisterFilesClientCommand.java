package bg.sofia.uni.fmi.mjt.torrent.client.commands;

import bg.sofia.uni.fmi.mjt.torrent.client.commands.enums.ClientCommandsKeywords;
import bg.sofia.uni.fmi.mjt.torrent.client.parsers.interfaces.ResponseParser;
import bg.sofia.uni.fmi.mjt.torrent.client.repositories.interfaces.SharedFilesRepository;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Responsible for sending a register request to the server
 * given paths and username
 * It is also responsible for filtering the invalid paths
 * given to server.
 */
public class RegisterFilesClientCommand implements ClientCommand {
    private final PrintWriter writer;
    private final BufferedReader reader;
    private final SharedFilesRepository sharedFilesRepository;
    private final ResponseParser responseParser;
    private final String username;
    private final Collection<File> files;

    public RegisterFilesClientCommand(PrintWriter writer,
                                      BufferedReader reader,
                                      SharedFilesRepository sharedFilesRepository,
                                      ResponseParser responseParser,
                                      String username,
                                      Collection<File> files) {
        this.writer = writer;
        this.reader = reader;
        this.sharedFilesRepository = sharedFilesRepository;
        this.responseParser = responseParser;
        this.username = username;
        this.files = files;
    }

    /**
     * @return A human readable response.
     * Starting with a [WARNING] line when there are invalid paths
     * on the local machine provided, or nonexistent files.
     * Followed by the response of the server with the remaining valid files
     * if there are any.
     * In the case where there are no valid files to issue to the server, a request
     * is not sent.
     * @throws IOException when an IO error occurs.
     */
    @Override
    public String execute() throws IOException {
        StringBuilder response = new StringBuilder();

        Set<File> filesNotFound = files
                .stream()
                .filter((f) -> !f.isFile())
                .map(File::getAbsoluteFile)
                .collect(Collectors.toSet());
        if (!filesNotFound.isEmpty()) {
            response.append("[WARNING]");
            response.append(System.lineSeparator());
            response.append("The following files were not found");
            response.append(System.lineSeparator());
            for (File path : filesNotFound) {
                response.append(path);
                response.append(System.lineSeparator());
            }
        }

        Set<File> filesToAdd = files
                .stream()
                .filter(File::isFile)
                .collect(Collectors.toSet());


        for (File file : filesToAdd) {
            this.sharedFilesRepository.registerFile(
                    Paths.get(file.getAbsolutePath()).normalize().toString(), file.length());
        }

        if (!filesToAdd.isEmpty()) {
            response.append("The following files were added");
            response.append(System.lineSeparator());
            this.writer.println(ClientCommandsKeywords.REGISTER_FILES_COMMAND.getKeyword() + ' ' +
                    username + ' ' +
                    filesToAdd
                            .stream()
                            .map(File::getAbsolutePath)
                            .map(Paths::get)
                            .map(Path::normalize)
                            .map(Path::toString)
                            .collect(Collectors.joining(" ")));
            response.append(this.responseParser.parseResponse(this.reader));
        }

        return response.toString().trim();
    }
}
