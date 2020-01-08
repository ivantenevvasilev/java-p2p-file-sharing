package bg.sofia.uni.fmi.mjt.torrent.client.parsers;

import bg.sofia.uni.fmi.mjt.torrent.client.models.Peer;
import bg.sofia.uni.fmi.mjt.torrent.client.parsers.interfaces.PeerParser;

import java.util.Arrays;

public class DefaultPeerParser implements PeerParser {
    private static final int EXPECTED_LINE_PARTS_COUNT = 3;
    @Override
    public Peer parseRemoteUser(String line) {
        String[] parts = Arrays
                .stream(line.trim().split(":"))
                .map(String::trim).toArray(String[]::new);
        if (parts.length != EXPECTED_LINE_PARTS_COUNT) {
            return null;
        }
        String username = parts[0];
        String hostname = parts[1];
        int remoteServerPort;
        try {
            remoteServerPort  = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            return null;
        }

        return new Peer(username, hostname, remoteServerPort);
    }
}
