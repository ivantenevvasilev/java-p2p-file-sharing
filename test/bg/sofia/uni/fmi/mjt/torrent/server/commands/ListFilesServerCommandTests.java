package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import bg.sofia.uni.fmi.mjt.torrent.models.SharedFile;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.InMemoryFileRepository;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces.FileRepository;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.FileAlreadyExistsException;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ListFilesServerCommandTests {
    private static Collection<SharedFile> sampleFiles;

    private ServerCommand listFilesServerCommand;

    private FileRepository fileRepository;

    @BeforeClass
    public static void setUpClass() {
        String sampleUser = "root";
        sampleFiles = List.of(
                new SharedFile(sampleUser, "/var/something"),
                new SharedFile(sampleUser, "/bin/g++-7"),
                new SharedFile("hero132", "/home/hero/Downloads/test.bin")
        );
    }

    @Before
    public void setUp() {
        this.fileRepository = new InMemoryFileRepository();
        this.listFilesServerCommand = new ListFilesServerCommand(this.fileRepository, null, null);
    }

    @Test
    public void testListFilesCommandListsInCorrectFormat() throws FileAlreadyExistsException {
        StringBuilder expectedStringBuilder = new StringBuilder();
        for (SharedFile file : sampleFiles) {
            fileRepository.registerFile(file);
        }
        expectedStringBuilder.append(sampleFiles.size());
        expectedStringBuilder.append(System.lineSeparator());
        for (SharedFile file :  this.fileRepository.getRegisteredFiles()) {
            expectedStringBuilder.append(file.toString());
            expectedStringBuilder.append(System.lineSeparator());
        }
        String expected = expectedStringBuilder.toString().trim();
        String result = listFilesServerCommand.execute();

        assertEquals("List Files Server command does not list files in expected format.", result, expected);
    }
}
