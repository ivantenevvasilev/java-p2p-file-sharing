package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import bg.sofia.uni.fmi.mjt.torrent.exceptions.ClientNotFoundException;
import bg.sofia.uni.fmi.mjt.torrent.models.Client;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces.ClientRepository;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces.FileRepository;

import java.nio.channels.SocketChannel;

public class UnregisterUserServerCommand extends BaseServerCommand {
    static final String CLIENT_NOT_FOUND_MESSAGE = "Client not found. No need to unregister";
    static final String CLIENT_UNREGISTERED_MESSAGE = "Successfully unregistered client %s";
    private final String username;

    public UnregisterUserServerCommand(FileRepository fileRepository,
                                       ClientRepository clientRepository,
                                       SocketChannel clientChannel,
                                       String username) {
        super(fileRepository, clientRepository, clientChannel);
        this.username = username;
    }

    @Override
    public String execute() {
        Client client = this.clientRepository.getClient(this.username);
        if (client == null) {
            return CLIENT_NOT_FOUND_MESSAGE;
        }
        try {
            this.clientRepository.unregisterClient(client);
        } catch (ClientNotFoundException e) {
            // This should never happen because of the check earlier
            // but just in case.
            return CLIENT_NOT_FOUND_MESSAGE;
        }
        return String.format(CLIENT_UNREGISTERED_MESSAGE, this.username);
    }
}
