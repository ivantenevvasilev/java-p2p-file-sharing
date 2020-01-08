package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import bg.sofia.uni.fmi.mjt.torrent.exceptions.ClientAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.torrent.models.Client;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.InMemoryClientRepository;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces.ClientRepository;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ListUsersServerCommandTests {
    private static Collection<Client> sampleClients;

    private ServerCommand listUsersServerCommand;
    private ClientRepository clientRepository;

    @BeforeClass
    public static void setUpClass() {
        final int[] samplePorts = {143, 2113, 3442};
        sampleClients = List.of(
                new Client("root", "localhost", samplePorts[0], samplePorts[1]),
                new Client("username", "192.168.0.4", samplePorts[1], samplePorts[2]),
                new Client("peter", "211.132.4.1", samplePorts[2], samplePorts[0])
        );
    }

    @Before
    public void setUp() {
        this.clientRepository = new InMemoryClientRepository();
        this.listUsersServerCommand = new ListUsersServerCommand(null, this.clientRepository, null);
    }

    @Test
    public void testListUsersServerCommandReturnsUsersInCorrectFormat() throws ClientAlreadyExistsException {
        for (Client client : sampleClients) {
            this.clientRepository.registerClient(client);
        }
        StringBuilder expectedStringBuilder = new StringBuilder();
        expectedStringBuilder.append(sampleClients.size());
        expectedStringBuilder.append(System.lineSeparator());
        for (Client client : this.clientRepository.getActiveClients()) {
            expectedStringBuilder.append(client.toString());
            expectedStringBuilder.append(System.lineSeparator());
        }
        String expected = expectedStringBuilder.toString().trim();
        String result = listUsersServerCommand.execute();

        assertEquals("List Users Server ServerCommand does not return users in expected format.", expected, result);
    }
}
