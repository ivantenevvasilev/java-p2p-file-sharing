package bg.sofia.uni.fmi.mjt.torrent;

import bg.sofia.uni.fmi.mjt.torrent.client.TorrentClient;

import java.io.IOException;
import java.util.Scanner;

public class TorrentClientStart {
    private static final String DEFAULT_HOST = "localhost";

    public static void main(String... args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.printf("Enter server domain name (default %s): ", DEFAULT_HOST);
        String domain = sc.nextLine();
        if (domain.isEmpty()) {
            domain = DEFAULT_HOST;
        }
        System.out.print("Enter server port: ");
        int torrentServerPort = sc.nextInt();

        System.out.print("Enter local server share port: ");
        int localServerSharePort = sc.nextInt();

        TorrentClient client = new TorrentClient(domain, torrentServerPort, localServerSharePort);
        client.start();
    }
}
