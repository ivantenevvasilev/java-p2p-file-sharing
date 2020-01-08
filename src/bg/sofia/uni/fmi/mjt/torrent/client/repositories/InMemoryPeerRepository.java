package bg.sofia.uni.fmi.mjt.torrent.client.repositories;

import bg.sofia.uni.fmi.mjt.torrent.client.models.Peer;
import bg.sofia.uni.fmi.mjt.torrent.client.repositories.interfaces.PeerRepository;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class InMemoryPeerRepository implements PeerRepository {
    private Map<String, Peer> users = new HashMap<>();

    @Override
    public Collection<Peer> getRemoteUsers() {
        return this.users.values();
    }

    @Override
    public Peer getUser(@NotNull String username) {
        return this.users.get(username);
    }

    @Override
    public void updateRemoteUsers(@NotNull Set<Peer> users) {
        this.users.clear();
        for (Peer u : users) {
            this.users.put(u.getUsername(), u);
        }
    }
}
