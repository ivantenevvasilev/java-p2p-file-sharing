package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import bg.sofia.uni.fmi.mjt.torrent.exceptions.ClientAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.torrent.models.Client;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces.ClientRepository;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces.FileRepository;

import java.nio.channels.SocketChannel;

public class RegisterUserServerCommand extends BaseServerCommand {
    static final String USER_ALREADY_EXISTS_MESSAGE = "User with given username is already online";
    static final String SUCCESSFULLY_CONNECTED_MESSAGE = "You have successfully registered as %s";
    private final String username;
    private int remoteServerPort;

    public RegisterUserServerCommand(FileRepository fileRepository,
                                     ClientRepository clientRepository,
                                     SocketChannel clientConnection,
                                     String username,
                                     int remoteServerPort) {
        super(fileRepository, clientRepository, clientConnection);
        this.username = username;
        this.remoteServerPort = remoteServerPort;
    }

    @Override
    public String execute() {
        try {
            this.clientRepository.registerClient(new Client(
                    this.username,
                    this.clientChannel.socket().getInetAddress().getHostAddress(),
                    this.clientChannel.socket().getPort(),
                    this.remoteServerPort
            ));
        } catch (ClientAlreadyExistsException e) {
            return USER_ALREADY_EXISTS_MESSAGE;
        }
        return String.format(SUCCESSFULLY_CONNECTED_MESSAGE, username);
    }
}
