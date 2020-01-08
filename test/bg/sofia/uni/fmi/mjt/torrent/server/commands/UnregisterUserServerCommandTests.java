package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import bg.sofia.uni.fmi.mjt.torrent.exceptions.ClientAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.torrent.models.Client;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.InMemoryClientRepository;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.InMemoryFileRepository;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces.ClientRepository;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces.FileRepository;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UnregisterUserServerCommandTests {
    private static String sampleClientName;
    private static Client sampleClient;

    private ClientRepository clientRepository;

    private ServerCommand command;

    @BeforeClass
    public static void setUpClass() {
        final int randomPort = 4215;
        sampleClientName = "user";
        sampleClient = new Client(sampleClientName, "domain", randomPort, randomPort);
    }

    @Before
    public void setUp() {
        this.clientRepository = new InMemoryClientRepository();
        this.command =  new UnregisterUserServerCommand(null,
                this.clientRepository, null,
                sampleClientName);
    }

    @Test
    public void testUnregisterUserCommandReturnsNoUserFoundMessageGivenInactiveUser() {
        String result = this.command.execute().trim();

        String expected = UnregisterUserServerCommand.CLIENT_NOT_FOUND_MESSAGE;
        assertEquals("Unregister User Command allowed " +
                "to unregister a user who is not online", expected, result);
    }

    @Test
    public void testUnregisterUserCommandReturnsSuccessfullyUnregisteredMessage()
            throws ClientAlreadyExistsException {
        this.clientRepository.registerClient(sampleClient);

        String result = this.command.execute().trim();

        String expected = String.format(UnregisterUserServerCommand.CLIENT_UNREGISTERED_MESSAGE, sampleClientName);
        assertEquals("Unregister User Command did not return unregistered user message.", expected, result);
    }

    @Test
    public void testUnregisterUserCommandRemovesUserFromRepository() throws ClientAlreadyExistsException {
        this.clientRepository.registerClient(sampleClient);

        this.command.execute();

        Client result = this.clientRepository.getClient(sampleClientName);
        assertNull("Unregister User Command did not return unregistered user message.", result);
    }

}
