package bg.sofia.uni.fmi.mjt.torrent.client.parsers.interfaces;

import bg.sofia.uni.fmi.mjt.torrent.client.models.Peer;

public interface PeerParser {
    Peer parseRemoteUser(String line);
}
