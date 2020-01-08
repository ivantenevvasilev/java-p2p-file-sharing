package bg.sofia.uni.fmi.mjt.torrent;

import bg.sofia.uni.fmi.mjt.torrent.server.TorrentServer;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.InMemoryClientRepository;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.InMemoryFileRepository;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces.ClientRepository;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces.FileRepository;

import java.io.IOException;
import java.util.Scanner;

public class TorrentServerStart {
    private static final String PORT_TAKEN = "Port %d is taken.\n";
    private static final String ENTER_PORT_MESSAGE = "Enter port: ";

    public static void main(String... args) {
        Scanner sc = new Scanner(System.in);
        boolean isStarted = false;

        final FileRepository fileRepository = new InMemoryFileRepository();
        final ClientRepository clientRepository = new InMemoryClientRepository();
        while (!isStarted) {
            System.out.print(ENTER_PORT_MESSAGE);
            int port = sc.nextInt();
            try {
                TorrentServer server = new TorrentServer(port, fileRepository, clientRepository);
                isStarted = true;
                System.out.println("Starting up server...");
                server.start();
            } catch (IOException e) {
                System.out.printf(PORT_TAKEN, port);
            }
        }
    }
}
