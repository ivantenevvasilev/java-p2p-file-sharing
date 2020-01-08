package bg.sofia.uni.fmi.mjt.torrent.client.parsers;

import bg.sofia.uni.fmi.mjt.torrent.client.commands.*;
import bg.sofia.uni.fmi.mjt.torrent.client.commands.enums.ClientCommandsKeywords;
import bg.sofia.uni.fmi.mjt.torrent.client.parsers.interfaces.ClientCommandParser;
import bg.sofia.uni.fmi.mjt.torrent.client.parsers.interfaces.ResponseParser;
import bg.sofia.uni.fmi.mjt.torrent.client.repositories.interfaces.SharedFilesRepository;
import bg.sofia.uni.fmi.mjt.torrent.client.repositories.interfaces.PeerRepository;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultClientCommandParser implements ClientCommandParser {
    private PrintWriter writer;
    private BufferedReader reader;
    private PeerRepository peerRepository;
    private final SharedFilesRepository sharedFilesRepository;
    private final PrintWriter outputStreamWriter;
    private ResponseParser responseParser;

    private String username;

    public DefaultClientCommandParser(PrintWriter writer,
                                      BufferedReader reader,
                                      PeerRepository peerRepository,
                                      SharedFilesRepository sharedFilesRepository,
                                      ResponseParser responseParser,
                                      String username,
                                      PrintWriter outputStreamWriter) {
        this.writer = writer;

        this.reader = reader;
        this.peerRepository = peerRepository;
        this.sharedFilesRepository = sharedFilesRepository;
        this.responseParser = responseParser;

        this.username = username;
        this.outputStreamWriter = outputStreamWriter;
    }


    @Override
    public ClientCommand getCommand(String line) {
        String[] tokens = line.trim().split("\\s+");

        String prefix = tokens[0];
        if (prefix.equals(ClientCommandsKeywords.LIST_USERS_COMMAND.getKeyword())) {
            return new ListClientsClientCommand(this.writer,
                    this.reader,
                    this.responseParser);
        }
        if (prefix.equals(ClientCommandsKeywords.LIST_FILES_COMMAND.getKeyword())) {
            return new ListFilesClientCommand(this.writer,
                    this.reader,
                    this.responseParser);
        }
        if (prefix.equals(ClientCommandsKeywords.REGISTER_FILES_COMMAND.getKeyword())) {
            Collection<File> filesToAdd = Arrays.stream(tokens).skip(1).map(File::new).collect(Collectors.toList());
            return new RegisterFilesClientCommand(this.writer,
                    this.reader,
                    this.sharedFilesRepository,
                    this.responseParser,
                    this.username,
                    filesToAdd);
        }
        if (prefix.equals(ClientCommandsKeywords.UNREGISTER_FILES_COMMAND.getKeyword())) {
            Set<String> filesToRemove = Arrays.stream(tokens)
                    .skip(1)
                    .map(File::new)
                    .map(File::getAbsolutePath)
                    .map(Paths::get)
                    .map(Path::normalize)
                    .map(Path::toString)
                    .collect(Collectors.toSet());

            return new UnregisterFilesClientCommand(this.writer,
                    this.reader,
                    this.sharedFilesRepository,
                    this.responseParser,
                    this.username,
                    filesToRemove);
        }
        if (prefix.equals(ClientCommandsKeywords.DOWNLOAD_FILE_COMMAND.getKeyword())) {
            final int expectedTokenLength = 4;
            final int usernameHostingFilePosition = 1;
            final int pathOnRemoteComputerPosition = 2;
            final int pathPosition = 3;

            if (tokens.length < expectedTokenLength) {
                return null;
            }
            String usernameHostingFile = tokens[usernameHostingFilePosition];
            String pathOnRemoteComputer = tokens[pathOnRemoteComputerPosition];
            String localPath = tokens[pathPosition];

            return new DownloadFileClientCommand(this.peerRepository, usernameHostingFile,
                    pathOnRemoteComputer, localPath, outputStreamWriter);
        }
        if (prefix.equals(ClientCommandsKeywords.DISCONNECT.getKeyword())) {
            return new DisconnectClientCommand(this.username, this.sharedFilesRepository, this.writer);
        }
        return null;
    }
}
