package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import bg.sofia.uni.fmi.mjt.torrent.models.Client;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces.ClientRepository;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces.FileRepository;

import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.stream.Collectors;

public class ListUsersServerCommand extends BaseServerCommand {

    public ListUsersServerCommand(FileRepository fileRepository,
                                  ClientRepository clientRepository,
                                  SocketChannel clientChannel) {
        super(fileRepository, clientRepository, clientChannel);
    }

    @Override
    public String execute() {
        Collection<Client> clients = this.clientRepository.getActiveClients();
        return clients.size() +
                System.lineSeparator() +
                this.clientRepository
                .getActiveClients()
                .stream()
                .map(Client::toString)
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
