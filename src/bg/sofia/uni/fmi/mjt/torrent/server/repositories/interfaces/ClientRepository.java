package bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces;

import bg.sofia.uni.fmi.mjt.torrent.exceptions.ClientAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.ClientNotFoundException;
import bg.sofia.uni.fmi.mjt.torrent.models.Client;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface ClientRepository {

    /**
     * @param username - Username to the corresponding client to find.
     * @return whether or not such a user is present in repository.
     */

    boolean isClientOnline(String username);

    /**
     * @param username - Username to the corresponding client to find.
     * @return null if client is not present or a client object if such
     * client is found.
     */
    Client getClient(String username);


    /**
     * @param client - Client to add to repository.
     * @throws ClientAlreadyExistsException when given client is already present in repository.
     */
    void registerClient(@NotNull Client client) throws ClientAlreadyExistsException;


    /**
     * @param client - Client to remove from repository.
     * @throws ClientNotFoundException when client is not present in repository.
     */
    void unregisterClient(Client client) throws ClientNotFoundException;

    /**
     * @return Read-only collection of clients in repository
     */
    Collection<Client> getActiveClients();

}
