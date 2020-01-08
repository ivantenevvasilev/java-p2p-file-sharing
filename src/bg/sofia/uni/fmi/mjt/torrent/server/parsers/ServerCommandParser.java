package bg.sofia.uni.fmi.mjt.torrent.server.parsers;

import bg.sofia.uni.fmi.mjt.torrent.server.commands.ServerCommand;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.*;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces.ClientRepository;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces.FileRepository;

import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class ServerCommandParser {
    private static final String LIST_FILES_COMMAND = "list-files";
    private static final String LIST_USERS_COMMAND = "list-users";
    private static final String REGISTER_USER_COMMAND = "register-user";
    private static final String REGISTER_FILES_COMMAND = "register";
    private static final String UNREGISTER_FILES_COMMAND = "unregister";
    private static final String UNREGISTER_USER_COMMAND = "unregister-user";

    private FileRepository fileRepository;
    private ClientRepository clientRepository;
    private SocketChannel clientConnection;

    public ServerCommandParser(FileRepository fileRepository,
                               ClientRepository clientRepository,
                               SocketChannel clientConnection) {
        this.fileRepository = fileRepository;
        this.clientRepository = clientRepository;
        this.clientConnection = clientConnection;
    }

    public ServerCommand getCommand(String line) {
        if (line == null) {
            return null;
        }
        final int complexCommandMinLength = 3;
        String[] tokens = line.trim().split("\\s+");
        String command = tokens[0];
        if (command.equals(LIST_FILES_COMMAND)) {
            return new ListFilesServerCommand(this.fileRepository, this.clientRepository, this.clientConnection);
        }
        if (command.equals(LIST_USERS_COMMAND)) {
            return new ListUsersServerCommand(this.fileRepository, this.clientRepository, this.clientConnection);
        } if (command.equals(REGISTER_USER_COMMAND)) {
            if (tokens.length < complexCommandMinLength) {
                return null;
            }
            String username = tokens[1];
            int remoteServerPort;
            try {
                remoteServerPort = Integer.parseInt(tokens[2]);
            } catch (NumberFormatException e) {
                return null;
            }
            return new RegisterUserServerCommand(this.fileRepository,
                    this.clientRepository,
                    this.clientConnection,
                    username,
                    remoteServerPort);
        }
        if (command.equals(REGISTER_FILES_COMMAND) || command.equals(UNREGISTER_FILES_COMMAND)) {
            if (tokens.length < complexCommandMinLength) {
                return null;
            }
            String username = tokens[1];
            Collection<String> paths = Arrays.stream(tokens)
                    .skip(2)
                    .collect(Collectors.toList());

            if (command.equals(REGISTER_FILES_COMMAND)) {
                return new RegisterFilesServerCommand(this.fileRepository,
                        this.clientRepository,
                        this.clientConnection,
                        username,
                        paths);
            }
            return new UnregisterFilesServerCommand(this.fileRepository,
                    this.clientRepository,
                    this.clientConnection,
                    username,
                    paths);
        }
        if (command.equals(UNREGISTER_USER_COMMAND)) {
            if (tokens.length < 2) {
                return null;
            }

            final String username = tokens[1];

            return new UnregisterUserServerCommand(this.fileRepository,
                    this.clientRepository,
                    this.clientConnection,
                    username);
        }

        return null;
    }
}
