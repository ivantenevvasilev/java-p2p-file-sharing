package bg.sofia.uni.fmi.mjt.torrent.client.commands;

import bg.sofia.uni.fmi.mjt.torrent.client.models.Peer;
import bg.sofia.uni.fmi.mjt.torrent.client.parsers.DefaultPeerParser;
import bg.sofia.uni.fmi.mjt.torrent.client.parsers.interfaces.PeerParser;
import bg.sofia.uni.fmi.mjt.torrent.client.parsers.interfaces.ResponseParser;
import bg.sofia.uni.fmi.mjt.torrent.client.repositories.interfaces.PeerRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is responsible for updating the client repository by
 * sending a request to the server, fetching the active users and
 * updating the local repository.
 */
public class UpdateUserMappingClientCommand implements ClientCommand {
    private final PrintWriter writer;
    private final BufferedReader reader;
    private final PeerRepository peerRepository;
    private final ResponseParser responseParser;

    public UpdateUserMappingClientCommand(PrintWriter writer,
                                          BufferedReader reader,
                                          PeerRepository peerRepository,
                                          ResponseParser responseParser) {
        this.writer = writer;
        this.reader = reader;
        this.peerRepository = peerRepository;
        this.responseParser = responseParser;
    }

    @Override
    public String execute() throws IOException {
        PeerParser parser = new DefaultPeerParser();
        String users = new ListClientsClientCommand(this.writer,
                this.reader,
                this.responseParser).execute();

        Set<Peer> clients = Arrays
                .stream(users.split(System.lineSeparator()))
                .map(parser::parseRemoteUser)
                .collect(Collectors.toSet());
        this.peerRepository.updateRemoteUsers(clients);
        return null;
    }
}
