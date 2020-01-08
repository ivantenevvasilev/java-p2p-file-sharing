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

import java.nio.file.FileAlreadyExistsException;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class UnregisterFilesServerCommandTests {
    private static String sampleUser;
    private static Collection<String> samplePaths;

    private ServerCommand unregisterFilesServerCommand;

    private FileRepository fileRepository;
    private ClientRepository clientRepository;

    @BeforeClass
    public static void setUpClass() {
        sampleUser = "testUser";
        samplePaths = List.of(
                "C:/Users/TestUser/Downloads/file.exe",
                "D:/Torrents/movie.avi",
                "F:/Documents/test.doc"
        );
    }

    @Before
    public void setUp() {
        this.clientRepository = new InMemoryClientRepository();
        this.fileRepository = new InMemoryFileRepository();
        this.unregisterFilesServerCommand = new UnregisterFilesServerCommand(this.fileRepository,
                this.clientRepository,
                null,
                sampleUser,
                samplePaths);
    }

    @Test
    public void testUnRegisterFilesShouldReturnErrorMessageGivenFilesThatWereNotShared()
            throws ClientAlreadyExistsException {
        final int samplePort = 5312;
        this.clientRepository.registerClient(new Client(sampleUser, null, samplePort, samplePort));
        StringBuilder expectedStringBuilder = new StringBuilder();

        expectedStringBuilder.append(samplePaths.size());
        expectedStringBuilder.append(System.lineSeparator());

        for (String path : samplePaths) {
            expectedStringBuilder.append(String.format(
                    UnregisterFilesServerCommand.FILE_IS_NOT_SHARED_MESSAGE,
                    path,
                    sampleUser
            ));
            expectedStringBuilder.append(System.lineSeparator());
        }

        ServerCommand command = new UnregisterFilesServerCommand(this.fileRepository,
                this.clientRepository,
                null,
                sampleUser,
                samplePaths);

        String expected = expectedStringBuilder.toString().trim();
        String result = command.execute();

        assertEquals("", expected, result);
    }

    @Test
    public void testUnregisterFilesWithRegisteredFilesShouldProduceExpectedOutput()
            throws ClientAlreadyExistsException, FileAlreadyExistsException {
        final int samplePort = 5312;
        this.clientRepository.registerClient(new Client(sampleUser, null, samplePort, samplePort));

        for (String path : samplePaths) {
            this.fileRepository.registerFile(sampleUser, path);
        }

        StringBuilder expectedStringBuilder = new StringBuilder();

        expectedStringBuilder.append(samplePaths.size());
        expectedStringBuilder.append(System.lineSeparator());

        for (String path : samplePaths) {
            expectedStringBuilder.append(String.format(
                    UnregisterFilesServerCommand.SUCCESSFULLY_REMOVED_MESSAGE,
                    path,
                    sampleUser
            ));
            expectedStringBuilder.append(System.lineSeparator());
        }

        ServerCommand command = new UnregisterFilesServerCommand(this.fileRepository,
                this.clientRepository,
                null,
                sampleUser,
                samplePaths);

        String expected = expectedStringBuilder.toString().trim();
        String result = command.execute();

        assertEquals("", expected, result);
    }

    @Test
    public void testUnregisterCommandWithUnregisteredUserShouldProduceErrorMessage() {
        String  result = this.unregisterFilesServerCommand.execute();
        String expected = String.format(UnregisterFilesServerCommand.CLIENT_NOT_ONLINE_MESSAGE, sampleUser);

        assertEquals(expected, result);
    }
}
