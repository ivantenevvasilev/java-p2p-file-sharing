package bg.sofia.uni.fmi.mjt.torrent.server.repositories;

import bg.sofia.uni.fmi.mjt.torrent.exceptions.ClientAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.torrent.exceptions.ClientNotFoundException;
import bg.sofia.uni.fmi.mjt.torrent.models.Client;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces.ClientRepository;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.*;

public class InMemoryClientRepositoryTests {
    private ClientRepository clientRepository;

    private static Client sampleClient;

    @BeforeClass
    public static void setUpClass() {
        final int port = 1421;
        sampleClient = new Client("test", "localhost", port, port);
    }

    @Before
    public void setUp() {
        this.clientRepository = new InMemoryClientRepository();
    }

    @Test
    public void testClientRepositoryRegistersClient() throws ClientAlreadyExistsException {
        this.clientRepository.registerClient(sampleClient);

        assertTrue("Client repository register client does not make user active",
                this.clientRepository.isClientOnline(sampleClient.getUsername()));
        assertEquals("Client repository register client does not update active clients.", 1,
                this.clientRepository.getActiveClients().size());
    }

    @Test(expected = ClientAlreadyExistsException.class)
    public void testClientRepositoryRegisterClientWithDuplicateUsersThrowsException()
            throws ClientAlreadyExistsException {
        this.clientRepository.registerClient(sampleClient);
        this.clientRepository.registerClient(sampleClient);
        fail("Client repository allows duplicate users to be registered");
    }

    @Test
    public void testClientRepositoryUnregisterClientRemovesClientFromRepository() throws ClientNotFoundException,
            ClientAlreadyExistsException {
        this.clientRepository.registerClient(sampleClient);

        this.clientRepository.unregisterClient(sampleClient);
        assertFalse("Client repository unregister client did not remove client",
                this.clientRepository.isClientOnline(sampleClient.getUsername()));
        assertEquals("Client repository unregister client did not update active clients",
                0,
                this.clientRepository.getActiveClients().size());
    }

    @Test(expected = ClientNotFoundException.class)
    public void testClientRepositoryUnregisterClientThrowsWhenClientNotPresent() throws ClientNotFoundException {
        this.clientRepository.unregisterClient(sampleClient);
        fail("Client Repository allows removal of clients that are not connected at all.");
    }

    @Test
    public void testClientRepositoryGetClientReturnsNullWhenClientNotActive() {
        assertNull("Client Repository get client method returns client when there is no such client connected.",
                this.clientRepository.getClient(sampleClient.getUsername()));
    }

    @Test
    public void testGetClientRepositoryGetClientReturnsCorrectClient() throws ClientAlreadyExistsException {
        this.clientRepository.registerClient(sampleClient);
        assertEquals("Client Repository get client method does not return correct client",
                sampleClient,
                this.clientRepository.getClient(sampleClient.getUsername()));
    }
}
