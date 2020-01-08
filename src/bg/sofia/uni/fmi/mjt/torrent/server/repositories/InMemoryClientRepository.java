package bg.sofia.uni.fmi.mjt.torrent.server.repositories;

import bg.sofia.uni.fmi.mjt.torrent.exceptions.ClientAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.ClientNotFoundException;
import bg.sofia.uni.fmi.mjt.torrent.models.Client;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces.ClientRepository;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class InMemoryClientRepository implements ClientRepository {
    private HashMap<String, Client> activeClients = new HashMap<>();

    @Override
    public synchronized boolean isClientOnline(String username) {
        return this.activeClients.containsKey(username);
    }

    @Override
    public synchronized Client getClient(String username) {
        if (!this.isClientOnline(username)) {
            return null;
        }
        return this.activeClients.get(username);
    }

    @Override
    public synchronized void registerClient(@NotNull Client client) throws ClientAlreadyExistsException {
        if (this.activeClients.containsKey(client.getUsername())) {
            throw new ClientAlreadyExistsException(client.getUsername());
        }
        this.activeClients.put(client.getUsername(), client);
    }

    @Override
    public synchronized void unregisterClient(@NotNull Client client) throws ClientNotFoundException {
        if (!this.activeClients.containsKey(client.getUsername())) {
            throw new ClientNotFoundException(client.getUsername());
        }
        this.activeClients.remove(client.getUsername());
    }

    @Override
    public synchronized Collection<Client> getActiveClients() {
        return Collections.unmodifiableCollection(this.activeClients.values());
    }
}
