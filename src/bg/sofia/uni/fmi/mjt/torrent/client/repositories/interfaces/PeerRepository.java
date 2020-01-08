package bg.sofia.uni.fmi.mjt.torrent.client.repositories.interfaces;

import bg.sofia.uni.fmi.mjt.torrent.client.models.Peer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public interface PeerRepository {
    Collection<Peer> getRemoteUsers();
    Peer getUser(@NotNull String username);
    void updateRemoteUsers(@NotNull Set<Peer> users);
}
