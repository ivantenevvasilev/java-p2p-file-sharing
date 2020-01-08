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

public class RegisterFilesServerCommandTests {
    private static String sampleClientName;
    private static Client sampleClient;
    private static Collection<String> samplePaths;

    private FileRepository fileRepository;
    private ClientRepository clientRepository;

    @BeforeClass
    public static void setUpClass() {
        final int randomPort = 4215;
        sampleClientName = "user";
        sampleClient = new Client(sampleClientName, "domain", randomPort, randomPort);
        samplePaths = List.of(
                "/home/user/Documents/lecture.pdf",
                "/usr/bin/opera",
                "/root/Downloads/virus.py"
        );
    }

    @Before
    public void setUp() {
        this.fileRepository = new InMemoryFileRepository();
        this.clientRepository = new InMemoryClientRepository();
    }

    @Test
    public void testRegisterFilesCommandReturnsSuccessfulOutputInCorrectFormat() throws ClientAlreadyExistsException {
        this.clientRepository.registerClient(sampleClient);
        ServerCommand command = new RegisterFilesServerCommand(this.fileRepository,
                this.clientRepository,
                null,
                sampleClientName,
                samplePaths);

        StringBuilder expectedStringBuilder = new StringBuilder();

        expectedStringBuilder.append(samplePaths.size());
        expectedStringBuilder.append(System.lineSeparator());

        for (String path : samplePaths) {
            expectedStringBuilder.append(String.format(RegisterFilesServerCommand.SUCCESSFULLY_ADDED_MESSAGE,
                    path,
                    sampleClientName));
            expectedStringBuilder.append(System.lineSeparator());
        }

        String expected = expectedStringBuilder.toString().trim();
        String result = command.execute();

        assertEquals("Register Files command did not produce expected format for output " +
                        "given files that weren't in repository prior",
                expected, result);
    }

    @Test
    public void testRegisterFilesCommandWithDuplicateFilesReturnsErrorMessageAndDoesNotAddAgain()
            throws ClientAlreadyExistsException {
        this.clientRepository.registerClient(sampleClient);
        ServerCommand command = new RegisterFilesServerCommand(this.fileRepository,
                this.clientRepository,
                null,
                sampleClientName,
                samplePaths);

        StringBuilder expectedStringBuilder = new StringBuilder();
        expectedStringBuilder.append(samplePaths.size());
        expectedStringBuilder.append(System.lineSeparator());
        for (String path : samplePaths) {
            expectedStringBuilder.append(String.format(RegisterFilesServerCommand.FILE_ALREADY_SHARED_MESSAGE,
                    path,
                    sampleClientName));
            expectedStringBuilder.append(System.lineSeparator());
        }

        String expected = expectedStringBuilder.toString().trim();
        command.execute();
        String result = command.execute();

        assertEquals("Register Files command did not produce expected format for output " +
                        "given files that were in repository already",
                expected,
                result);
    }

    @Test
    public void testRegisterFilesWithInactiveUserReturnsErrorMessage() {
        ServerCommand command = new RegisterFilesServerCommand(this.fileRepository,
                this.clientRepository,
                null,
                sampleClientName,
                samplePaths);

        String expected = String.format(RegisterFilesServerCommand.CLIENT_NOT_ONLINE_MESSAGE, sampleClientName);
        String result = command.execute();

        assertEquals("Register Files command allows for inactive users.",
                expected, result);
    }
}
