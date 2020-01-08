package bg.sofia.uni.fmi.mjt.torrent;

import bg.sofia.uni.fmi.mjt.torrent.client.TorrentClient;
import bg.sofia.uni.fmi.mjt.torrent.server.TorrentServer;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.InMemoryClientRepository;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.InMemoryFileRepository;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces.ClientRepository;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces.FileRepository;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        final FileRepository fileRepository = new InMemoryFileRepository();
        final ClientRepository clientRepository = new InMemoryClientRepository();
        final int randomPort = 1235;
        new Thread(() -> {
            try {
                TorrentServer server = new TorrentServer(randomPort, fileRepository, clientRepository);
                System.out.println("Server listening on " + randomPort);
                server.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        TorrentClient client = new TorrentClient("localhost", randomPort);
        client.start();
    }
}
